#!/bin/sh
# this assumes webserver is running on port 8080

echo "Deploy everything first"
java org.apache.axis.client.AdminClient deploy.xml

echo "These next 2 should work..."
java samples.stock.GetQuote -uuser1 -wpass1 XXX
java samples.stock.GetQuote -uuser2 XXX

echo "The rest of these should fail... (nicely of course)"
java samples.stock.GetQuote XXX
java samples.stock.GetQuote -uuser1 -wpass2 XXX
java samples.stock.GetQuote -uuser3 -wpass3 XXX

echo "This should work but print debug info on the client and server"
java samples.stock.GetQuote -d -uuser1 -wpass1 XXX

# Now undeploy everything
java org.apache.axis.client.AdminClient undeploy.xml
