@echo off
echo ========================================================================
echo              Running Gym Membership System Console App
echo ========================================================================
echo.
java -cp target\classes com.gym.gym_membership_system.GymConsoleApp
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Failed to run the application!
    echo Please make sure you have compiled the project first using compile.bat
)
echo.
pause
