rem this assumes webserver is running on port 8080

@echo Deploy everything first
java org.apache.axis.client.AdminClient deploy.wsdd %*

@echo These next 3 should work...
java samples.wsa.wsaClient

@echo Now undeploy everything
java org.apache.axis.client.AdminClient undeploy.wsdd %*
