This directory tests Java2WSDL generation with inheritance.

Here are the steps executed in the WSDL test suite to perform
the Java2WSDL inheritance test.

1. WSDL2Java is executed on the InheritanceTest.wsdl in this (the source) 
directory. This generates some Java files we will need in the build working
directory.
   
2. InheritancePortType.java is replaced with the version from this directory.
The local version extends the StockQuoteProvider interface in 
StockQuoteProvider.java, which is also copied over to the build working
directory.

3. The Java files in the working directory are compiled so that Java2WSDL will
have class files to use.

4. Java2WSDL is run against the InheritancePortType class.  The --all switch is
used to include inherited methods in the WSDL.  The output is sent to 
InheritanceTest.wsdl in the build working directory.

5. The class directory created in step 3 is deleted, so we start with a clean
slate.

6. WSDL2Java is run against the generated WSDL file, and the resulting Java file
are treated 'as normal' in the WSDL test suite; i.e. any Impl and TestCase files
are copied from the source tree, the service and client are compiled and the 
test invocation of the operations are executed.

Here are the addtional steps performed to set up execution of the StopExclude
test.  This test verifies the behavior of the --stopClasses and --exclude 
options.

1. The test files, Parent, Child, Baby, Parent_bean, Child_bean, and Baby_bean
are compiled to the build classes directory.

2. Java2WSDL is executed on the Baby class and the StopExclude.wsdl file is
generated in the build working directory.

3. The test case testStopExclude in the InheritanceTestCase class manually
verifies the XML in the types element of the WSDL and WSDL4J is used to
verify the operation names in the portType of the WSDL.


Tom Jordahl
Febuary 5, 2002
Note: I did not create the first test, but I did create the second StopExclude
test.
