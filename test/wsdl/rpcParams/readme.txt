This is a simple test case to test parameter handling in an RPC/Encoded
web service. It tests two scenarios:
  1) Null parameters to an operation may be ommitted.
  2) Parameters to an operation may be sent in any order.

To accomplish this, a custom binding stub implementation is provided, since
the default wsdl2Java versions at the time of writing never reorder or omit
null parameters.

The test case was originally created to validate bug
http://nagoya.apache.org/bugzilla/show_bug.cgi?id=20930.

 - dave_marquard@forgent.com