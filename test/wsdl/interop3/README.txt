These are the Round 3, Group D and E SOAP Builders interop tests.

To build, you can
1. Run the interop3 target from the top level
2. Run 'ant' in the interop3 directory
3. Run 'ant' in each of the test directories

To deploy the Group D services to Axis:
- Run 'ant deploy' in the this (the interop3) directory

To run the tests
- Execute the run.sh file with an endpoint file argument

Example:  
   ./run.sh endpoints-local
will execute each test against the services running in SimpleAxisServer

There is also a Java driver, Interop3TestCase.java.
This reads the test.properties file in the classpath and exectes the test
classes listed with the endpoint URL provided.
It should work if the test.properties file is set up correctly.

Tom Jordahl
10/14/02
