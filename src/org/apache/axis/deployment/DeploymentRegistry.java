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
package org.apache.axis.deployment;

import org.apache.axis.Handler;
import org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration;
import org.apache.axis.encoding.TypeMappingRegistry;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import org.apache.axis.Handler;
import org.apache.axis.Chain;
import org.apache.axis.TargetedChain;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration;
import org.apache.axis.deployment.wsdd.WSDDDeployableItem;
import javax.xml.rpc.namespace.QName;

/**
 * The DeploymentRegistry abstract class takes the place of the
 * org.apache.axis.registry.HandlerRegistry and extends the
 * functionality to cover all Axis deployable items.
 *
 * @author James Snell
 */
public abstract class DeploymentRegistry
    implements Serializable
{

    /**
     * retrieve the global configuration for the axis engine
     * @return XXX
     * @throws DeploymentException XXX
     */
    public abstract WSDDGlobalConfiguration getGlobalConfiguration()
        throws DeploymentException;

    /**
     * retrieve an instance of the named handler
     * @param qname XXX
     * @return XXX
     * @throws DeploymentException XXX
     */
    public abstract Handler getDeployedItem(QName qname)
        throws DeploymentException;

    /**
     * retrieve an instance of the named mapping registry
     * @param encodingStyle XXX
     * @return XXX
     * @throws DeploymentException XXX
     */
    public abstract TypeMappingRegistry getTypeMappingRegistry(
        String encodingStyle)
        throws DeploymentException;

    /**
     * add the given type mapping
     * @param encodingStyle XXX
     * @param tmr XXX
     * @throws DeploymentException XXX
     */
    public abstract void addTypeMappingRegistry(String encodingStyle,
                                                TypeMappingRegistry tmr)
        throws DeploymentException;

    /**
     * remove the given type mapping
     * @param encodingStyle XXX
     * @throws DeploymentException XXX
     */
    public abstract void removeTypeMappingRegistry(String encodingStyle)
        throws DeploymentException;

    /**
     * Deploy an Axis WSDD Document
     * @param deployment XXX
     * @throws DeploymentException XXX
     */
    public abstract void deploy(DeploymentDocument deployment)
        throws DeploymentException;

    /**
     * deploy the given item
     * @param item XXX
     * @throws DeploymentException XXX
     */
    public abstract void deployItem(DeployableItem item)
        throws DeploymentException;

    /**
     * remove the given item
     * @param qname XXX
     * @throws DeploymentException XXX
     */
    public abstract void removeDeployedItem(QName qname)
        throws DeploymentException;

    /**
     * set the global configuration for the axis engine
     * @param global XXX
     */
    public abstract void setGlobalConfiguration(
        WSDDGlobalConfiguration global);

    /**
     * Save the registry to the given filepath
     * @param filename XXX
     * @throws IOException XXX
     */
    public void save(String filename)
        throws IOException
    {
        this.save(filename, this);
    }

    /**
     * Save the registry to the given outputstream
     * @param out XXX
     * @throws IOException XXX
     */
    public void save(OutputStream out)
        throws IOException
    {
        this.save(out, this);
    }

    /**
     * Save the given registry to the given filepath
     * @param filename XXX
     * @param registry XXX
     * @throws IOException XXX
     */
    public static void save(String filename, DeploymentRegistry registry)
        throws IOException
    {

        FileOutputStream fos = new FileOutputStream(filename);

        save(fos, registry);
    }

    /**
     * save the given registry to the given output stream
     * @param out XXX
     * @param registry XXX
     * @throws IOException XXX
     */
    public static void save(OutputStream out, DeploymentRegistry registry)
        throws IOException
    {

        ObjectOutputStream oos = new ObjectOutputStream(out);

        oos.writeObject(registry);
        oos.close();
        out.close();
    }

    /**
     * load a registry from the given filepath
     * @param filename XXX
     * @return XXX
     * @throws IOException XXX
     */
    public static DeploymentRegistry load(String filename)
        throws IOException
    {

        FileInputStream fis = new FileInputStream(filename);

        return load(fis);
    }

    /**
     * load a registry from the given inputstream
     * @param in XXX
     * @return XXX
     * @throws IOException XXX
     */
    public static DeploymentRegistry load(InputStream in)
        throws IOException
    {

        ObjectInputStream ois = new ObjectInputStream(in);

        try {
            DeploymentRegistry registry =
                (DeploymentRegistry) ois.readObject();

            return registry;
        }
        catch (java.lang.ClassNotFoundException cnfe) {
            throw new IOException(cnfe.toString());
        }
        finally {
            ois.close();
            in.close();
        }
    }
}
