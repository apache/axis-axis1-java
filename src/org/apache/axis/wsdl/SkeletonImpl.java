/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.axis.wsdl;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import java.util.HashMap;

/**
 * Provides Base function implementation for the Skeleton interface
 */
public class SkeletonImpl implements Skeleton {

    /** Field table */
    private static HashMap table = null;

    /**
     * Constructor
     */
    public SkeletonImpl() {

        if (table == null) {
            table = new HashMap();
        }
    }

    /**
     * Class MetaInfo
     * 
     * @version %I%, %G%
     */
    class MetaInfo {

        /** Field names */
        QName[] names;

        /** Field modes */
        ParameterMode[] modes;

        /** Field inputNamespace */
        String inputNamespace;

        /** Field outputNamespace */
        String outputNamespace;

        /** Field soapAction */
        String soapAction;

        /**
         * Constructor MetaInfo
         * 
         * @param names           
         * @param modes           
         * @param inputNamespace  
         * @param outputNamespace 
         * @param soapAction      
         */
        MetaInfo(QName[] names, ParameterMode[] modes, String inputNamespace,
                 String outputNamespace, String soapAction) {

            this.names = names;
            this.modes = modes;
            this.inputNamespace = inputNamespace;
            this.outputNamespace = outputNamespace;
            this.soapAction = soapAction;
        }
    }

    /**
     * Add operation name and vector containing return and parameter names.
     * The first name in the array is either the return name (which
     * should be set to null if there is no return name)
     * 
     * @param operation       
     * @param names           
     * @param modes           
     * @param inputNamespace  
     * @param outputNamespace 
     * @param soapAction      
     */
    public void add(String operation, QName[] names, ParameterMode[] modes,
                    String inputNamespace, String outputNamespace,
                    String soapAction) {

        table.put(operation,
                new MetaInfo(names, modes, inputNamespace, outputNamespace,
                        soapAction));
    }

    /**
     * Convenience method which allows passing an array of Strings which
     * will be converted into QNames with no namespace.
     * 
     * @param operation       
     * @param names           
     * @param modes           
     * @param inputNamespace  
     * @param outputNamespace 
     * @param soapAction      
     */
    public void add(String operation, String[] names, ParameterMode[] modes,
                    String inputNamespace, String outputNamespace,
                    String soapAction) {

        QName[] qnames = new QName[names.length];

        for (int i = 0; i < names.length; i++) {
            QName qname = new QName(null, names[i]);

            qnames[i] = qname;
        }

        add(operation, qnames, modes, inputNamespace, outputNamespace,
                soapAction);
    }

    /**
     * Used to return the name of the n-th parameter of the specified
     * operation.  Use -1 to get the return type name
     * Returns null if problems occur or the parameter is not known.
     * 
     * @param operationName 
     * @param n             
     * @return 
     */
    public QName getParameterName(String operationName, int n) {

        MetaInfo value = (MetaInfo) table.get(operationName);

        if ((value == null) || (value.names == null)
                || (value.names.length <= n + 1)) {
            return null;
        }

        return value.names[n + 1];
    }

    /**
     * Used to return the mode of the n-th parameter of the specified
     * operation.  Use -1 to get the return mode.
     * Returns null if problems occur or the parameter is not known.
     * 
     * @param operationName 
     * @param n             
     * @return 
     */
    public ParameterMode getParameterMode(String operationName, int n) {

        MetaInfo value = (MetaInfo) table.get(operationName);

        if ((value == null) || (value.modes == null)
                || (value.modes.length <= n + 1)) {
            return null;
        }

        return value.modes[n + 1];
    }

    /**
     * Used to return the namespace of the input clause of the given
     * operation.  Returns null if problems occur.
     * 
     * @param operationName 
     * @return 
     */
    public String getInputNamespace(String operationName) {

        MetaInfo value = (MetaInfo) table.get(operationName);

        if (value == null) {
            return null;
        }

        return value.inputNamespace;
    }

    /**
     * Used to return the namespace of the output clause of the given
     * operation.  Returns null if problems occur.
     * 
     * @param operationName 
     * @return 
     */
    public String getOutputNamespace(String operationName) {

        MetaInfo value = (MetaInfo) table.get(operationName);

        if (value == null) {
            return null;
        }

        return value.outputNamespace;
    }

    /**
     * Used to return the SOAPAction of the given operation.
     * Returns null if problems occur.
     * 
     * @param operationName 
     * @return 
     */
    public String getSOAPAction(String operationName) {

        MetaInfo value = (MetaInfo) table.get(operationName);

        if (value == null) {
            return null;
        }

        return value.soapAction;
    }
}
