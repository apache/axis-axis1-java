// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.providers.java;

import com.ibm.wsdl.extensions.format.*;
import com.ibm.wsdl.extensions.java.*;
import com.ibm.wsif.*;
import com.ibm.wsif.util.*;
import com.ibm.wsif.logging.*;
import javax.wsdl.*;
import java.util.*;
import java.io.Serializable;
import java.lang.reflect.*;
/**
 * Java operation. 
 * @see WSIFPort_Java
 * @author <a href="mailto:gpfau@de.ibm.com">Gerhard Pfau</a>
 */
public class WSIFOperation_Java
    extends WSIFDefaultOperation
    implements WSIFOperation {
    protected javax.wsdl.Port fieldPortModel;
    protected WSIFPort_Java fieldPort;
    protected javax.wsdl.BindingOperation fieldBindingOperationModel;
    protected JavaOperation fieldJavaOperationModel;

    protected Method fieldMethod = null;
    protected Constructor fieldConstructor = null;
    protected String[] fieldInParameterNames = null;
    protected Map fieldFaultMessageInfos = null;
    // key: class name, value: FaultMessageInfo instance
    protected Map fieldOutParameterNames = new HashMap();
    // key: position, value: name
    protected String fieldOutputMessageName = null;
    protected String fieldInputMessageName = null;

    protected boolean fieldIsStatic = false;
    protected boolean fieldIsConstructor = false;
    protected boolean fieldTypeMapBuilt = false;

    protected Map fieldTypeMaps = new HashMap();
    private class FaultMessageInfo {
        String fieldMessageName;
        String fieldPartName;
        String fieldFormatType;
        // Note: In Java fault messages contain only one part: the Java exception

        FaultMessageInfo(String messageName, String partName, String formatType) {
            fieldMessageName = messageName;
            fieldPartName = partName;
            fieldFormatType = formatType;
        }
    }

    public WSIFOperation_Java(
        javax.wsdl.Port portModel,
        BindingOperation bindingOperationModel,
        WSIFPort_Java port)
        throws WSIFException {
        	
        TraceLogger.getGeneralTraceLogger().entry(
            new Object[] {portModel.getName(),bindingOperationModel.getName(),
           	              port.toShallowString() });

        fieldPortModel = portModel;
        fieldBindingOperationModel = bindingOperationModel;
        fieldPort = port;

        try {
            fieldJavaOperationModel =
                (JavaOperation) fieldBindingOperationModel.getExtensibilityElements().get(0);
        } catch (Exception e) {
            throw new WSIFException(
                "Unable to resolve Java binding for operation '"
                    + bindingOperationModel.getName()
                    + "'");
        }

        // Check what kind of method we have...
        String methodType = fieldJavaOperationModel.getMethodType();
        if (methodType != null) {
            if (methodType.equals("static")) {
                fieldIsStatic = true;
            } else
                if (methodType.equals("constructor")) {
                    fieldIsConstructor = true;
                } else {
                    // Assume instance method...
                }
        }
        
        TraceLogger.getGeneralTraceLogger().exit();
    }

    /**
     * Create a new copy of this object. This is not a clone, since
     * it does not copy the referenced objects as well.
     */
    public WSIFOperation_Java copy() throws WSIFException {
        return new WSIFOperation_Java(
            fieldPortModel,
            fieldBindingOperationModel,
            fieldPort);
    }

    protected static Class getClassForName(String classname) throws WSIFException {
        Class cls = null;

        if (classname == null) {
            throw new WSIFException("Error in getClassForName(): No class name specified!");
        }

        try {
            if (classname.lastIndexOf('.') == -1) {
                // Have to check for built in data types
                if (classname.equals("char")) {
                    cls = char.class;
                } else
                    if (classname.equals("boolean")) {
                        cls = boolean.class;
                    } else
                        if (classname.equals("byte")) {
                            cls = byte.class;
                        } else
                            if (classname.equals("short")) {
                                cls = short.class;
                            } else
                                if (classname.equals("int")) {
                                    cls = int.class;
                                } else
                                    if (classname.equals("long")) {
                                        cls = long.class;
                                    } else
                                        if (classname.equals("float")) {
                                            cls = float.class;
                                        } else
                                            if (classname.equals("double")) {
                                                cls = double.class;
                                            } else {
                                            	// Load the class using the Thread context's class loader
                                                cls = Class.forName(classname, true, Thread.currentThread().getContextClassLoader());
                                            }
            } else {
                cls = Class.forName(classname, true, Thread.currentThread().getContextClassLoader());
            }
        } catch (ClassNotFoundException ex) {
            throw new WSIFException("Could not instantiate class '" + classname + "'", ex);
        }

        return cls;
    }

    protected Constructor[] getConstructors() throws WSIFException {
    	Constructor[] candidates;
        if (fieldConstructor == null) {
            // Get the possible constructors with the argument classes we've found.
            Constructor[] constructors =
                fieldPort.getObjectReference().getClass().getConstructors();
            Object[] args = getMethodArgumentClasses();
            Vector possibles = new Vector();
			for (int i=0; i<constructors.length; i++)
			{
				Class[] params = constructors[i].getParameterTypes();
				if (params.length != args.length) continue;
				
				boolean match = true;
				for(int j=0; j<params.length; j++)
				{
					Object obj = args[j];
					if(obj instanceof Vector)
					{
						Vector vec = (Vector) obj;
						boolean found = false;
						for (int p=0; p<vec.size(); p++)
						{
							Class cl = (Class) vec.get(p);
							if (cl.getName().equals(params[j].getName()))
							{
								found = true;
								break;
							}
						}
						if (!found)
						{
							match = false;
							break;
						}
					}
					else
					{
						if (!((Class) obj).getName().equals(params[j].getName()))
						{
							match = false;
							break;
						} 
					}							
				}
				if (match)
				{
					possibles.addElement(constructors[i]);
				}
			}
			candidates = new Constructor[possibles.size()];
			for (int k=0; k<candidates.length; k++)
			{
				candidates[k] = (Constructor) possibles.get(k);
			}
			return candidates;
        }
        return null;
    }

    protected Map getFaultMessageInfos() throws WSIFException {
        // Get the current operation
        Operation operation = null;
        try {
            operation = getOperation();
        } catch (Exception e) {
            throw new WSIFException("Failed to get Operation", e);
        }

        if (fieldFaultMessageInfos == null) {
            fieldFaultMessageInfos = new HashMap();
        }

        BindingFault bindingFaultModel = null;
        Map bindingFaultModels = fieldBindingOperationModel.getBindingFaults();
        List parts = null;
        TypeMap typeMap = null;
        Iterator modelsIterator = bindingFaultModels.values().iterator();

        while (modelsIterator.hasNext()) {
            bindingFaultModel = (BindingFault) modelsIterator.next();
            String name = bindingFaultModel.getName();
            if (name == null) {
                throw new WSIFException("Fault name not found in binding");
            }

            Map map = operation.getFault(name).getMessage().getParts();
            if (map.size() >= 1) {
                Part part = (Part) map.values().iterator().next();
                Object formatType = fieldTypeMaps.get(part.getTypeName());
                if (formatType == null) {
                    throw new WSIFException(
                        "formatType for typeName '" + part.getName() + "' not found in document");
                }

				if (formatType instanceof Vector)
				{
					Vector types = (Vector) formatType;
					Enumeration enum = types.elements();
					while (enum.hasMoreElements())
					{
						String type = (String) enum.nextElement();
						// Add new fault message information to the map
                		fieldFaultMessageInfos.put(
                    		type,
                    		new FaultMessageInfo(name, part.getName(), type));
					}                	
				}
				else
				{
					String type = (String) formatType;
					// Add new fault message information to the map
                	fieldFaultMessageInfos.put(
                    		type,
                    		new FaultMessageInfo(name, part.getName(), type));	
				}
            }
        }

        return fieldFaultMessageInfos;
    }

    protected String getInputMessageName() throws WSIFException {
        if (fieldInputMessageName == null) {
            BindingInput bindingInputModel = fieldBindingOperationModel.getBindingInput();
            if (bindingInputModel != null) {
                fieldInputMessageName = bindingInputModel.getName();
            }
        }
        return fieldInputMessageName;
    }
    
    protected Method[] getMethods() throws WSIFException {
    	Method[] candidates;
        try {
            if (!fieldIsConstructor) {
                // Get the possible methods with the argument classes we've found.
                Method[] methods =
                    fieldPort.getObjectReference().getClass().getMethods();
                Object[] args = getMethodArgumentClasses();
                Object retClass = getMethodReturnClass();
                Vector possibles = new Vector();
				for (int i=0; i<methods.length; i++)
				{
					if (!methods[i].getName().equals(fieldJavaOperationModel.getMethodName()))
						continue;
					Class[] params = methods[i].getParameterTypes();
					if (params.length != args.length) continue;
					Class retType = methods[i].getReturnType();
					if (retClass != null && retClass instanceof Vector)
					{
						Vector vec = (Vector) retClass;
						boolean found = false;
						for (int p=0; p<vec.size(); p++)
						{
							Class cl = (Class) vec.get(p);
							if (cl.getName().equals(retType.getName()))
							{
								found = true;
								break;
							}							
						}
						if (!found) continue;
					}
					else
					{
						if (retType != null && retClass != null)
						{
							if (!((Class) retClass).getName().equals(retType.getName())) continue;
						}
					}	
					
					boolean match = true;
					for(int j=0; j<params.length; j++)
					{
						Object obj = args[j];
						if(obj instanceof Vector)
						{
							Vector vec = (Vector) obj;
							boolean found = false;
							for (int p=0; p<vec.size(); p++)
							{
								Class cl = (Class) vec.get(p);
								if (cl.getName().equals(params[j].getName()))
								{
									found = true;
									break;
								}								
							}
							if (!found)
							{
								match = false;
								break;
							}
						}
						else
						{
							if (!((Class) obj).getName().equals(params[j].getName()))
							{
								match = false;
								break;
							} 
						}							
					}
					if (match)
					{
						possibles.addElement(methods[i]);
					}
				}
				candidates = new Method[possibles.size()];
				for (int k=0; k<candidates.length; k++)
				{
					candidates[k] = (Method) possibles.get(k);
				}
				return candidates;
            }
            return null;
        } catch (WSIFException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new WSIFException(
                "Error while resolving meta information of method "
                    + fieldJavaOperationModel.getMethodName()
                    + " : The meta information is not consistent.",
                ex);
        }
    }

	private void buildTypeMap() throws WSIFException
	{
		// Only build once!
		if (fieldTypeMapBuilt) return;
		
        TypeMapping typeMapping = null;

        // Get the TypeMappings from the binding
        Iterator bindingIterator =
            this.fieldPortModel.getBinding().getExtensibilityElements().iterator();
        while (bindingIterator.hasNext()) {
            try {
                typeMapping = (TypeMapping) bindingIterator.next();
                if (typeMapping.getEncoding().equals("Java"))
                    break;
            } catch (ClassCastException exn) {
            }
        }

        if (typeMapping == null) {
            throw new WSIFException("Definition does not contain TypeMapping");
        }

        // Build the hashmap 
        bindingIterator = typeMapping.getMaps().iterator();
        while (bindingIterator.hasNext()) {
            TypeMap typeMap = (TypeMap) bindingIterator.next();
            ///////////////////////////////////
            QName typeName = typeMap.getTypeName();
            String type = typeMap.getFormatType();
            if (typeName != null && type != null)
            {
            	if (fieldTypeMaps.containsKey(typeName))
            	{
        			Vector v = new Vector();
        			v.addElement(fieldTypeMaps.get(typeName));
        			v.addElement(type);
        			this.fieldTypeMaps.put(typeName,v);

            	}
            	else
            	{
            		this.fieldTypeMaps.put(typeName,type);
            	}
            }
            else
            {
            	throw new WSIFException("Error in binding TypeMap. Key or Value is null");
            }            
        }
        fieldTypeMapBuilt = true;		
	}

    private Operation getOperation() throws Exception {
        Operation operation = null;
        
		buildTypeMap();

		// <input> and <output> tags in binding operations are not mandatory
		// so deal with null BindingInputs or BindingOutputs
		String inputName = null;
        try {
            inputName = this.fieldBindingOperationModel.getBindingInput().getName();
        } catch (NullPointerException e) {
            inputName = null;
        }

        String outputName = null;
        try {
            outputName = this.fieldBindingOperationModel.getBindingOutput().getName();
        } catch (NullPointerException e) {
            outputName = null;
        }
        
        // Build the parts list
        operation =
            this.fieldPortModel.getBinding().getPortType().getOperation(
                this.fieldBindingOperationModel.getName(),
                //this.fieldBindingOperationModel.getBindingInput().getName(),
                inputName,
                outputName);
        return operation;
    }

    protected Object getMethodReturnClass() throws WSIFException {
        Object methodReturnClass = null;
        try {
            String returnPartString = fieldJavaOperationModel.getReturnPart();
            if (returnPartString != null) {
                // A returnPart has been specified so check that this method has the correct
                // return type
                Part returnPart =
                    getOperation().getOutput().getMessage().getPart(returnPartString);

                // If there is no returnPart specified then not interested in return value
                if (returnPart != null) {
                	Object obj = this.fieldTypeMaps.get(returnPart.getTypeName());
                	if (obj instanceof Vector)
                	{
                		Vector v = (Vector) obj;
                		Vector argv = new Vector();
                		Enumeration enum = v.elements();
                		while (enum.hasMoreElements())
                		{
                			String cls = (String) enum.nextElement();
                			argv.addElement(getClassForName(cls));
                		}
                		methodReturnClass = argv;
                	}
                	else
                	{
                		methodReturnClass =
                        	getClassForName((String) fieldTypeMaps.get(returnPart.getTypeName()));
                	}                    
                } else {
                    // If we get here then the return part specified on the java operation was not
                    // in the output message
                    throw new Exception(
                        "returnPart '" + returnPartString + "' was not in the output message");
                }
            }
            // returnPart attribute was not present so return methodReturnClass as default null
        } catch (Exception ex) {
            throw new WSIFException(
                "Error while determining return class of method "
                    + fieldJavaOperationModel.getMethodName()
                    + " : The meta information is not consistent.",
                ex);
        }

        return methodReturnClass;
    }

    protected Object[] getMethodArgumentClasses() throws WSIFException {
        Object[] methodArgClasses = null;
        try {

            Operation operation = getOperation();

            /*
            The order of the parameters as passed to the Java method is
            determined in this way:
            1. from the <wsdl:operation parameterOrder="xxx yyy"> attribute if present
            2. from the <java:operation parameterOrder="xxx yyy"> attribute if present
            3. from the order the parts are specified in the input message
            */

            // Get the parameter order according to the above rules
            List parameterOrder = null;

            parameterOrder = fieldJavaOperationModel.getParameterOrder();

            if (parameterOrder == null) {
                parameterOrder = operation.getParameterOrdering();
            }

            /*
            The MessageImpl actually the order the parts were added in a List, but
            this can only be accessed via the getOrderedParts() methods which
            of course returns a List of Parts!
            So here I (rather inefficiently) convert the list of parts to a list of
            string part names.
            */
            if (parameterOrder == null) {
                List partList = operation.getInput().getMessage().getOrderedParts(null);
                parameterOrder = new Vector();
                Iterator partListIterator = partList.iterator();
                while (partListIterator.hasNext()) {
                    Part part = (Part) partListIterator.next();
                    parameterOrder.add(part.getName());
                }
            }

            /*
            	Operations do not specify whether they are to be used with RPC-like bindings 
            	or not. However, when using an operation with an RPC-binding, it is useful to 
            	be able to capture the original RPC function signature. For this reason, 
            	a request-response or solicit-response operation MAY specify a list of parameter
            	names via the parameterOrder attribute (of type nmtokens). The value of the 
            	attribute is a list of message part names separated by a single space. 
            	The value of the parameterOrder attribute MUST follow the following rules:
            
                * The part name order reflects the order of the parameters in the RPC signature
                * The return value part is not present in the list
                * If a part name appears in both the input and output message, it is an in/out parameter
            	* If a part name appears in only the input message, it is an in parameter
            	* If a part name appears in only the output message, it is an out parameter
            
            	Note that this information serves as a "hint" and may safely be ignored by 
            	those not concerned with RPC signatures. Also, it is not required to be present, 
            	even if the operation is to be used with an RPC-like binding.
            */

            ArrayList argNames = new ArrayList();
            ArrayList argTypes = new ArrayList();

            Iterator parameterIterator = parameterOrder.iterator();
            while (parameterIterator.hasNext()) {
                String param = (String) parameterIterator.next();
                Part part = (Part) operation.getInput().getMessage().getPart(param);
                if (part == null) {
                    part = (Part) operation.getOutput().getMessage().getPart(param);
                }
                if (part == null)
                    throw new Exception(
                        "Part '"
                            + param
                            + "' from parameterOrder not found in input or output message");
                argNames.add((String) part.getName());

                // should also check for the element
                QName partType = part.getTypeName();
                Object obj = this.fieldTypeMaps.get(partType);
                if (obj instanceof Vector)
                {
                	Vector v = (Vector) obj;
                	Vector argv = new Vector();
                	Enumeration enum = v.elements();
                	while (enum.hasMoreElements())
                	{
                		String cls = (String) enum.nextElement();
                		argv.addElement(getClassForName(cls));
                	}
                	argTypes.add(argv);
                }
                else
                {
                	argTypes.add(getClassForName((String) this.fieldTypeMaps.get(partType)));
                }
                
            }

            methodArgClasses = new Object[argTypes.size()];
            for (int i = 0; i < argTypes.size(); i++) {
                methodArgClasses[i] = argTypes.get(i);
            }

            fieldInParameterNames = new String[argNames.size()];
            for (int i = 0; i < argNames.size(); i++) {
                fieldInParameterNames[i] = (String) argNames.get(i);
            }

            // Deal with output parts if operation is Request-Response
            if (operation.getStyle().equals(OperationType.REQUEST_RESPONSE)) {
                argNames = new ArrayList();
                    Iterator outputPartsIterator =
                        operation.getOutput().getMessage().getOrderedParts(null).iterator();
                    while (outputPartsIterator.hasNext()) {
                    Part part = (Part) outputPartsIterator.next();
                        argNames.add((String) part.getName());
                        }

                for (int i = 0; i < argNames.size(); i++) {
                    fieldOutParameterNames.put(Integer.toString(i), (String) argNames.get(i)); }
            }
        } catch (Exception ex) {
            throw new WSIFException(
                "Error while determining signature of method "
                    + fieldJavaOperationModel.getMethodName()
                    + " : The meta information is not consistent.",
                ex);
                }

        return methodArgClasses;
    }

    // Turns an array of arguments into a form compatible with a method
    // If they are compatible, the object array is populated
    // otherwise returns null
    protected Object[] getCompatibleArguments(Class[] parmTypes, Object[] args) {
    	// Go through each argument checking it's compatability with the method arg
    	// creating a compatible set along the way.
    	// In essence this just converts from String to Character when necessary
    	// if there are further special case classes such as these which are dependent
    	// on the object value, PUT THEM HERE :-)
    	if (args == null || parmTypes == null)
    	{
    		Object[] compatibleArgs = new Object[0];
    		return compatibleArgs;
    	}
    	
    	Object[] compatibleArgs = new Object[args.length];
    	for (int i=0; i<parmTypes.length; i++) {
    		// If the arg is a null then skip it
    		if (args[i] == null) {
    			compatibleArgs[i] = null;
    			continue;
    		}
    		// Consider the special cas, squeezing a String into a Character
    		Object convertedArg = getCompatibleObject(parmTypes[i], args[i]);
    		if (convertedArg == null) {
    			// can't convert one of the arguments so return null
    			return null;
    		} else {
    			compatibleArgs[i] = convertedArg;
    		}
    		
    	}
    	return compatibleArgs;
    }

    protected Object getCompatibleReturn(Method method, Object returnObj) {
    	if (method.getReturnType().equals(java.lang.Character.class)) {
    		return getCompatibleObject(java.lang.String.class, returnObj);
    	} else {
    		return returnObj;
    	}
    }
    
    // Usually cls1.isAssignableFrom(cls2) returning false means you can't cast 
    // instance of cls1 to cls2. There are some special cases we need to cover ...
    // String->Character and Character->String
    // If a conversion is known then the obj is converted to class cls
    //   if that conversion failed, null is returned
    //   else the converted obj is returned
    // If a conversion is not known about then the obj is returned
    // Note: if you are adding other cases ensure you add both directions since the
    //       this conversion may be needed on method args AND returns
    protected Object getCompatibleObject(Class cls, Object obj) {
    	// String -> Character
	   	if ( cls.equals(java.lang.Character.class)
    	  && obj.getClass().equals(java.lang.String.class)) {
    	 	Character charArg = stringToCharacter((String)obj);
    	 	if (charArg == null) {
    	 		// Can't convert this string to character so return null
    	 		return null;
    	 	}
    	 	return charArg;
    	}
    	   	
    	if ( cls.equals(java.lang.String.class)
    	  && obj.getClass().equals(java.lang.Character.class)) {
    	  	return (obj.toString());
    	}  
    	
    	return obj;
    }
    
    protected Character stringToCharacter(String str) {
    	if (str.length() != 1) return null;
    	return new Character(str.charAt(0));
    }

    protected String getOutputMessageName() throws WSIFException {
        if (fieldOutputMessageName == null) {
            BindingOutput bindingOutputModel =
                fieldBindingOperationModel.getBindingOutput();
                if (bindingOutputModel != null) {
                fieldOutputMessageName = bindingOutputModel.getName();
            }
        }
        return fieldOutputMessageName;
    }

    /**
     * Emulates asynchronous operation by executing a
     * requestResponseOperation on a seperate thread.
     * ???ant??? Doesn't need to use the CorrelationService
     *           or Listener so is there much point?
     * @param input   input message to send to the operation
     * @param handler   the response handler that will be notified 
     *             when the asynchronous response becomes available.
     *
     * @return the correlation ID or the request. The correlation ID
     *         is used to associate the request with the WSIFOperation.
     *
     * @exception WSIFException if something goes wrong.
     * @see WSIFOperation#executeRequestResponseAsync()
     * ???ant??? need to think about the copy of the WSIFOp
     * @see WSIFOperation#executeRequestResponseAsync()
     */

/* ***************Not supported yet
    
    public Serializable executeRequestResponseAsync(final WSIFMessage input,
                                                  final WSIFResponseHandler handler)
                                                  throws WSIFException {
       // spawn a new thread to process the request  
       final WSIFOperation_Java copiedOp = this.copy();
       Thread t = new Thread() {
          public void run() {
             copiedOp.processAsyncRequest( input, handler );
		  }
	   };
       t.setName( "WSIFOperation_Java async request processor" );
       t.start();
       return null; // ???ant??? need a correlation ID?
    }

    private void processAsyncRequest(WSIFMessage inMsg, WSIFResponseHandler handler) {
       WSIFMessage outMsg = createOutputMessage();
       WSIFMessage faultMsg = createFaultMessage();
       try {
          executeRequestResponseOperation( inMsg, outMsg, faultMsg );
       } catch (Exception ex) {
          //???ant???what to do with this?
       }    
       handler.executeAsyncResponse(outMsg, faultMsg);
    }

    public boolean isAsyncSupported() {
       return true;
    }

*/

    public boolean executeRequestResponseOperation(
        WSIFMessage input,
        WSIFMessage output,
        WSIFMessage fault)
        throws WSIFException {
        	
        TraceLogger.getGeneralTraceLogger().entry(
            new Object[] { input, output, fault });

        boolean operationSucceeded = true;
        boolean foundInputParameter = false;
        boolean usedOutputParam = false;
        
        try {
            Object result = null;
            Method[] methods = null;
            Constructor[] constructors = null;
            // Need to get the stuff here because this also initializes fieldInParameterNames
            if (fieldIsConstructor) {
                constructors = getConstructors();
                if (constructors.length <= 0)
                	throw new WSIFException("No constructor found that match the parts specified");
            } else {
                methods = getMethods();
                if (methods.length <= 0)
                	throw new WSIFException("No method named '"
                		+ fieldJavaOperationModel.getMethodName()
                		+"' found that match the parts specified");
            }

            Object[] arguments = null;
			Object part = null;
            if ((fieldInParameterNames != null) && (fieldInParameterNames.length > 0)) {
                arguments = new Object[fieldInParameterNames.length];
                for (int i = 0; i < fieldInParameterNames.length; i++) {
                    part = input.getObjectPart(fieldInParameterNames[i]);
                    if (part != null) {
                        arguments[i] = part;
                        foundInputParameter = true;
                    } else {
                    	String paramName = fieldInParameterNames[i]; 
                    	Iterator partsIterator = input.getPartNames();
                    	while (partsIterator.hasNext()) {
                    		String partName = (String) partsIterator.next();
                    		if (partName == null || paramName == null) break;
                    		if (partName.equals(paramName)) {
                    			arguments[i] = null;
                    			foundInputParameter = true;
                    		}
                    	}
                    	if (!foundInputParameter) {
	                        if (fieldOutParameterNames.size() > 0) {
	                            String outParameterName = null;
	                            for (int j = 1; j <= arguments.length; j++) {
	                                outParameterName = (String) fieldOutParameterNames.get(Integer.toString(j));
	                                if ((outParameterName != null)
	                                    && (outParameterName.equals(fieldInParameterNames[i]))) {
	                                    arguments[i] = (methods[0].getParameterTypes()[i]).newInstance();
	                                    foundInputParameter = true;
	                                    usedOutputParam = true;
	                                }
	                            }
	                        }
                    	}
                    }
                    if (!foundInputParameter) {
                        throw new WSIFException(
                            this +" : Could not set input parameter '" + fieldInParameterNames[i] + "'");
                    }
                }
            }

			boolean invokedOK = false;
            if (fieldIsConstructor) {
            	for (int a=0; a<constructors.length; a++)
            	{
            		try
            		{
            			// Get a set of arguments which are compatible with the ctor
						Object[] compatibleArguments = getCompatibleArguments(constructors[a].getParameterTypes(), arguments);
						// If we didn't get any arguments then the parts aren't compatible with the ctor
						if (compatibleArguments == null) break;
						// Parts are compatible so invoke the ctor with the compatible set
            			result = constructors[a].newInstance(compatibleArguments);
                    	fieldPort.setObjectReference(result);
                    	invokedOK = true;
                    	break;
            		}
            		catch(IllegalArgumentException ia)
            		{
            			// Ingore and try next constructor
            		}
            	}
            	if (!invokedOK)
            		throw new WSIFException("Failed to call constructor for object in Java operation");
                // Side effect: Initialize port's object reference
            } else {
                if (fieldIsStatic) {
                	for (int a=0; a<methods.length; a++)
	            	{
	            		if (usedOutputParam)
		            	{
      		                for (int i = 0; i < fieldInParameterNames.length; i++) {
			            		String outParameterName = null;
			                    for (int j = 1; j <= arguments.length; j++) {
			                        outParameterName = (String) fieldOutParameterNames.get(Integer.toString(j));
			                        if ((outParameterName != null)
			                            && (outParameterName.equals(fieldInParameterNames[i]))) {
			                            arguments[i] = (methods[a].getParameterTypes()[i]).newInstance();
			                        }
			                    }
      		                }
		            	}
	            		try
	            		{
			      			// Get a set of arguments which are compatible with the method
							Object[] compatibleArguments = getCompatibleArguments(methods[a].getParameterTypes(), arguments);
							// If we didn't get any arguments then the parts aren't compatible with the method
							if (compatibleArguments == null) break;
							// Parts are compatible so invoke the method with the compatible set
	            			result = methods[a].invoke(null, compatibleArguments);
	            			fieldMethod = methods[a];
	            			invokedOK = true;
	            			break;
	            		}
	            		catch(IllegalArgumentException ia)
	            		{
	            			// Ingore and try next method
            			}
	            	}
            		if (!invokedOK)
            			throw new WSIFException("Failed to invoke method '"
            				+fieldJavaOperationModel.getMethodName()+"'");                    
                } else {
                	for (int a=0; a<methods.length; a++)
	            	{
	            		if (usedOutputParam)
		            	{
      		                for (int i = 0; i < fieldInParameterNames.length; i++) {
			            		String outParameterName = null;
			                    for (int j = 1; j <= arguments.length; j++) {
			                        outParameterName = (String) fieldOutParameterNames.get(Integer.toString(j));
			                        if ((outParameterName != null)
			                            && (outParameterName.equals(fieldInParameterNames[i]))) {
			                            arguments[i] = (methods[a].getParameterTypes()[i]).newInstance();
			                        }
			                    }
      		                }
		            	}
	            		try
	            		{
	            			// Get a set of arguments which are compatible with the method
							Object[] compatibleArguments = getCompatibleArguments(methods[a].getParameterTypes(), arguments);
							// If we didn't get any arguments then the parts aren't compatible with the method
							if (compatibleArguments == null) break;
							// Parts are compatible so invoke the method with the compatible set
	            			result = methods[a].invoke(fieldPort.getObjectReference(), compatibleArguments);
	            			fieldMethod = methods[a];
	            			invokedOK = true;
	            			break;
	            		}
	            		catch(IllegalArgumentException ia)
	            		{
	            			// Ingore and try next method
            			}
	            	}
            		if (!invokedOK)
            			throw new WSIFException("Failed to invoke method '"
            				+fieldJavaOperationModel.getMethodName()+"'");                     
                } 

                String outParameterName = null;
                if (fieldOutParameterNames.size() > 0) {
                    output.setName(getOutputMessageName());
                    outParameterName = (String) fieldOutParameterNames.get("0");
                    if (outParameterName != null) {
                        output.setObjectPart(outParameterName, getCompatibleReturn(fieldMethod, result));
                        // Should we use the class of the method signature instead here ?
                    }

                    if (arguments != null) {
                        for (int i = 1; i <= arguments.length; i++) {
                            outParameterName = (String) fieldOutParameterNames.get(Integer.toString(i));
                            if (outParameterName != null) {
 								output.setObjectPart(outParameterName, arguments[i - 1]);
                            }
                        }
                    }
                }
            }
        } catch (InvocationTargetException ex) {
            Throwable invocationFault = ex.getTargetException();
            String className = invocationFault.getClass().getName();
            Map faultMessageInfos = getFaultMessageInfos();
            FaultMessageInfo faultMessageInfo =
                (FaultMessageInfo) faultMessageInfos.get(className);
            if ((faultMessageInfo != null)
                && (faultMessageInfo.fieldPartName != null)) { // Found fault
				Object faultPart = invocationFault;                // Should we use the class of the method signature here ?
				fault.setObjectPart(faultMessageInfo.fieldPartName, faultPart);
                fault.setName(faultMessageInfo.fieldMessageName);
                operationSucceeded = false;
            } else {
                // Try to find a matching class:
                Class invocationFaultClass = invocationFault.getClass();
                Class tempClass = null;
                Iterator it = faultMessageInfos.values().iterator();
                boolean found = false;
                while (it.hasNext()) {
                    faultMessageInfo = (FaultMessageInfo) it.next();
                    try {
                        tempClass = Class.forName(faultMessageInfo.fieldFormatType, true, Thread.currentThread().getContextClassLoader());
                        if (tempClass.isAssignableFrom(invocationFaultClass)) {
                            found = true;
                            Object faultPart = invocationFault;
                            // Should we use the class of the method signature here ?
							fault.setObjectPart(faultMessageInfo.fieldPartName, faultPart);
                            fault.setName(faultMessageInfo.fieldMessageName);
                            operationSucceeded = false;
                        }
                    } catch (Exception exc) { // Nothing to do - just try the next one...
                    }
                }
                if (!found) {
                    throw new WSIFException("Operation failed!", invocationFault);
                }
            }
        } catch (Exception ex) {
        	// Log message
      		MessageLogger messageLog =
      		MessageLogger.newMessageLogger("WSIF", "com.ibm.wsif.catalog.Messages");
      		messageLog.message(
        	WSIFConstants.TYPE_ERROR,
        	"WSIF.0005E",
        	new Object[] { "Java", fieldJavaOperationModel.getMethodName()});
      		messageLog.destroy();
      		// End message
        
            throw new WSIFException(
                this +" : Could not invoke '" + fieldJavaOperationModel.getMethodName() + "'",
                ex);
        }

        TraceLogger.getGeneralTraceLogger().exit(new Boolean(operationSucceeded));
        return operationSucceeded;
    }

    public void executeInputOnlyOperation(WSIFMessage input) throws WSIFException {

        TraceLogger.getGeneralTraceLogger().entry(input);

        boolean foundInputParameter = false;
        try {
            Object result = null;
            Method[] methods = null;
            Constructor[] constructors = null;
            // Need to get the stuff here because this also initializes fieldInParameterNames
            if (fieldIsConstructor) {
                constructors = getConstructors();
                if (constructors.length <= 0)
                	throw new WSIFException("No constructors found that match the parts specified");
            } else {
                methods = getMethods();
                if (methods.length <= 0)
                	throw new WSIFException("No methods named '"
                		+ fieldJavaOperationModel.getMethodName()
                		+"' found that match the parts specified");
            }

            Object[] arguments = null;
            Object part = null;
            if ((fieldInParameterNames != null) && (fieldInParameterNames.length > 0)) {
                arguments = new Object[fieldInParameterNames.length];
                for (int i = 0; i < fieldInParameterNames.length; i++) {
                    part = input.getObjectPart(fieldInParameterNames[i]);
                    if (part != null) {
                        arguments[i] = part;
                        foundInputParameter = true;
                    }		
                    else {
						String paramName = fieldInParameterNames[i];
                    	Iterator partsIterator = input.getPartNames();
                    	while (partsIterator.hasNext()) {
                    		Object partName = (Object) partsIterator.next();
                    		if (partName == null || paramName == null) break;
                    		if (partName.equals(paramName)) {
                    			arguments[i] = part;
                    			foundInputParameter = true;
                    		}
                    	}
                    }

                    if (!foundInputParameter) {
                        throw new WSIFException(
                            this +" : Could not set input parameter '" + fieldInParameterNames[i] + "'");
                    }
                }
            }

			boolean invokedOK = false;
			
            if (fieldIsConstructor) {
            	for (int a=0; a<constructors.length; a++)
            	{
            		try
            		{
            			// Get a set of arguments which are compatible with the ctor
						Object[] compatibleArguments = getCompatibleArguments(constructors[a].getParameterTypes(), arguments);
						// If we didn't get any arguments then the parts aren't compatible with the ctor
						if (compatibleArguments == null) break;
						// Parts are compatible so invoke the ctor with the compatible set
            			result = constructors[a].newInstance(compatibleArguments);
                    	fieldPort.setObjectReference(result);
                    	invokedOK = true;
                    	break;
            		}
            		catch(IllegalArgumentException ia)
            		{
            			// Ignore and try next constructor
            		}
            	}
            	if (!invokedOK)
            		throw new WSIFException("Failed to call constructor for object in Java operation");
            } else {
                if (fieldIsStatic) {
                	for (int a=0; a<methods.length; a++)
	            	{
	            		try
	            		{
	            			// Get a set of arguments which are compatible with the method
							Object[] compatibleArguments = getCompatibleArguments(methods[a].getParameterTypes(), arguments);
							// If we didn't get any arguments then the parts aren't compatible with the method
							if (compatibleArguments == null) break;
							// Parts are compatible so invoke the method with the compatible set
	            			result = methods[a].invoke(null, compatibleArguments);
	            			fieldMethod = methods[a];
	            			invokedOK = true;
	            			break;
	            		}
	            		catch(IllegalArgumentException ia)
	            		{
	            			// Ignore and try next method
            			}
	            	}
            		if (!invokedOK)
            			throw new WSIFException("Failed to invoke method '"
            				+fieldJavaOperationModel.getMethodName()+"'");                    
                } else {
                	for (int a=0; a<methods.length; a++)
	            	{
	            		try
	            		{
	            			// Get a set of arguments which are compatible with the method
							Object[] compatibleArguments = getCompatibleArguments(methods[a].getParameterTypes(), arguments);
							// If we didn't get any arguments then the parts aren't compatible with the method
							if (compatibleArguments == null) break;
							// Parts are compatible so invoke the method with the compatible set
	            			result = methods[a].invoke(fieldPort.getObjectReference(), compatibleArguments);
							fieldMethod = methods[a];
	            			invokedOK = true;
	            			break;
	            		}
	            		catch(IllegalArgumentException ia)
	            		{
	            			// Ignore and try next method
            			}
	            	}  
            		if (!invokedOK)
            			throw new WSIFException("Failed to invoke method '"
            				+fieldJavaOperationModel.getMethodName()+"'");	            	                   
                } 
            }
        } catch (InvocationTargetException ex) {
        	// Log message
      		MessageLogger messageLog =
      		MessageLogger.newMessageLogger("WSIF", "com.ibm.wsif.catalog.Messages");
      		messageLog.message(
        	WSIFConstants.TYPE_ERROR,
        	"WSIF.0005E",
        	new Object[] { "Java", fieldJavaOperationModel.getMethodName()});
      		messageLog.destroy();
      		// End message
      		
            throw new WSIFException(
                this
                    + " : Invocation of '"
                    + fieldJavaOperationModel.getMethodName()
                    + "' failed.",
                ex);
        } catch (Exception ex) {
        	// Log message
      		MessageLogger messageLog =
      		MessageLogger.newMessageLogger("WSIF", "com.ibm.wsif.catalog.Messages");
      		messageLog.message(
        	WSIFConstants.TYPE_ERROR,
        	"WSIF.0005E",
        	new Object[] { "Java", fieldJavaOperationModel.getMethodName()});
      		messageLog.destroy();
      		// End message
            throw new WSIFException(
                this +" : Could not invoke '" + fieldJavaOperationModel.getMethodName() + "'",
                ex);
        }
                
        TraceLogger.getGeneralTraceLogger().exit();
    }
        
	/**
	 * Creates a new input WSIFMessage. This overrides the 
	 * WSIFDefaultOperation method to enable the use of 
	 * compiled WSIFMessages by using a WSIFMessageFactory
	 * to create the message.
	 * 
	 * @param name   the name of the message
	 * @return a WSIFMessage instance
	 * @see WSIFOperation#createInputMessage(String)
	 */
	public WSIFMessage createInputMessage(String name) {
		Definition d = ( fieldPort == null ) ? null : fieldPort.getDefinition();
		String ns = ( d == null ) ? "" : d.getTargetNamespace();
		WSIFMessageFactory mf = WSIFServiceImpl.getMessageFactory();
		WSIFMessage msg = mf.createMessage( ns, name + "Message" );
		if (msg!=null) msg.setName(name);
		return msg;
	}

	/**
	 * Creates a new output WSIFMessage. This overrides the 
	 * WSIFDefaultOperation method to enable the use of 
	 * compiled WSIFMessages by using a WSIFMessageFactory
	 * to create the message.
	 * 
	 * @param name   the name of the message
	 * @return a WSIFMessage instance
	 * @see WSIFOperation#createInputMessage(String)
	 */
	public WSIFMessage createOutputMessage(String name) {
		Definition d = ( fieldPort == null ) ? null : fieldPort.getDefinition();
		String ns = ( d == null ) ? "" : d.getTargetNamespace();
		WSIFMessageFactory mf = WSIFServiceImpl.getMessageFactory();
		WSIFMessage msg = mf.createMessage( ns, name + "Message" );
		if (msg!=null) msg.setName(name);
		return msg;
	}
	
   public String toString() {
   	 String buff=new String(super.toString()+":\n");
   	 if (fieldPortModel != null) {
   	 	buff += "portModel:"+fieldPortModel.getName();
   	 }
   	 else {
   	 	buff += "portModel:null";
   	 }
   	 if (fieldPort != null) {
   	 	buff += " wsifPort_Java:"+fieldPort.toShallowString();
   	 }
   	 else {
   	 	buff += " wsifPort_Java:null";
   	 }
   	 if (fieldBindingOperationModel != null) {
   	 	buff += " bindingOperationModel:"+fieldBindingOperationModel.getName();
   	 }
   	 else {
   	 	buff += " bindingOperationModel:null";
   	 }

   	 buff += " JavaOperation:"+fieldJavaOperationModel;
   	 buff += " method:"+fieldMethod;
   	 buff += " constructor:"+fieldConstructor;

     if (fieldInParameterNames==null)
     {
       buff += " inParameterNames:null";
     }
     else
     {   	 
   	   buff += " inParameterNames: size:"+fieldInParameterNames.length;
   	   for (int i=0; i<fieldInParameterNames.length; i++)
   	     buff += " inParameterNames["+i+"]:"+fieldInParameterNames[i];
     }
   	   
     if (fieldOutParameterNames==null)
     {
       buff += " outParameterNames:null";
     }
     else
     {   	 
   	   buff += " outParameterNames: size:"+fieldOutParameterNames.size();
   	   Iterator it=fieldOutParameterNames.keySet().iterator();
   	   int i=0;
   	   while (it.hasNext())
   	   {
   	     String key=(String)it.next();
   	     buff += " outParameterNames["+i+"]:"+key+
   	             " "+fieldOutParameterNames.get(key);
   	     i++;
   	   }
     }
   	 
     if (fieldFaultMessageInfos==null)
     {
       buff += " faultMessageInfos:null";
     }
     else
     {   	 
   	   Iterator it=fieldFaultMessageInfos.keySet().iterator();
   	   int i=0;
   	   while (it.hasNext())
   	   {
   	     String key=(String)it.next();
   	     buff += " faultMessageInfos["+i+"]:"+key+
   	             " "+fieldFaultMessageInfos.get(key);
   	     i++;
   	   }
     }
   	 
   	 buff += " outputMessageName:"+fieldOutputMessageName;
   	 buff += " inputMessageName:"+fieldInputMessageName;
   	 buff += " isStatic:"+fieldIsStatic;
   	 buff += " isConstructor:"+fieldIsConstructor;
   	 
     if (fieldTypeMaps==null)
     {
       buff += " faultTypeMaps:null";
     }
     else
     {   	 
   	   Iterator it=fieldTypeMaps.keySet().iterator();
   	   int i=0;
   	   while (it.hasNext())
   	   {
   	     QName key=(QName)it.next();
   	     buff += " typeMaps["+i+"]:"+key+
   	             " "+fieldTypeMaps.get(key);
   	     i++;
   	   }
     }
   	 
  	 return buff;
  }
  
  public String toShallowString() { return super.toString(); }

}