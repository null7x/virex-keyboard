@echo off
setlocal

echo Checking APK build status...
timeout /t 10 /nobreak

cd /d "D:\github\ket\ultra-keyboard"

if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo.
    echo ========================================
    echo ✅ SUCCESS - APK BUILT!
    echo ========================================
    echo.
    dir "app\build\outputs\apk\debug\app-debug.apk"
    echo.
    echo APK Location:
    echo D:\github\ket\ultra-keyboard\app\build\outputs\apk\debug\app-debug.apk
    echo.
    
    call powershell -NoProfile -Command "Write-Host 'Size:' ((Get-Item 'app\build\outputs\apk\debug\app-debug.apk' -ErrorAction SilentlyContinue).Length / 1MB) 'MB' -ForegroundColor Green"
    
    echo.
    echo ========================================
    echo NEXT STEPS:
    echo ========================================
    echo.
    echo 1. Install APK on device:
    echo    adb install app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo 2. Enable keyboard in Settings
    echo.
    echo 3. Deploy backend to Vercel:
    echo    cd backend
    echo    vercel --prod
    echo.
    echo 4. Update BASE_URL in AppModule.kt
    echo.
    echo ========================================
) else (
    echo.
    echo ⏳ BUILD STILL IN PROGRESS...
    echo.
    echo Gradle processes running:
    tasklist | findstr java
    echo.
    echo Waiting for completion...
    echo Estimated time: 1-2 more minutes
    echo.
    echo Check again with: check-apk.bat
)

pause
