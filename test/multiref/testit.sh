@echo off
echo This test assumes a server URL of http://localhost:8080/axis/servlet/
echo Deploying the multiref service...
java org.apache.axis.client.AdminClient deploy.xml %*%
echo .
echo Running demo...
java test.multiref.Main %*%
