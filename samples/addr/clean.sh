rm Address.java
rm AddressBook.java
rm AddressBookService.java
rm AddressBookSOAPBindingImpl.java
rm AddressBookSOAPBindingSkeleton.java
rm AddressBookSOAPBindingStub.java
rm AddressHolder.java
rm Phone.java
rm PhoneHolder.java
cd ../..
java org.apache.axis.wsdl.Wsdl2java -s samples/addr/AddressBook.wsdl -p samples.addr
cd samples/addr
