/*
 * The Apache Software License, Version 1.1
 * 
 * 
 * Copyright (c) 2002-2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  3. The end-user documentation included with the redistribution, if any,
 * must include the following acknowledgment: "This product includes software
 * developed by the Apache Software Foundation (http://www.apache.org/)."
 * Alternately, this acknowledgment may appear in the software itself, if and
 * wherever such third-party acknowledgments normally appear.
 *  4. The names "Axis" and "Apache Software Foundation" must not be used to
 * endorse or promote products derived from this software without prior written
 * permission. For written permission, please contact apache@apache.org.
 *  5. Products derived from this software may not be called "Apache", nor may
 * "Apache" appear in their name, without prior written permission of the
 * Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the Apache Software Foundation. For more information on the
 * Apache Software Foundation, please see <http://www.apache.org/> .
 */
package org.apache.axis.wsi.scm.retailer;

import java.math.BigDecimal;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.server.ServiceLifecycle;

import org.apache.axis.wsi.scm.retailer.catalog.CatalogItem;
import org.apache.axis.wsi.scm.retailer.catalog.CatalogType;

/**
 * Implementation of RetailerPortType
 * 
 * @author Ias (iasandcb@tmax.co.kr)
 */
public class RetailerSoapBindingImpl implements org.apache.axis.wsi.scm.retailer.RetailerPortType, ServiceLifecycle {

    CatalogType catalog = new CatalogType();

    public org.apache.axis.wsi.scm.retailer.catalog.CatalogType getCatalog() throws java.rmi.RemoteException {
        return catalog;
    }

    public org.apache.axis.wsi.scm.retailer.order.PartsOrderResponseType submitOrder(
        org.apache.axis.wsi.scm.retailer.order.PartsOrderType partsOrder,
        org.apache.axis.wsi.scm.retailer.order.CustomerDetailsType customerDetails,
        org.apache.axis.wsi.scm.configuration.ConfigurationType configurationHeader)
        throws
            java.rmi.RemoteException,
            org.apache.axis.wsi.scm.retailer.order.InvalidProductCodeType,
            org.apache.axis.wsi.scm.retailer.BadOrderFault,
            org.apache.axis.wsi.scm.configuration.ConfigurationFaultType {
        return null;
    }

    /**
     * @see javax.xml.rpc.server.ServiceLifecycle#init(java.lang.Object)
     */
    public void init(Object context) throws ServiceException {
        CatalogItem[] items = new CatalogItem[10];
        items[0] = new CatalogItem();
        items[0].setName("TV, Brand1");
        items[0].setDescription("24in, Color, Advanced Velocity Scan Modulation, Stereo");
        items[0].setProductNumber(new java.math.BigInteger("605001"));
        items[0].setCategory("TV");
        items[0].setBrand("Brand1");
        items[0].setPrice((new BigDecimal(299.95)).setScale(2, BigDecimal.ROUND_HALF_UP));

        items[1] = new CatalogItem();
        items[1].setName("TV, Brand2");
        items[1].setDescription("32in, Super Slim Flat Panel Plasma");
        items[1].setProductNumber(new java.math.BigInteger("605002"));
        items[1].setCategory("TV");
        items[1].setBrand("Brand2");
        items[1].setPrice((new BigDecimal(1499.99)).setScale(2, BigDecimal.ROUND_HALF_UP));

        items[2] = new CatalogItem();
        items[2].setName("TV, Brand3");
        items[2].setDescription("50in, Plasma Display");
        items[2].setProductNumber(new java.math.BigInteger("605003"));
        items[2].setCategory("TV");
        items[2].setBrand("Brand3");
        items[2].setPrice(new BigDecimal("5725.98"));

        items[3] = new CatalogItem();
        items[3].setName("Video, Brand1");
        items[3].setDescription("S-VHS");
        items[3].setProductNumber(new java.math.BigInteger("605004"));
        items[3].setCategory("Video");
        items[3].setBrand("Brand1");
        items[3].setPrice(new BigDecimal("199.95"));

        items[4] = new CatalogItem();
        items[4].setName("Video, Brand2");
        items[4].setDescription("HiFi, S-VHS");
        items[4].setProductNumber(new java.math.BigInteger("605005"));
        items[4].setCategory("Video");
        items[4].setBrand("Brand2");
        items[4].setPrice(new BigDecimal("400.00"));

        items[5] = new CatalogItem();
        items[5].setName("Video, Brand3");
        items[5].setDescription("s-vhs, mindv");
        items[5].setProductNumber(new java.math.BigInteger("605006"));
        items[5].setCategory("Video");
        items[5].setBrand("Brand3");
        items[5].setPrice(new BigDecimal("949.99"));

        items[6] = new CatalogItem();
        items[6].setName("DVD, Brand1");
        items[6].setDescription("DVD-Player W/Built-In Dolby Digital Decoder");
        items[6].setProductNumber(new java.math.BigInteger("605007"));
        items[6].setCategory("DVD");
        items[6].setBrand("Brand1");
        items[6].setPrice(new BigDecimal("100.00"));

        items[7] = new CatalogItem();
        items[7].setName("DVD, Brand2");
        items[7].setDescription(
            "Plays DVD-Video discs, CDs, stereo and multi-channel SACDs, and audio CD-Rs & CD-RWs, 27MHz/10-bit video DAC, ");
        items[7].setProductNumber(new java.math.BigInteger("605008"));
        items[7].setCategory("DVD");
        items[7].setBrand("Brand2");
        items[7].setPrice(new BigDecimal("200.00"));

        items[8] = new CatalogItem();
        items[8].setName("DVD, Brand3");
        items[8].setDescription(
            "DVD Player with SmoothSlow forward/reverse; Digital Video Enhancer; DVD/CD Text; Custom Parental Control (20-disc); Digital Cinema Sound modes");
        items[8].setProductNumber(new java.math.BigInteger("605009"));
        items[8].setCategory("DVD");
        items[8].setBrand("Brand3");
        items[8].setPrice(new BigDecimal("250.00"));

        // This one is an invalid product
        items[9] = new CatalogItem();
        items[9].setName("TV, Brand4");
        items[9].setDescription(
            "Designated invalid product code that is allowed to appear in the catalog, but is unable to be ordered");
        items[9].setProductNumber(new java.math.BigInteger("605010"));
        items[9].setCategory("TV");
        items[9].setBrand("Brand4");
        items[9].setPrice(new BigDecimal("149.99"));
        catalog.setItem(items);
    }

    /**
     * @see javax.xml.rpc.server.ServiceLifecycle#destroy()
     */
    public void destroy() {
    }

}
