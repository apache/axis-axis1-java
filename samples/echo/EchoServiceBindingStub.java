/**
 * EchoServiceBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 * 
 * This file has been hand modified for TestClient specific access.
 */

package samples.echo;

public class EchoServiceBindingStub extends org.apache.axis.client.Stub implements samples.echo.EchoServicePortType {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();
    private boolean firstCall = true;

    public String soapAction = "http://soapinterop.org/";
    public boolean addMethodToAction = false;
    public Integer timeout = new Integer(60000);

    public EchoServiceBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public EchoServiceBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public EchoServiceBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        try {
            if (service == null) {
                super.service = new org.apache.axis.client.Service();
            } else {
                super.service = service;
            }
            Class cls;
            javax.xml.rpc.namespace.QName qName;
            Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            qName = new javax.xml.rpc.namespace.QName("http://soapinterop.org/xsd", "SOAPArrayStruct");
            cachedSerQNames.add(qName);
            cls = samples.echo.SOAPArrayStruct.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.rpc.namespace.QName("http://soapinterop.org/xsd", "ArrayOfSOAPStruct");
            cachedSerQNames.add(qName);
            cls = samples.echo.SOAPStruct[].class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(arraysf);
            cachedDeserFactories.add(arraydf);

            qName = new javax.xml.rpc.namespace.QName("http://soapinterop.org/xsd", "ArrayOfstring");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(arraysf);
            cachedDeserFactories.add(arraydf);

            qName = new javax.xml.rpc.namespace.QName("http://soapinterop.org/xsd", "ArrayOfString2D");
            cachedSerQNames.add(qName);
            cls = java.lang.String[][].class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(arraysf);
            cachedDeserFactories.add(arraydf);

            qName = new javax.xml.rpc.namespace.QName("http://soapinterop.org/xsd", "SOAPStructStruct");
            cachedSerQNames.add(qName);
            cls = samples.echo.SOAPStructStruct.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.rpc.namespace.QName("http://soapinterop.org/xsd", "ArrayOffloat");
            cachedSerQNames.add(qName);
            cls = float[].class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(arraysf);
            cachedDeserFactories.add(arraydf);

            qName = new javax.xml.rpc.namespace.QName("http://soapinterop.org/xsd", "ArrayOfint");
            cachedSerQNames.add(qName);
            cls = int[].class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(arraysf);
            cachedDeserFactories.add(arraydf);

            qName = new javax.xml.rpc.namespace.QName("http://soapinterop.org/xsd", "SOAPStruct");
            cachedSerQNames.add(qName);
            cls = samples.echo.SOAPStruct.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

        }
        catch(Exception t) {
            throw org.apache.axis.AxisFault.makeFault(t);
        }
    }

    private org.apache.axis.client.Call getCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call call =
                    (org.apache.axis.client.Call) super.service.createCall();
            if (super.maintainSessionSet) {
                call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            if (firstCall) {
                firstCall = false;
                for (int i = 0; i < cachedSerFactories.size(); ++i) {
                    Class cls = (Class) cachedSerClasses.get(i);
                    javax.xml.rpc.namespace.QName qName =
                            (javax.xml.rpc.namespace.QName) cachedSerQNames.get(i);
                    Class sf = (Class)
                             cachedSerFactories.get(i);
                    Class df = (Class)
                             cachedDeserFactories.get(i);
                    call.registerTypeMapping(cls, qName, sf, df, false);
                }
            }
            return call;
        }
        catch (Throwable t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", t);
        }
    }

    public java.lang.String echoString(java.lang.String input) throws java.rmi.RemoteException{
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call call = getCall();
        call.addParameter("inputString", new javax.xml.rpc.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), javax.xml.rpc.ParameterMode.PARAM_MODE_IN);
        call.setReturnType(new javax.xml.rpc.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        call.setUseSOAPAction(true);
        String methodName = (addMethodToAction) ? "echoString" : "";
        call.setSOAPActionURI(soapAction+methodName);
        call.setTimeout(timeout);

        call.setOperationStyle("rpc");
        call.setOperationName(new javax.xml.rpc.namespace.QName("http://soapinterop.org/", "echoString"));

        Object resp = call.invoke(new Object[] {input});

        if (resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)resp;
        }
        else {
             return (java.lang.String) resp;
        }
    }

    public java.lang.String[] echoStringArray(java.lang.String[] input) throws java.rmi.RemoteException{
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call call = getCall();
        call.addParameter("inputStringArray", new javax.xml.rpc.namespace.QName("http://soapinterop.org/xsd", "ArrayOfstring"), javax.xml.rpc.ParameterMode.PARAM_MODE_IN);
        call.setReturnType(new javax.xml.rpc.namespace.QName("http://soapinterop.org/xsd", "ArrayOfstring"));
        call.setUseSOAPAction(true);
        String methodName = (addMethodToAction) ? "echoStringArray" : "";
        call.setSOAPActionURI(soapAction+methodName);

        call.setTimeout(timeout);
        call.setOperationStyle("rpc");
        call.setOperationName(new javax.xml.rpc.namespace.QName("http://soapinterop.org/", "echoStringArray"));

        Object resp = call.invoke(new Object[] {input});

        if (resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)resp;
        }
        else {
             // REVISIT THIS!
             return (java.lang.String[])org.apache.axis.utils.JavaUtils.convert(resp,java.lang.String[].class);
        }
    }

    public int echoInteger(int input) throws java.rmi.RemoteException{
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call call = getCall();
        call.addParameter("inputInteger", new javax.xml.rpc.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), javax.xml.rpc.ParameterMode.PARAM_MODE_IN);
        call.setReturnType(new javax.xml.rpc.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        call.setUseSOAPAction(true);
        String methodName = (addMethodToAction) ? "echoInteger" : "";
        call.setSOAPActionURI(soapAction+methodName);
        call.setTimeout(timeout);
        call.setOperationStyle("rpc");
        call.setOperationName(new javax.xml.rpc.namespace.QName("http://soapinterop.org/", "echoInteger"));

        Object resp = call.invoke(new Object[] {new Integer(input)});

        if (resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)resp;
        }
        else {
             return ((Integer) resp).intValue();
        }
    }

    public int[] echoIntegerArray(int[] input) throws java.rmi.RemoteException{
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call call = getCall();
        call.addParameter("inputIntegerArray", new javax.xml.rpc.namespace.QName("http://soapinterop.org/xsd", "ArrayOfint"), javax.xml.rpc.ParameterMode.PARAM_MODE_IN);
        call.setReturnType(new javax.xml.rpc.namespace.QName("http://soapinterop.org/xsd", "ArrayOfint"));
        call.setUseSOAPAction(true);
        String methodName = (addMethodToAction) ? "echoIntegerArray" : "";
        call.setSOAPActionURI(soapAction+methodName);
        call.setTimeout(timeout);
        call.setOperationStyle("rpc");
        call.setOperationName(new javax.xml.rpc.namespace.QName("http://soapinterop.org/", "echoIntegerArray"));

        Object resp = call.invoke(new Object[] {input});

        if (resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)resp;
        }
        else {
             // REVISIT THIS!
             return (int[])org.apache.axis.utils.JavaUtils.convert(resp,int[].class);
        }
    }

    public float echoFloat(float input) throws java.rmi.RemoteException{
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call call = getCall();
        call.addParameter("inputFloat", new javax.xml.rpc.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"), javax.xml.rpc.ParameterMode.PARAM_MODE_IN);
        call.setReturnType(new javax.xml.rpc.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        call.setUseSOAPAction(true);
        String methodName = (addMethodToAction) ? "echoFloat" : "";
        call.setSOAPActionURI(soapAction+methodName);
        call.setTimeout(timeout);
        call.setOperationStyle("rpc");
        call.setOperationName(new javax.xml.rpc.namespace.QName("http://soapinterop.org/", "echoFloat"));

        Object resp = call.invoke(new Object[] {new Float(input)});

        if (resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)resp;
        }
        else {
             return ((Float) resp).floatValue();
        }
    }

    public float[] echoFloatArray(float[] input) throws java.rmi.RemoteException{
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call call = getCall();
        call.addParameter("inputFloatArray", new javax.xml.rpc.namespace.QName("http://soapinterop.org/xsd", "ArrayOffloat"), javax.xml.rpc.ParameterMode.PARAM_MODE_IN);
        call.setReturnType(new javax.xml.rpc.namespace.QName("http://soapinterop.org/xsd", "ArrayOffloat"));
        call.setUseSOAPAction(true);
        String methodName = (addMethodToAction) ? "echoFloatArray" : "";
        call.setSOAPActionURI(soapAction+methodName);
        call.setTimeout(timeout);
        call.setOperationStyle("rpc");
        call.setOperationName(new javax.xml.rpc.namespace.QName("http://soapinterop.org/", "echoFloatArray"));

        Object resp = call.invoke(new Object[] {input});

        if (resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)resp;
        }
        else {
             // REVISIT THIS!
             return (float[])org.apache.axis.utils.JavaUtils.convert(resp,float[].class);
        }
    }

    public samples.echo.SOAPStruct echoStruct(samples.echo.SOAPStruct input) throws java.rmi.RemoteException{
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call call = getCall();
        call.addParameter("inputStruct", new javax.xml.rpc.namespace.QName("http://soapinterop.org/xsd", "SOAPStruct"), javax.xml.rpc.ParameterMode.PARAM_MODE_IN);
        call.setReturnType(new javax.xml.rpc.namespace.QName("http://soapinterop.org/xsd", "SOAPStruct"));
        call.setUseSOAPAction(true);
        String methodName = (addMethodToAction) ? "echoStruct" : "";
        call.setSOAPActionURI(soapAction+methodName);
        call.setTimeout(timeout);
        call.setOperationStyle("rpc");
        call.setOperationName(new javax.xml.rpc.namespace.QName("http://soapinterop.org/", "echoStruct"));

        Object resp = call.invoke(new Object[] {input});

        if (resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)resp;
        }
        else {
             return (samples.echo.SOAPStruct) resp;
        }
    }

    public samples.echo.SOAPStruct[] echoStructArray(samples.echo.SOAPStruct[] input) throws java.rmi.RemoteException{
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call call = getCall();
        call.addParameter("inputStructArray", new javax.xml.rpc.namespace.QName("http://soapinterop.org/xsd", "ArrayOfSOAPStruct"), javax.xml.rpc.ParameterMode.PARAM_MODE_IN);
        call.setReturnType(new javax.xml.rpc.namespace.QName("http://soapinterop.org/xsd", "ArrayOfSOAPStruct"));
        call.setUseSOAPAction(true);
        String methodName = (addMethodToAction) ? "echoStructArray" : "";
        call.setSOAPActionURI(soapAction+methodName);
        call.setTimeout(timeout);
        call.setOperationStyle("rpc");
        call.setOperationName(new javax.xml.rpc.namespace.QName("http://soapinterop.org/", "echoStructArray"));

        Object resp = call.invoke(new Object[] {input});

        if (resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)resp;
        }
        else {
             // REVISIT THIS!
             return (samples.echo.SOAPStruct[])org.apache.axis.utils.JavaUtils.convert(resp,samples.echo.SOAPStruct[].class);
        }
    }

    public void echoVoid() throws java.rmi.RemoteException{
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call call = getCall();
        call.setReturnType(null);
        call.setUseSOAPAction(true);
        String methodName = (addMethodToAction) ? "echoVoid" : "";
        call.setSOAPActionURI(soapAction+methodName);
        call.setTimeout(timeout);
        call.setOperationStyle("rpc");
        call.setOperationName(new javax.xml.rpc.namespace.QName("http://soapinterop.org/", "echoVoid"));

        Object resp = call.invoke(new Object[] {});

        if (resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)resp;
        }
    }

    public byte[] echoBase64(byte[] input) throws java.rmi.RemoteException{
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call call = getCall();
        call.addParameter("inputBase64", new javax.xml.rpc.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"), javax.xml.rpc.ParameterMode.PARAM_MODE_IN);
        call.setReturnType(new javax.xml.rpc.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        call.setUseSOAPAction(true);
        String methodName = (addMethodToAction) ? "echoBase64" : "";
        call.setSOAPActionURI(soapAction+methodName);
        call.setTimeout(timeout);
        call.setOperationStyle("rpc");
        call.setOperationName(new javax.xml.rpc.namespace.QName("http://soapinterop.org/", "echoBase64"));

        Object resp = call.invoke(new Object[] {input});

        if (resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)resp;
        }
        else {
             // REVISIT THIS!
             return (byte[])org.apache.axis.utils.JavaUtils.convert(resp,byte[].class);
        }
    }

    public byte[] echoHexBinary(byte[] input) throws java.rmi.RemoteException{
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call call = getCall();
        call.addParameter("inputHexBinary", new javax.xml.rpc.namespace.QName("http://www.w3.org/2001/XMLSchema", "hexBinary"), javax.xml.rpc.ParameterMode.PARAM_MODE_IN);
        call.setReturnType(new javax.xml.rpc.namespace.QName("http://www.w3.org/2001/XMLSchema", "hexBinary"));
        call.setUseSOAPAction(true);
        String methodName = (addMethodToAction) ? "echoHexBinary" : "";
        call.setSOAPActionURI(soapAction+methodName);
        call.setTimeout(timeout);
        call.setOperationStyle("rpc");
        call.setOperationName(new javax.xml.rpc.namespace.QName("http://soapinterop.org/", "echoHexBinary"));

        Object resp = call.invoke(new Object[] {new org.apache.axis.encoding.Hex(input)});

        if (resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)resp;
        }
        else {
             // REVISIT THIS!
             return (byte[])org.apache.axis.utils.JavaUtils.convert(resp,byte[].class);
        }
    }

    public java.util.Date echoDate(java.util.Date input) throws java.rmi.RemoteException{
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call call = getCall();
        call.addParameter("inputDate", new javax.xml.rpc.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), javax.xml.rpc.ParameterMode.PARAM_MODE_IN);
        call.setReturnType(new javax.xml.rpc.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        call.setUseSOAPAction(true);
        String methodName = (addMethodToAction) ? "echoDate" : "";
        call.setSOAPActionURI(soapAction+methodName);
        call.setTimeout(timeout);
        call.setOperationStyle("rpc");
        call.setOperationName(new javax.xml.rpc.namespace.QName("http://soapinterop.org/", "echoDate"));

        Object resp = call.invoke(new Object[] {input});

        if (resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)resp;
        }
        else {
             return (java.util.Date) resp;
        }
    }

    public java.math.BigDecimal echoDecimal(java.math.BigDecimal input) throws java.rmi.RemoteException{
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call call = getCall();
        call.addParameter("inputDecimal", new javax.xml.rpc.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"), javax.xml.rpc.ParameterMode.PARAM_MODE_IN);
        call.setReturnType(new javax.xml.rpc.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        call.setUseSOAPAction(true);
        String methodName = (addMethodToAction) ? "echoDecimal" : "";
        call.setSOAPActionURI(soapAction+methodName);
        call.setTimeout(timeout);
        call.setOperationStyle("rpc");
        call.setOperationName(new javax.xml.rpc.namespace.QName("http://soapinterop.org/", "echoDecimal"));

        Object resp = call.invoke(new Object[] {input});

        if (resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)resp;
        }
        else {
             return (java.math.BigDecimal) resp;
        }
    }

    public boolean echoBoolean(boolean input) throws java.rmi.RemoteException{
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call call = getCall();
        call.addParameter("inputBoolean", new javax.xml.rpc.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), javax.xml.rpc.ParameterMode.PARAM_MODE_IN);
        call.setReturnType(new javax.xml.rpc.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        call.setUseSOAPAction(true);
        String methodName = (addMethodToAction) ? "echoBoolean" : "";
        call.setSOAPActionURI(soapAction+methodName);
        call.setTimeout(timeout);
        call.setOperationStyle("rpc");
        call.setOperationName(new javax.xml.rpc.namespace.QName("http://soapinterop.org/", "echoBoolean"));

        Object resp = call.invoke(new Object[] {new Boolean(input)});

        if (resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)resp;
        }
        else {
             return ((Boolean) resp).booleanValue();
        }
    }

    public java.util.Map echoMap(java.util.Map input) throws java.rmi.RemoteException{
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call call = getCall();
        call.addParameter("inputMap", new javax.xml.rpc.namespace.QName("http://xml.apache.org/xml-soap", "Map"), javax.xml.rpc.ParameterMode.PARAM_MODE_IN);
        call.setReturnType(new javax.xml.rpc.namespace.QName("http://xml.apache.org/xml-soap", "Map"));
        call.setUseSOAPAction(true);
        String methodName = (addMethodToAction) ? "echoMap" : "";
        call.setSOAPActionURI(soapAction+methodName);
        call.setTimeout(timeout);
        call.setOperationStyle("rpc");
        call.setOperationName(new javax.xml.rpc.namespace.QName("http://soapinterop.org/", "echoMap"));

        Object resp = call.invoke(new Object[] {input});

        if (resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)resp;
        }
        else {
             return (java.util.Map) resp;
        }
    }

    public java.util.Map[] echoMapArray(java.util.Map[] input) throws java.rmi.RemoteException{
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call call = getCall();
        call.addParameter("inputMapArray", new javax.xml.rpc.namespace.QName("http://soapinterop.org/", "ArrayOf_tns2_Map"), javax.xml.rpc.ParameterMode.PARAM_MODE_IN);
        call.setReturnType(new javax.xml.rpc.namespace.QName("http://soapinterop.org/", "ArrayOf_tns2_Map"));
        call.setUseSOAPAction(true);
        String methodName = (addMethodToAction) ? "echoMapArray" : "";
        call.setSOAPActionURI(soapAction+methodName);
        call.setTimeout(timeout);
        call.setOperationStyle("rpc");
        call.setOperationName(new javax.xml.rpc.namespace.QName("http://soapinterop.org/", "echoMapArray"));

        Object resp = call.invoke(new Object[] {input});

        if (resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)resp;
        }
        else {
             // REVISIT THIS!
             return (java.util.Map[])org.apache.axis.utils.JavaUtils.convert(resp,java.util.Map[].class);
        }
    }

    public void echoStructAsSimpleTypes(samples.echo.SOAPStruct inputStruct, javax.xml.rpc.holders.StringHolder outputString, javax.xml.rpc.holders.IntHolder outputInteger, javax.xml.rpc.holders.FloatHolder outputFloat) throws java.rmi.RemoteException{
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call call = getCall();
        call.addParameter("inputStruct", new javax.xml.rpc.namespace.QName("http://soapinterop.org/xsd", "SOAPStruct"), javax.xml.rpc.ParameterMode.PARAM_MODE_IN);
        call.addParameter("outputString", new javax.xml.rpc.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), javax.xml.rpc.ParameterMode.PARAM_MODE_OUT);
        call.addParameter("outputInteger", new javax.xml.rpc.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), javax.xml.rpc.ParameterMode.PARAM_MODE_OUT);
        call.addParameter("outputFloat", new javax.xml.rpc.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"), javax.xml.rpc.ParameterMode.PARAM_MODE_OUT);
        call.setReturnType(null);
        call.setUseSOAPAction(true);
        String methodName = (addMethodToAction) ? "echoStructAsSimpleTypes" : "";
        call.setSOAPActionURI(soapAction+methodName);

        call.setTimeout(timeout);
        call.setOperationStyle("rpc");
        call.setOperationName(new javax.xml.rpc.namespace.QName("http://soapinterop.org/", "echoStructAsSimpleTypes"));

        Object resp = call.invoke(new Object[] {inputStruct});

        if (resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)resp;
        }
        else {
            java.util.Map output;
            output = call.getOutputParams();
            outputString.value = (java.lang.String) output.get("outputString");
            outputInteger.value = ((Integer) output.get("outputInteger")).intValue();
            outputFloat.value = ((Float) output.get("outputFloat")).floatValue();
        }
    }

    public samples.echo.SOAPStruct echoSimpleTypesAsStruct(java.lang.String inputString, int inputInteger, float inputFloat) throws java.rmi.RemoteException{
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call call = getCall();
        call.addParameter("inputString", new javax.xml.rpc.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), javax.xml.rpc.ParameterMode.PARAM_MODE_IN);
        call.addParameter("inputInteger", new javax.xml.rpc.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), javax.xml.rpc.ParameterMode.PARAM_MODE_IN);
        call.addParameter("inputFloat", new javax.xml.rpc.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"), javax.xml.rpc.ParameterMode.PARAM_MODE_IN);
        call.setReturnType(new javax.xml.rpc.namespace.QName("http://soapinterop.org/xsd", "SOAPStruct"));
        call.setUseSOAPAction(true);
        String methodName = (addMethodToAction) ? "echoSimpleTypesAsStruct" : "";
        call.setSOAPActionURI(soapAction+methodName);

        call.setTimeout(timeout);
        call.setOperationStyle("rpc");
        call.setOperationName(new javax.xml.rpc.namespace.QName("http://soapinterop.org/", "echoSimpleTypesAsStruct"));

        Object resp = call.invoke(new Object[] {inputString, new Integer(inputInteger), new Float(inputFloat)});

        if (resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)resp;
        }
        else {
             return (samples.echo.SOAPStruct) resp;
        }
    }

    public java.lang.String[][] echo2DStringArray(java.lang.String[][] input2DStringArray) throws java.rmi.RemoteException{
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call call = getCall();
        call.addParameter("input2DStringArray", new javax.xml.rpc.namespace.QName("http://soapinterop.org/xsd", "ArrayOfstring"), javax.xml.rpc.ParameterMode.PARAM_MODE_IN);
        call.setReturnType(new javax.xml.rpc.namespace.QName("http://soapinterop.org/xsd", "ArrayOfstring"));
        call.setUseSOAPAction(true);
        String methodName = (addMethodToAction) ? "echo2DStringArray" : "";
        call.setSOAPActionURI(soapAction+methodName);

        call.setTimeout(timeout);
        call.setOperationStyle("rpc");
        call.setOperationName(new javax.xml.rpc.namespace.QName("http://soapinterop.org/", "echo2DStringArray"));

        Object resp = call.invoke(new Object[] {input2DStringArray});

        if (resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)resp;
        }
        else {
             // REVISIT THIS!
             return (java.lang.String[][])org.apache.axis.utils.JavaUtils.convert(resp,java.lang.String[][].class);
        }
    }

    public samples.echo.SOAPStructStruct echoNestedStruct(samples.echo.SOAPStructStruct inputStruct) throws java.rmi.RemoteException{
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call call = getCall();
        call.addParameter("inputStruct", new javax.xml.rpc.namespace.QName("http://soapinterop.org/xsd", "SOAPStructStruct"), javax.xml.rpc.ParameterMode.PARAM_MODE_IN);
        call.setReturnType(new javax.xml.rpc.namespace.QName("http://soapinterop.org/xsd", "SOAPStructStruct"));
        call.setUseSOAPAction(true);
        String methodName = (addMethodToAction) ? "echoNestedStruct" : "";
        call.setSOAPActionURI(soapAction+methodName);

        call.setTimeout(timeout);
        call.setOperationStyle("rpc");
        call.setOperationName(new javax.xml.rpc.namespace.QName("http://soapinterop.org/", "echoNestedStruct"));

        Object resp = call.invoke(new Object[] {inputStruct});

        if (resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)resp;
        }
        else {
             return (samples.echo.SOAPStructStruct) resp;
        }
    }

    public samples.echo.SOAPArrayStruct echoNestedArray(samples.echo.SOAPArrayStruct inputStruct) throws java.rmi.RemoteException{
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call call = getCall();
        call.addParameter("inputStruct", new javax.xml.rpc.namespace.QName("http://soapinterop.org/xsd", "SOAPArrayStruct"), javax.xml.rpc.ParameterMode.PARAM_MODE_IN);
        call.setReturnType(new javax.xml.rpc.namespace.QName("http://soapinterop.org/xsd", "SOAPArrayStruct"));
        call.setUseSOAPAction(true);
        String methodName = (addMethodToAction) ? "echoNestedArray" : "";
        call.setSOAPActionURI(soapAction+methodName);

        call.setTimeout(timeout);
        call.setOperationStyle("rpc");
        call.setOperationName(new javax.xml.rpc.namespace.QName("http://soapinterop.org/", "echoNestedArray"));

        Object resp = call.invoke(new Object[] {inputStruct});

        if (resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)resp;
        }
        else {
             return (samples.echo.SOAPArrayStruct) resp;
        }
    }

}
