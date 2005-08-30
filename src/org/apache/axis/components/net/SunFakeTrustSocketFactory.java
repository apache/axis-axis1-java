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
package org.apache.axis.components.net;

import java.util.Hashtable;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

import com.sun.net.ssl.SSLContext;
import com.sun.net.ssl.TrustManager;
import com.sun.net.ssl.X509TrustManager;

/**
 * Hook for Axis sender, allowing unsigned server certs
 */
public class SunFakeTrustSocketFactory extends SunJSSESocketFactory {

    /** Field log           */
    protected static Log log =
            LogFactory.getLog(SunFakeTrustSocketFactory.class.getName());

    /**
     * Constructor FakeTrustSocketFactory
     *
     * @param attributes
     */
    public SunFakeTrustSocketFactory(Hashtable attributes) {
        super(attributes);
    }

    /**
     * Method getContext
     *
     * @return
     *
     * @throws Exception
     */
    protected SSLContext getContext() throws Exception {

        try {
            SSLContext sc = SSLContext.getInstance("SSL");

            sc.init(null, // we don't need no stinkin KeyManager
                    new TrustManager[]{new FakeX509TrustManager()},
                    new java.security.SecureRandom());
            if (log.isDebugEnabled()) {
                log.debug(Messages.getMessage("ftsf00"));
            }
            return sc;
        } catch (Exception exc) {
            log.error(Messages.getMessage("ftsf01"), exc);
            throw new Exception(Messages.getMessage("ftsf02"));
        }
    }

    /**
     * Class FakeX509TrustManager
     */
    public static class FakeX509TrustManager implements X509TrustManager {

        /** Field log           */
        protected static Log log =
                LogFactory.getLog(FakeX509TrustManager.class.getName());

        /**
         * Method isClientTrusted
         *
         * @param chain
         *
         * @return
         */
        public boolean isClientTrusted(java.security.cert
                .X509Certificate[] chain) {

            if (log.isDebugEnabled()) {
                log.debug(Messages.getMessage("ftsf03"));
            }
            return true;
        }

        /**
         * Method isServerTrusted
         *
         * @param chain
         *
         * @return
         */
        public boolean isServerTrusted(java.security.cert
                .X509Certificate[] chain) {

            if (log.isDebugEnabled()) {
                log.debug(Messages.getMessage("ftsf04"));
            }
            return true;
        }

        /**
         * Method getAcceptedIssuers
         *
         * @return
         */
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {

            if (log.isDebugEnabled()) {
                log.debug(Messages.getMessage("ftsf05"));
            }
            return null;
        }
    }
}
