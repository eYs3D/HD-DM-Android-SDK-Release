package com.esp.uvc.di

import com.esp.android.usb.camera.core.USBMonitor
import com.esp.uvc.main.settings.firmware_register.*
import com.esp.uvc.main.settings.firmware_table.*
import com.esp.uvc.main.settings.firmware_version.*
import com.esp.uvc.main.settings.module_sync.*
import com.esp.uvc.main.settings.preview_settings.*
import com.esp.uvc.main.settings.sensor.*
import com.esp.uvc.ply.IMUMesh
import com.esp.uvc.ply.Mesh
import com.esp.uvc.roi_size.RoiSizeProvider
import com.esp.uvc.roi_size.RoiSizeSharedPrefsProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * appModule used within the Koin dependency framework
 * It represents the modules that will be injected via koins mechanisms
 */
val appModule = module {
    // settings -> preview_settings
    factory { (view: IPreviewSettings.View, usbMonitor: USBMonitor) -> PPreviewSettings(view, usbMonitor) as IPreviewSettings.Presenter }
    factory { (presenter: IPreviewSettings.Presenter, usbMonitor: USBMonitor) -> MPreviewSettings(presenter, usbMonitor) as IPreviewSettings.Model }
    // settings -> senor
    factory { (view: ISensor.View) -> PSensorSettings(view) as ISensor.Presenter }
    factory { (presenter: ISensor.Presenter) -> MSensorSettings(presenter) as ISensor.Model }
    // settings -> module_sync
    factory { (view: IModuleSync.View, usbMonitor: USBMonitor) -> PModuleSync(view, usbMonitor) as IModuleSync.Presenter }
    factory { (presenter: IModuleSync.Presenter, usbMonitor: USBMonitor) -> MModuleSync(presenter, usbMonitor) as IModuleSync.Model }
    // settings -> firmware_version
    factory { (view: IFirmwareVersion.View, usbMonitor: USBMonitor) -> PFirmwareVersion(view, usbMonitor) as IFirmwareVersion.Presenter }
    factory { (presenter: IFirmwareVersion.Presenter, usbMonitor: USBMonitor) -> MFirmwareVersion(presenter, usbMonitor) as IFirmwareVersion.Model }
    // settings -> firmware_table
    factory { (view: IFirmwareTable.View, usbMonitor: USBMonitor) -> PFirmwareTable(view, usbMonitor) as IFirmwareTable.Presenter }
    factory { (presenter: IFirmwareTable.Presenter, usbMonitor: USBMonitor) -> MFirmwareTable(presenter, usbMonitor) as IFirmwareTable.Model }
    // settings -> firmware_register
    factory { (view: IFirmwareRegister.View, usbMonitor: USBMonitor) -> PFirmwareRegister(view, usbMonitor) as IFirmwareRegister.Presenter }
    factory { (presenter: IFirmwareRegister.Presenter, usbMonitor: USBMonitor) -> MFirmwareRegister(presenter, usbMonitor) as IFirmwareRegister.Model }

    factory { RoiSizeSharedPrefsProvider(androidContext()) as RoiSizeProvider }
    factory { Mesh() }
    factory { IMUMesh() }
}