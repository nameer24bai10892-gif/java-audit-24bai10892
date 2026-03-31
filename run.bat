@echo off
REM build script for Windows

if not exist out mkdir out

echo Compiling...
javac -d out -sourcepath src src\Main.java src\model\Category.java src\model\Transaction.java src\model\Budget.java src\service\FileService.java src\service\ExpenseService.java src\util\ReportGenerator.java

if %errorlevel% == 0 (
    echo Done. Starting SmartBudget...
    echo.
    java -cp out Main
) else (
    echo Compilation failed.
)
