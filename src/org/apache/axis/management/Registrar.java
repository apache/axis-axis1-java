/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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

package org.apache.axis.management;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * class to act as a dynamic loading registrar to commons-modeler, so
 * as to autoregister stuff
 * @link http://www.webweavertech.com/costin/archives/000168.html#000168
 */
public class Registrar {
    /**
     * our log
     */
    protected static Log log = LogFactory.getLog(Registrar.class.getName());

    /**
     *  register using reflection. The perf hit is moot as jmx is
     * all reflection anyway
     * @param objectToRegister
     * @param name
     * @param context
     */
    public static boolean register(Object objectToRegister,
                                   String name, String context) {

        if (isBound()) {
            if(log.isDebugEnabled()) {
                log.debug("Registering "+objectToRegister+" as "
                    +name);
            }
            return modelerBinding.register(objectToRegister, name, context);
        } else {
            return false;
        }
    }

    /**
     * Check for being bound to a modeler -this will force
     * a binding if none existed.
     * @return
     */

    public static boolean isBound() {
        createModelerBinding();
        return modelerBinding.canBind();
    }

    /**
     * create the modeler binding if it is needed
     * At the end of this call, modelerBinding != null
     */
    private static void createModelerBinding() {
        if (modelerBinding == null) {
            modelerBinding = new ModelerBinding();
        }
    }

    /**
     * the inner class that does the binding
     */
    private static ModelerBinding modelerBinding = null;

    /**
     * This class provides a dynamic binding to the
     * commons-modeler registry at run time
     */
    static class ModelerBinding {

        /**
         * the constructor binds
         */
        public ModelerBinding() {
            bindToModeler();
        }

        /**
         * can the binding bind?
         * @return true iff the classes are bound
         */
        public boolean canBind() {
            return registry != null;
        }

        /**
         * our log
         */
        protected static Log log = LogFactory.getLog(ModelerBinding.class.getName());

        /**
         * registry object
         */
        Object registry;

        /**
         * method to call
         */
        Method registerComponent;

        /*
         * this is what we could do without reflection
         */
        /*
        void simpleRegister(Object objectToRegister, String name, String context)
                throws Exception {
            Registry.setUseContextClassLoader(true);
            Registry reg = Registry.getRegistry(null, null);
            reg.registerComponent(objectToRegister, name, context);
        }
*/
        /**
         *  register using reflection. The perf hit is moot as jmx is
         * all reflection anyway
         * @param objectToRegister
         * @param name
         * @param context
         */
        public boolean register(Object objectToRegister, String name, String context) {
            if (registry != null) {
                Object args[] = new Object[]{objectToRegister, name, context};
                try {
                    registerComponent.invoke(registry, args);
                    if (log.isDebugEnabled()) {
                        log.debug("Registered " + name + " in " + context);
                    }
                } catch (IllegalAccessException e) {
                    log.error(e);
                    return false;
                } catch (IllegalArgumentException e) {
                    log.error(e);
                    return false;
                } catch (InvocationTargetException e) {
                    log.error(e);
                    return false;
                }
                return true;
            } else {
                return false;
            }
        }

        /**
         * bind to the modeler; return success/failure flag
         * @return true if the binding worked
         */
        private boolean bindToModeler() {
            Exception ex = null;
            try {
                Class clazz = Class.forName("org.apache.commons.modeler.Registry");
                Class[] getRegistryArgs = new Class[]{Object.class, Object.class, };
                Method getRegistry = clazz.getMethod("getRegistry", getRegistryArgs);
                Object[] getRegistryOptions = new Object[]{null, null};
                registry = getRegistry.invoke(null, getRegistryOptions);
                Class[] registerArgs = new Class[]{Object.class,
                                                   String.class,
                                                   String.class};

                registerComponent = clazz.getMethod("registerComponent", registerArgs);
            } catch (IllegalAccessException e) {
                ex = e;
            } catch (IllegalArgumentException e) {
                ex = e;
            } catch (InvocationTargetException e) {
                ex = e;
            } catch (ClassNotFoundException e) {
                ex = e;
            } catch (NoSuchMethodException e) {
                ex = e;
            }
            // handle any of these exceptions
            if (ex != null) {
                //log the error
                log.error(ex);
                //mark the registration as a failure
                registry = null;
                //and fail
                return false;
            } else {
                //success
                return true;
            }

        }
    }

}
