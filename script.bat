@ECHO OFF

REM Définir les variables (modifier les valeurs entre guillemets)

SET TOMCAT_HOME=E:\xampp\tomcat
SET APP_NAME=Sprint1
SET APP_DIR=%~dp0

SET SRC_MAIN_DIR=%APP_DIR%src\main\java
SET SRC_TEST_DIR=%APP_DIR%src\test\java

SET LIB_DIR=%APP_DIR%lib
SET WEB_DIR=%APP_DIR%src\test\webapp
SET TEMP_DIR=%APP_DIR%temp
SET WEBXML_FILE=%APP_DIR%src\test\config\web.xml
SET WAR_FILE=%APP_DIR%\%APP_NAME%.war

REM Créer le répertoire d'applicaion dans Tomcat (vérifier si existant)
IF EXIST "%APP_DIR%\%APP_NAME%" (
	ECHO Le répertoire d'applicaion existe déjà. On le supprime.
	RD /S /Q "%APP_DIR%\%APP_NAME%"
)
MKDIR "%APP_DIR%\%APP_NAME%"
MKDIR "%APP_DIR%\%APP_NAME%\WEB-INF"
MKDIR "%APP_DIR%\%APP_NAME%\WEB-INF\lib"
MKDIR "%APP_DIR%\%APP_NAME%\WEB-INF\classes"

REM Créer le répertoire d'application dans Tomcat (vérifier si existant)
IF EXIST "%APP_DIR%\temp" (
	ECHO Le répertoire d'application existe déjà. On le supprime.
	RD /S /Q "%APP_DIR%\temp"
)
MKDIR "%APP_DIR%\temp"
MKDIR "%APP_DIR%temp\classes"
MKDIR "%APP_DIR%temp\java_files"

rem Itérer sur chaque fichier .java dans le répertoire source et ses sous-dossiers
for /R %SRC_MAIN_DIR% %%a in (*.java) do (
  rem echo 
  COPY /Y "%%a" "%APP_DIR%temp\java_files"
)

for /R %SRC_TEST_DIR% %%a in (*.java) do (
  echo %%a
  COPY /Y "%%a" "%APP_DIR%temp\java_files"
)

REM Compiler les classes Java
javac -parameters -cp %LIB_DIR%\* -d %APP_DIR%temp\classes %APP_DIR%temp\java_files\*.java 
REM javac -parameters -d %APP_DIR%temp\classes %APP_DIR%temp\java_files\*.java

REM Copier les classes compilés, le répertoire web et le fichier web.xml

XCOPY /S /E /Y "%APP_DIR%\temp\classes\*.*" "%APP_DIR%\%APP_NAME%\WEB-INF\classes"
XCOPY /S /E /Y "%WEB_DIR%\*.*" "%APP_DIR%\%APP_NAME%"
XCOPY /S /E /Y "%LIB_DIR%\*.*" "%APP_DIR%\%APP_NAME%\WEB-INF\lib"

COPY /Y "%WEBXML_FILE%" "%APP_DIR%\%APP_NAME%\WEB-INF"

REM Create a .war file from the temporary directory
jar cvf "%WAR_FILE%" -C "%APP_DIR%\%APP_NAME%" .

REM Copy the .war file to the webapps directory of Tomcat
COPY /Y "%WAR_FILE%" "%TOMCAT_HOME%\webapps"
@REM COPY /Y "%WAR_FILE%" "E:\xampp\tomcat\webapps"

REM Stop and start Tomcat
REM %TOMCAT_HOME%\bin\shutdown.bat
REM %TOMCAT_HOME%\bin\startup.bat
REM NET STOP Tomcat%TOMCAT_HOME%:~-2%
REM NET START Tomcat%TOMCAT_HOME%:~-2%

REM CD E:\xampp\tomcat\bin
REM shutdown.bat
REM startup.bat

@REM ECHO Deployment of te application "%APP_NAME%" completed successfully!

RD /S /Q "%WAR_FILE%"
RD /S /Q "%APP_DIR%\%APP_NAME%"
RD /S /Q "%APP_DIR%\temp"



