This test confirms that the WSDL2Java / serialization system does the right
thing with <sequence> ordering of XML elements (which map to bean fields).

In the WSDL file, we have a type with six ordered elements.  We build this
into a bean, then use the generated client + bean to call a test service.

The service is NOT generated, but a custom built message-style service which
simply confirms the XML looks right by walking the DOM tree until it finds
the <zero> element, then checking the siblings look right.  It returns a
boolean true/false which indicates if the run was successful.

--Glen Daniels
  July 30, 2002
