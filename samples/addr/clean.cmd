del Address.java
del AddressBook.java
del AddressBookService.java
del AddressBookSOAPBindingImpl.java
del AddressBookSOAPBindingSkeleton.java
del AddressBookSOAPBindingStub.java
del AddressHolder.java
del Phone.java
del PhoneHolder.java
cd ..\..
java org.apache.axis.wsdl.Wsdl2java -s samples\addr\AddressBook.wsdl -p samples.addr
cd samples\addr
