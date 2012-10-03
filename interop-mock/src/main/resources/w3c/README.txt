This folder contains request/responses extracted from SOAP Version 1.2 Specification Assertions and
Test Collection (Second Edition; see http://www.w3.org/TR/2007/REC-soap12-testcollection-20070427/),
with corrections for the following mistakes in the spec:

  * The response shown in XMLP-1 (second alternative) has incorrect namespace prefixes for the fault
    code and subcode values.
  * Request messages for the echoSimpleTypesAsStruct operation use inputInt as parameter name, but
    in the description the parameter is called inputInteger (see XMLP-4).

In addition, request values echoed in responses have been replaced by template variables.