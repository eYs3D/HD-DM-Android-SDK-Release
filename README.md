# HD-DM-Android-SDK-Release
1. Download Android Studio from https://developer.android.com/studio  
![android-1](https://user-images.githubusercontent.com/13328289/119078560-05489380-ba29-11eb-9a9f-47ca57e30031.png)

2. Unpack the Android Studio distribution archive that you downloaded
     where you wish to install the program. We will refer to this
     location as your {installation home}.  
![androd-2](https://user-images.githubusercontent.com/13328289/119078593-14c7dc80-ba29-11eb-886d-de2816b4c1c6.png)

3. To start the application, open a console, cd into "{installation home}/bin" and type:

       ./studio.sh

4. Select "Open an Existing Project" to open existing DMPreview project and then select DMPreview folder which you download from GitHub.     
![android-3-open project](https://user-images.githubusercontent.com/13328289/119078997-daab0a80-ba29-11eb-8ddc-408795977cc0.png)  
![android-4-select project](https://user-images.githubusercontent.com/13328289/119079058-f0b8cb00-ba29-11eb-8382-e9588399ba26.png)  

5. Select "Build/Clean Project"  
![clean-project](https://user-images.githubusercontent.com/13328289/119079230-4a20fa00-ba2a-11eb-8763-17d40f030c24.png)

6. Select "Build/Make Project" to build DMPreivew  
![make-project](https://user-images.githubusercontent.com/13328289/119079468-bd2a7080-ba2a-11eb-8c90-c36291960bea.png)

7. Build DMPreview successful.
![android-6_build_finish](https://user-images.githubusercontent.com/13328289/119079600-fcf15800-ba2a-11eb-8bb9-b6b96a4b8583.png)

8. The APK file will be found in the /HD-DM-Android-SDK/DMPreview/app/build/outputs/apk/debug/ folder
![android_6](https://user-images.githubusercontent.com/13328289/119079850-8d2f9d00-ba2b-11eb-814c-93fd2614c9bd.png)

10. Connect Android device to Computer and use below command to install APK.  
     
        adb install eDepthPreview_v1.3.9.1-debug-(202105202041).apk
     
11. Launch eDepthPreview application from Android device.  

