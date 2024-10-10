@echo off
setlocal

set "work=D:\BOSY\L2\S4\Naina\Sprint-S4"

set "libpath=C:\xampp\tomcat\lib\servlet-api.jar"

set "output=%work%\src"

set "temp=%output%\temp"

copy "%output%\annotations\*.java" "%temp%"

copy "%output%\frameworks\*.java" "%temp%"

copy "%output%\util\*.java" "%temp%"

copy "%output%\controllers\*.java" "%temp%"

javac --source 17 --target 17 -cp "%libpath%" -d "%output%" "%output%\temp\*.java"

cd /d "%output%\frameworks"

jar cvf frontcontrol.jar -C "%output%\frameworks" .

copy frontcontrol.jar "%work%\jar"

del frontcontrol.jar

del "%output%\annotations\*.class"

del "%output%\frameworks\*.class"

del "%output%\util\*.class"

del "%output%\controllers\*.class"

pause
