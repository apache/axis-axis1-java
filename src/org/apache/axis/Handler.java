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

package org.apache.axis ;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;

/**
 * An AXIS handler.
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public interface Handler extends Serializable {
    /**
     * Init is called when the chain containing this Handler object
     * is instantiated.
     */
    public void init();

    /**
     * Cleanup is called when the chain containing this Handler object
     * is done processing the chain.
     */
    public void cleanup();

    /**
     * Invoke is called to do the actual work of the Handler object.
     * If there is a fault during the processing of this method it is
     * invoke's job to catch the exception and undo any partial work
     * that has been completed.  Once we leave 'invoke' if a fault
     * is thrown, this classes 'onFault' method will be called.
     * Invoke should rethrow any exceptions it catches, wrapped in
     * an AxisFault.
     *
     * @param msgContext    the <code>MessageContext</code> to process with this
     *              <code>Handler</code>.
     * @throws AxisFault if the handler encounters an error
     */
    public void invoke(MessageContext msgContext) throws AxisFault ;

    /**
     * Called when a subsequent handler throws a fault.
     *
     * @param msgContext    the <code>MessageContext</code> to process the fault
     *              to
     */
    public void onFault(MessageContext msgContext);

    /**
     * Indicate if this handler can process <code>qname</code>.
     *
     * @param qname  the <code>QName</code> to check
     * @return true if this <code>Handler</code> can handle <code>qname<code>,
     *              false otherwise
     */
    public boolean canHandleBlock(QName qname);

    // fixme: will modifications to this List be reflected in the state of this
    //  handler?
    /**
     * Return a list of QNames which this Handler understands.  By returning
     * a particular QName here, we are committing to fulfilling any contracts
     * defined in the specification of the SOAP header with that QName.
     *
     * @return a List of <code>QName</code> instances
     */
    public List getUnderstoodHeaders();

    // fixme: doesn't specify what happens when an option is re-defined
    /**
     * Add the given option (name/value) to this handler's bag of options.
     *
     * @param name  the name of the option
     * @param value the new value of the option
     */
    public void setOption(String name, Object value);

    /**
     * Returns the option corresponding to the 'name' given.
     *
     * @param name  the name of the option
     * @return the value of the option
     */
    public Object getOption(String name);

    /**
     * Set the name (i.e. registry key) of this Handler.
     *
     * @param name  the new name
     */
    public void setName(String name);

    /**
     * Return the name (i.e. registry key) for this <code>Handler</code>.
     *
     * @return the name for this <code>Handler</code>
     */
    public String getName();

    // fixme: doesn't tell us if modifying this Hashset will modify this Handler
    // fixme: do we mean to use a Hahset, or will Map do?
    /**
     * Return the entire list of options.
     *
     * @return a <code>Hashset</code> containing all name/value pairs
     */
    public Hashtable getOptions();

    // fixme: this doesn't indicate if opts becomes the new value of
    //  getOptions(), or if it is merged into it. Also doesn't specify if
    //  modifications to opts after calling this method will affect this handler
    /**
     * Sets a whole list of options.
     *
     * @param opts  a <code>Hashtable</code> of name-value pairs to use
     */
    public void setOptions(Hashtable opts);

    // fixme: presumably doc is used as the factory & host for Element, and
    //  Element should not be grafted into doc by getDeploymentData, but will
    //  potentially be grafted in by code calling this - could we clarify this?
    /**
     * This will return the root element of an XML doc that describes the
     * deployment information about this handler.  This is NOT the WSDL,
     * this is all of the static internal data use by Axis - WSDL takes into
     * account run-time information (like which service we're talking about)
     * this is just the data that's stored in the registry.  Used by the
     * 'list' Admin function.
     *
     * @param doc  a <code>Document</code> within which to build the deployment
     *              data
     * @return an Element representing the deployment data
     */
    public Element getDeploymentData(Document doc);

    /**
     * Obtain WSDL information.  Some Handlers will implement this by
     * merely setting properties in the MessageContext, others (providers)
     * will take responsibility for doing the "real work" of generating
     * WSDL for a given service.
     *
     * @param msgContext the <code>MessageContext</code> to generate the WSDL
     *              to
     * @throws AxisFault  if there was a problem generating the WSDL
     */
    public void generateWSDL(MessageContext msgContext) throws AxisFault;
};
