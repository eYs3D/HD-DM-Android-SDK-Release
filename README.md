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
![a-1](https://user-images.githubusercontent.com/13328289/121127927-1e40a980-c85d-11eb-9efb-7252dea1caf9.png)
5. Select "Build/Clean Project"  
![clean-project](https://user-images.githubusercontent.com/13328289/119079230-4a20fa00-ba2a-11eb-8763-17d40f030c24.png)

6. Select "Build/Make Project" to build DMPreivew  
![make-project](https://user-images.githubusercontent.com/13328289/119079468-bd2a7080-ba2a-11eb-8c90-c36291960bea.png)

7. Build DMPreview successful.
![a-2](https://user-images.githubusercontent.com/13328289/121127991-3c0e0e80-c85d-11eb-95ee-61ba3594a9ba.png)

8. The APK file will be found in the \HD-DM-Android-SDK-Release\DMPreview\app\build\outputs\apk\debug folder
![a-3](https://user-images.githubusercontent.com/13328289/121128394-e71ec800-c85d-11eb-8277-d5efda473ff7.png)

10. Connect Android device to Computer and use below command to install APK.  
     
        adb install eDepthPreview_v1.3.9.1-debug-(202106081319).apk
     
11. Launch eDepthPreview application from Android device.  

## License

This project is licensed under the [Apache License, Version 2.0](/LICENSE). Copyright 2020 eYs3D Microelectronics, Co., Ltd.
