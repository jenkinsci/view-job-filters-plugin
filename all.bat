call cls
call mvn clean install -Dmaven.test.skip=true
call rmdir /S /Q C:\Users\jacob\.hudson\plugins\view-job-filters
call copy target\view-job-filters.hpi C:\Users\jacob\.hudson\plugins
call java -jar C:\Users\jacob\workspace\hudson\main\war/target/hudson.war
rem call java -jar C:\Users\jacob\Desktop\Wikispecies\hudson-1.347.war