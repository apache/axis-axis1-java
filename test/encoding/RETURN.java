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

package test.encoding;

public class RETURN implements java.io.Serializable {
    private java.lang.String TYPE;
    private java.lang.String ID;
    private java.lang.String NUMBER;
    private java.lang.String MESSAGE;
    private java.lang.String LOGNO;
    private java.lang.String LOGMSGNO;
    private java.lang.String MESSAGEV1;
    private java.lang.String MESSAGEV2;
    private java.lang.String MESSAGEV3;
    private java.lang.String MESSAGEV4;

    public RETURN() {
    }

    public java.lang.String getTYPE() {
        return TYPE;
    }

    public void setTYPE(java.lang.String TYPE) {
        this.TYPE = TYPE;
    }

    public java.lang.String getID() {
        return ID;
    }

    public void setID(java.lang.String ID) {
        this.ID = ID;
    }

    public java.lang.String getNUMBER() {
        return NUMBER;
    }

    public void setNUMBER(java.lang.String NUMBER) {
        this.NUMBER = NUMBER;
    }

    public java.lang.String getMESSAGE() {
        return MESSAGE;
    }

    public void setMESSAGE(java.lang.String MESSAGE) {
        this.MESSAGE = MESSAGE;
    }

    public java.lang.String getLOGNO() {
        return LOGNO;
    }

    public void setLOGNO(java.lang.String LOGNO) {
        this.LOGNO = LOGNO;
    }

    public java.lang.String getLOGMSGNO() {
        return LOGMSGNO;
    }

    public void setLOGMSGNO(java.lang.String LOGMSGNO) {
        this.LOGMSGNO = LOGMSGNO;
    }

    public java.lang.String getMESSAGEV1() {
        return MESSAGEV1;
    }

    public void setMESSAGEV1(java.lang.String MESSAGEV1) {
        this.MESSAGEV1 = MESSAGEV1;
    }

    public java.lang.String getMESSAGEV2() {
        return MESSAGEV2;
    }

    public void setMESSAGEV2(java.lang.String MESSAGEV2) {
        this.MESSAGEV2 = MESSAGEV2;
    }

    public java.lang.String getMESSAGEV3() {
        return MESSAGEV3;
    }

    public void setMESSAGEV3(java.lang.String MESSAGEV3) {
        this.MESSAGEV3 = MESSAGEV3;
    }

    public java.lang.String getMESSAGEV4() {
        return MESSAGEV4;
    }

    public void setMESSAGEV4(java.lang.String MESSAGEV4) {
        this.MESSAGEV4 = MESSAGEV4;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc();

    static {
        org.apache.axis.description.FieldDesc field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("LOGNO");
        field.setXmlName(new javax.xml.rpc.namespace.QName("", "LOG_NO"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("MESSAGEV4");
        field.setXmlName(new javax.xml.rpc.namespace.QName("", "MESSAGE_V4"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("MESSAGEV3");
        field.setXmlName(new javax.xml.rpc.namespace.QName("", "MESSAGE_V3"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("MESSAGEV2");
        field.setXmlName(new javax.xml.rpc.namespace.QName("", "MESSAGE_V2"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("MESSAGEV1");
        field.setXmlName(new javax.xml.rpc.namespace.QName("", "MESSAGE_V1"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("LOGMSGNO");
        field.setXmlName(new javax.xml.rpc.namespace.QName("", "LOG_MSG_NO"));
        typeDesc.addFieldDesc(field);
    };

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    public boolean equals(Object obj) {
        // compare elements
        RETURN other = (RETURN) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (! (obj instanceof RETURN)) return false;
        return
            ((TYPE==null && other.getTYPE()==null) || 
             (TYPE!=null &&
              TYPE.equals(other.getTYPE()))) &&
            ((ID==null && other.getID()==null) || 
             (ID!=null &&
              ID.equals(other.getID()))) &&
            ((NUMBER==null && other.getNUMBER()==null) || 
             (NUMBER!=null &&
              NUMBER.equals(other.getNUMBER()))) &&
            ((MESSAGE==null && other.getMESSAGE()==null) || 
             (MESSAGE!=null &&
              MESSAGE.equals(other.getMESSAGE()))) &&
            ((LOGNO==null && other.getLOGNO()==null) || 
             (LOGNO!=null &&
              LOGNO.equals(other.getLOGNO()))) &&
            ((LOGMSGNO==null && other.getLOGMSGNO()==null) || 
             (LOGMSGNO!=null &&
              LOGMSGNO.equals(other.getLOGMSGNO()))) &&
            ((MESSAGEV1==null && other.getMESSAGEV1()==null) || 
             (MESSAGEV1!=null &&
              MESSAGEV1.equals(other.getMESSAGEV1()))) &&
            ((MESSAGEV2==null && other.getMESSAGEV2()==null) || 
             (MESSAGEV2!=null &&
              MESSAGEV2.equals(other.getMESSAGEV2()))) &&
            ((MESSAGEV3==null && other.getMESSAGEV3()==null) || 
             (MESSAGEV3!=null &&
              MESSAGEV3.equals(other.getMESSAGEV3()))) &&
            ((MESSAGEV4==null && other.getMESSAGEV4()==null) || 
             (MESSAGEV4!=null &&
              MESSAGEV4.equals(other.getMESSAGEV4())));
    }
}
