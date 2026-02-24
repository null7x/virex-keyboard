@echo off
REM ============================================
REM VERCEL BACKEND DEPLOYMENT SCRIPT
REM ============================================

cd /d "%~dp0backend"

echo.
echo 🚀 VIREX KEYBOARD - BACKEND DEPLOYMENT
echo ============================================
echo.

REM Check if npm is installed
where npm >nul 2>&1
if errorlevel 1 (
    echo ❌ npm not found. Please install Node.js first.
    echo 📥 Download: https://nodejs.org/
    pause
    exit /b 1
)

echo ✅ Node.js / npm detected
echo.

REM Check if vercel CLI is installed globally
where vercel >nul 2>&1
if errorlevel 1 (
    echo 📦 Installing Vercel CLI...
    call npm install -g vercel
    if errorlevel 1 (
        echo ❌ Failed to install Vercel CLI
        pause
        exit /b 1
    )
    echo ✅ Vercel CLI installed
) else (
    echo ✅ Vercel CLI already installed
)

echo.
echo.
echo 📋 DEPLOYMENT OPTIONS:
echo ============================================
echo.
echo 1️⃣  Deploy to production (vercel --prod)
echo 2️⃣  Link to existing project
echo 3️⃣  Show deployment guide (do it manually)
echo.

set /p choice="Choose option (1-3): "

if "%choice%"=="1" (
    echo.
    echo 🚀 Starting production deployment...
    echo.
    call vercel --prod
    if errorlevel 1 (
        echo.
        echo ⚠️  Deployment had issues. Check:
        echo    - Vercel account is logged in (run: vercel login)
        echo    - vercel.json is valid
        echo    - api/ folder has .ts files
        pause
        exit /b 1
    )
    echo.
    echo ✅ DEPLOYMENT SUCCESSFUL!
    echo 📌 Copy your deployment URL from above
    echo 📝 Update app/src/main/java/com/virex/app/di/AppModule.kt
    echo    with the BASE_URL
    echo 🔨 Rebuild APK: gradlew assembleDebug
    pause
) else if "%choice%"=="2" (
    echo.
    echo 🔗 Linking to existing Vercel project...
    echo.
    call vercel link
) else if "%choice%"=="3" (
    echo.
    echo 📖 Manual deployment guide:
    echo.
    echo 1. Go to https://vercel.com/new
    echo 2. Click "Git Repository"
    echo 3. Import ultra-keyboard repo
    echo 4. Set root directory: backend/
    echo 5. Click Deploy
    echo 6. Copy the URL
    echo 7. Update AppModule.kt with URL
    echo 8. Rebuild APK
    echo.
    echo For details, see: VERCEL_DEPLOYMENT.md
    pause
) else (
    echo ❌ Invalid choice
    pause
    exit /b 1
)

