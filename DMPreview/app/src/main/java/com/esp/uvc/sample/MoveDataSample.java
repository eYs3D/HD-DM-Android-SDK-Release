package com.esp.uvc.sample;

import android.app.Activity;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.esp.android.usb.camera.core.*;
import com.esp.android.usb.camera.core.ApcCamera;
import com.esp.android.usb.camera.core.USBMonitor;
import com.esp.uvc.R;
import com.esp.uvc.old.usbcamera.CameraDialog;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MoveDataSample extends Activity {

    private static boolean DEBUG = true;
    private static String TAG = "eys3d";

    // add product id of eys3d camera here
    private final int[] mSupportPidList = {
            0x0112,
            0x0120,
            0x0121,
            0x0137,
            0x0146,
            0x0160,
            0x0162
    };

    private USBMonitor mUSBMonitor = null;
    private ApcCamera mApcCamera = null;
    private Button button = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_data_sample);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        button = findViewById(R.id.buttonMove);
        button.setOnClickListener(mClickListener);
        button.setEnabled(false);

        mUSBMonitor = new USBMonitor(this, mOnDeviceConnectListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Register to monitor USB events.
        if (mUSBMonitor != null)
            mUSBMonitor.register();

        if (mApcCamera != null) {
            mApcCamera.destroy();
            mApcCamera = null;
        }
    }

    @Override
    public void onPause() {
        if (mApcCamera != null) {
            mApcCamera.destroy();
            mApcCamera = null;
        }

        if (mUSBMonitor != null)
            mUSBMonitor.unregister();

        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mApcCamera != null) {
            mApcCamera.destroy();
            mApcCamera = null;
        }

        if (mUSBMonitor != null) {
            mUSBMonitor.destroy();
            mUSBMonitor = null;
        }

        super.onDestroy();
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "click");
            MoveThread mthread = new MoveThread("moveThread");
            new Thread(mthread).start();
        }
    };

    private static final int CORE_POOL_SIZE = 1;
    private static final int MAX_POOL_SIZE = 4;
    private static final int KEEP_ALIVE_TIME = 10;
    protected static final ThreadPoolExecutor EXECUTER = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    private final USBMonitor.OnDeviceConnectListener mOnDeviceConnectListener = new USBMonitor.OnDeviceConnectListener() {

        @Override
        public void onAttach(final UsbDevice device) {
            final int n = mUSBMonitor.getDeviceCount();
            UsbDevice usbDevice = null;
            if (DEBUG) Log.v(TAG, ">>>> onAttach getDeviceCount:" + n);

            //Get devices list
            if(n == 1) {
                usbDevice = mUSBMonitor.getDeviceList().get(0);
            } else if(n > 1) {
                for (UsbDevice deviceInfo : mUSBMonitor.getDeviceList()) {
                    for (int pid : mSupportPidList) {
                        if (pid == deviceInfo.getProductId()) {
                            usbDevice = deviceInfo;
                            break;
                        }
                    }
                    if (usbDevice != null) break;
                }
            }

            //Request permission
            if(usbDevice == null) {
                CameraDialog.showDialog(MoveDataSample.this, mUSBMonitor);
            } else {
                mUSBMonitor.requestPermission(usbDevice);
            }
        }

        @Override
        public void onConnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock, final boolean createNew) {
            if (mApcCamera != null) {
                Log.i(TAG,"Camera already exist.");
                return;
            }
            if (DEBUG) Log.v(TAG, ">>>> onConnect UsbDevice:" + device);
            if (DEBUG) Log.v(TAG, ">>>> onConnect UsbControlBlock:" + ctrlBlock);
            if (DEBUG) Log.v(TAG, ">>>> onConnect createNew:" + createNew);
            mApcCamera = new ApcCamera();
            EXECUTER.execute(new Runnable() {
                @Override
                public void run() {
                    int openCode = mApcCamera.open(ctrlBlock);
                    if (openCode == -1){ //I/O error
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mApcCamera = null;
                                Toast.makeText(getApplicationContext(), "Failed to open Camera", Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            button.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "Open Camera successfully", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }

        @Override
        public void onDisconnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock) {
            if (DEBUG) Log.v(TAG, "onDisconnect");
            button.setEnabled(false);
            if (mApcCamera != null && device.equals(mApcCamera.getDevice())) {
                mApcCamera.close();
                mApcCamera.destroy();
                mApcCamera = null;
            }
        }

        @Override
        public void onDetach(final UsbDevice device) {
            if (DEBUG) Log.v(TAG, "onDetach");
            Toast.makeText(MoveDataSample.this, R.string.usb_device_detached, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            if (DEBUG) Log.v(TAG, "onCancel");
        }
    };

    class MoveThread implements Runnable {
        private String name;
        public MoveThread(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    button.setEnabled(false);
                    Toast.makeText(MoveDataSample.this, "Start to move data", Toast.LENGTH_SHORT).show();
                }
            });

            // 10 indexes for data
            for (int i = 0; i < 10; i++) {
                // Get data of YOffset, RecitifyTable, ZDTable and LogData
                byte[] bufferYOffset = mApcCamera.getYOffsetValue(i);
                byte[] bufferRecitify = mApcCamera.getRectifyTableValue(i);
                byte[] bufferLogData = mApcCamera.getLogDataValue(i, 0);
                int[] intArrayZD = mApcCamera.getZDTableValue(i, 0);
                byte[] bufferZD = null;
                if (intArrayZD != null) {
                    bufferZD = new byte[intArrayZD.length * 2];
                    for (int j = 0 ; j < intArrayZD.length ; j++) {
                        bufferZD[j*2 + 0] = (byte) ((intArrayZD[j] >> 8) & 0xff);
                        bufferZD[j*2 + 1] = (byte) (intArrayZD[j] & 0xff);
                    }
                }

                // Set data of YOffset, RecitifyTable, ZDTable and LogData
                if (bufferYOffset != null) {
                    mApcCamera.setYOffsetValue(bufferYOffset, i);
                }
                if (bufferRecitify != null) {
                    mApcCamera.setRectifyTableValue(bufferRecitify, i);
                }
                if (bufferLogData != null) {
                    mApcCamera.setLogDataValue(bufferLogData, i, 0);
                }
                if (bufferZD != null) {
                    mApcCamera.setZDTableValue(bufferZD, i, 0);
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    button.setEnabled(true);
                    Toast.makeText(MoveDataSample.this, "Move data done", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
}
