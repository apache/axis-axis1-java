This folder contains request/responses extracted from SOAP Version 1.2 Specification Assertions and
Test Collection (Second Edition; see http://www.w3.org/TR/2007/REC-soap12-testcollection-20070427/),
with corrections for the following mistakes in the spec:

  * The response shown in XMLP-1 (second alternative) has incorrect namespace prefixes for the fault
    code and subcode values.
  * Request messages for the echoSimpleTypesAsStruct operation use inputInt as parameter name, but
    in the description the parameter is called inputInteger (see XMLP-4).
  * The request shown in XMLP-6 has a typo (encodingstyle instead of encodingStyle).
  * The response shown in XMLP-9 has a typo (</env:value> instead of </env:Value>).
  * The response shown in XMLP-14 has a typo (<inputString> instead of </inputString>).

In addition, the following changes have been made:

  * Request values (and element names) echoed in responses (or that are irrelevant) have been
    replaced by template variables.
