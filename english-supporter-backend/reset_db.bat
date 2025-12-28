@echo off
echo Resetting database...
echo.

mysql -u root -pcmanh7524 < src\main\resources\db\migration\reset_database.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Database reset successfully!
    echo You can now restart the application.
) else (
    echo.
    echo Error resetting database. Please check your MySQL connection.
)

pause

