call cls

call set HUDSON_PORT=80
call set HUDSON_WAR=C:\softwaredistribution\hudson\dashboard-to-install\hudson-1.376.war
call set HUDSON_HOME=C:\softwaredistribution\qddemo
call set PLUGIN=view-job-filters

call mvn6 clean install -Dmaven.test.skip=true

call rmdir /S /Q "%HUDSON_HOME%\plugins\%PLUGIN%"
call copy target\%PLUGIN%.hpi "%HUDSON_HOME%\plugins"

call C:\softwaredistribution\Sun\jdk1.5.0_22\bin\java.exe -jar %HUDSON_WAR% --httpPort=%HUDSON_PORT%
