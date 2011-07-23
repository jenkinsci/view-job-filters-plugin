call cls

call set HUDSON_PORT=80
call set HUDSON_HOME=C:\softwaredistribution\hudson\hudson-home-plugins
call set HUDSON_WAR=%HUDSON_HOME%\hudson.war
call set PLUGIN=view-job-filters

call mvn6 clean install eclipse:eclipse -Dmaven.test.skip=true -gs C:\softwaredistribution\apache-maven-2.2.1\conf\jenkins-settings.xml -Pjenkins-repos

call rmdir /S /Q "%HUDSON_HOME%\plugins\%PLUGIN%"
call copy target\%PLUGIN%.hpi "%HUDSON_HOME%\plugins"

call C:\softwaredistribution\Sun\jdk1.5.0_22\bin\java.exe -jar %HUDSON_WAR% --httpPort=%HUDSON_PORT%
