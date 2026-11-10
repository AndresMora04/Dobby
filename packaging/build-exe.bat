@echo off
rem Genera Dobby.exe (ejecutable nativo de Windows) en packaging\output\Dobby.
rem No requiere tener Java instalado para USAR el .exe resultante: el Java
rem necesario queda empaquetado adentro. Para GENERARLO si se necesita tener
rem el JDK 21 instalado (jpackage viene incluido en el JDK desde la version 14).

setlocal enabledelayedexpansion
cd /d "%~dp0\.."

echo Compilando el proyecto...
call mvn -q -o package -DskipTests
if errorlevel 1 (
    echo Fallo la compilacion. Revisa los errores de arriba.
    exit /b 1
)

echo Preparando archivos...
if exist packaging\input rmdir /s /q packaging\input
mkdir packaging\input
copy /y target\dobby.jar packaging\input\dobby.jar >nul

if exist packaging\output rmdir /s /q packaging\output

echo Buscando jpackage...
set JPACKAGE=jpackage
where jpackage >nul 2>&1
if errorlevel 1 (
    set "JPACKAGE="
    for /d %%D in ("%ProgramFiles%\Java\jdk-*") do (
        if exist "%%D\bin\jpackage.exe" set "JPACKAGE=%%D\bin\jpackage.exe"
    )
    if "!JPACKAGE!"=="" (
        echo No se encontro jpackage ni en el PATH ni en "%ProgramFiles%\Java".
        echo Necesitas el JDK 21 instalado. Si lo tenes en otra carpeta, agrega
        echo su subcarpeta "bin" al PATH e intenta de nuevo.
        exit /b 1
    )
)

echo Generando el ejecutable...
"%JPACKAGE%" ^
    --type app-image ^
    --input packaging\input ^
    --main-jar dobby.jar ^
    --main-class dobby.Main ^
    --name Dobby ^
    --icon packaging\dobby.ico ^
    --dest packaging\output ^
    --app-version 1.0 ^
    --vendor "Jose Andres Mora Mora y Darien Arroyo Castro" ^
    --description "Dobby: lenguaje de programacion con tematica de Harry Potter"

if errorlevel 1 (
    echo Fallo jpackage. Verifica que tengas el JDK 21 instalado y que
    echo jpackage este en el PATH.
    exit /b 1
)

echo.
echo Listo. La carpeta packaging\output\Dobby contiene Dobby.exe.
echo Copia esa carpeta completa a donde quieras (por ejemplo, el Escritorio)
echo y hace doble clic en Dobby.exe para abrir la aplicacion.
endlocal
