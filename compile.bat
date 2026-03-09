@echo off
echo Compiling Gym Membership System...
echo.
REM Create target directory
if not exist target\classes\com\gym\gym_membership_system mkdir target\classes\com\gym\gym_membership_system
REM Compile Java files
javac -encoding UTF-8 -d target\classes src\main\java\com\gym\gym_membership_system\Member.java src\main\java\com\gym\gym_membership_system\GymConsoleApp.java
if %errorlevel% equ 0 (
    echo.
    echo [SUCCESS] Compilation completed successfully!
    echo.
    echo To run the application, use: run.bat
    echo Or manually: java -cp target\classes com.gym.gym_membership_system.GymConsoleApp
) else (
    echo.
    echo [ERROR] Compilation failed! Please check the error messages above.
)
echo.
pause
