This tests whether we correctly process element declarations which are of
types (i.e. xsd:int) which would normally map to Java primitives, but have
minOccurs="0", meaning they should in fact map to the Java wrapper classes
(i.e. java.lang.Integer).  The WSDL contains both a bean containing field
declarations and a wrapped method with parameter declarations with
minOccurs="0".

Author : Glen Daniels (gdaniels@apache.org)
