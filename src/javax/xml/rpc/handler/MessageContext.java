/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package javax.xml.rpc.handler;

import java.util.Iterator;

/**
 * The interface <code>MessageContext</code> abstracts the message
 * context that is processed by a handler in the <code>handle</code>
 * method.
 *
 * <p>The <code>MessageContext</code> interface provides methods to
 * manage a property set. <code>MessageContext</code> properties
 * enable handlers in a handler chain to share processing related
 * state.
 *
 * @version 1.0
 */
public interface MessageContext {

    /**
     * Sets the name and value of a property associated with the
     * <code>MessageContext</code>. If the <code>MessageContext</code>
     * contains a value of the same property, the old value is replaced.
     *
     * @param  name ame of the property associated with the
     *         <code>MessageContext</code>
     * @param  value Value of the property
     * @throws java.lang.IllegalArgumentException If some aspect
     *         the property is prevents it from being stored
     *         in the context
     * @throws java.lang.UnsupportedOperationException If this method is
     *         not supported.
     */
    public abstract void setProperty(String name, Object value);

    /**
     * Gets the value of a specific property from the <code>MessageContext</code>
     * @param name Name of the property whose value is to be
     *        retrieved
     * @return Value of the property
     * @throws java.lang.IllegalArgumentException if an illegal
     *        property name is specified
     */
    public abstract Object getProperty(String name);

    /**
     * Removes a property (name-value pair) from the <code>MessageContext</code>
     * @param  name Name of the property to be removed
     *
     * @param nae
     * @throws java.lang.IllegalArgumentException if an illegal
     *        property name is specified
     */
    public abstract void removeProperty(String name);

    /**
     * Returns true if the <code>MessageContext</code> contains a property
     * with the specified name.
     * @param   name Name of the property whose presense is to be tested
     * @return  Returns true if the MessageContext contains the
     *     property; otherwise false
     */
    public abstract boolean containsProperty(String name);

    /**
     * Returns an Iterator view of the names of the properties
     * in this <code>MessageContext</code>
     * @return Iterator for the property names
     */
    public abstract Iterator getPropertyNames();
}

