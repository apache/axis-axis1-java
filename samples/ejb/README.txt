EJBs as Web Services
=====================

Two beans are defined here.

SimpleBeanEJB is a straightfoward adaptation of everybody's favourite: 
the HelloWorld routine.

NiceThingsBeanEJB is a demonstration of using complex types. It uses NiceThings objects - each NiceThings object holds a person's favourite food, colour and lucky number.

Look in the ant build.xml file. The most relevant tasks are:

* deployejb: Compiles and deploys the ejbs. You will need to alter the deploy.dir parameter; you may also need to add vendor-specific configuration in the META-INF directory in order to be able to deploy 
the ejbs.

* deployws and undeployws: Deploy and Undeploy the web services respectively.

* simplebean and nicethingsbean: Generate wsdl files for each of the services respectively, Ant needs to find the ejb classes to do this - the easiest way to do this is to put wsejbsample.jar in your classpath