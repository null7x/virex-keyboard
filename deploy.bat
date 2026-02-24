@echo off
echo ========================================
echo VIREX KEYBOARD - FULL DEPLOYMENT
echo ========================================
echo.

echo [1/3] Building Android APK...
cd /d "%~dp0"

if not exist gradlew.bat (
    echo Creating Gradle Wrapper...
    gradle wrapper --gradle-version=8.7 --no-daemon
)

echo Building Debug APK...
call gradlew.bat assembleDebug --no-daemon

if errorlevel 1 (
    echo ERROR: APK build failed!
    pause
    exit /b 1
)

echo.
echo ========================================
echo APK Built Successfully!
echo Location: app\build\outputs\apk\debug\app-debug.apk
echo ========================================
echo.

echo [2/3] Preparing Backend...
cd backend
if not exist node_modules (
    echo Installing dependencies...
    call npm install
)

echo.
echo [3/3] Backend Ready for Deployment
echo.
echo To deploy backend to Vercel:
echo   1. Install Vercel CLI: npm install -g vercel
echo   2. cd backend
echo   3. vercel --prod
echo.
echo Or use Vercel Dashboard: https://vercel.com/new
echo.

cd ..
echo ========================================
echo DEPLOYMENT COMPLETE!
echo ========================================
echo.
echo APK: app\build\outputs\apk\debug\app-debug.apk
echo Size: 
for %%A in ("app\build\outputs\apk\debug\app-debug.apk") do echo %%~zA bytes
echo.
echo Next Steps:
echo 1. Install APK on device: adb install app-debug.apk
echo 2. Deploy backend to Vercel
echo 3. Update BASE_URL in AppModule.kt with Vercel URL
echo.
pause
