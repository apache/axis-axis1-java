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
        new org.apache.axis.description.TypeDesc(RETURN.class);

    static {
        org.apache.axis.description.FieldDesc field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("LOGNO");
        field.setXmlName(new javax.xml.namespace.QName("", "LOG_NO"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("MESSAGEV4");
        field.setXmlName(new javax.xml.namespace.QName("", "MESSAGE_V4"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("MESSAGEV3");
        field.setXmlName(new javax.xml.namespace.QName("", "MESSAGE_V3"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("MESSAGEV2");
        field.setXmlName(new javax.xml.namespace.QName("", "MESSAGE_V2"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("MESSAGEV1");
        field.setXmlName(new javax.xml.namespace.QName("", "MESSAGE_V1"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("LOGMSGNO");
        field.setXmlName(new javax.xml.namespace.QName("", "LOG_MSG_NO"));
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
