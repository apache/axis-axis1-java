package org.apache.axis.enum;

import javax.xml.rpc.namespace.QName;

import org.apache.axis.Constants;
import org.apache.axis.deployment.wsdd.WSDDConstants;



/**
 * @author rsitze
 */
public class Style extends Enum {
    private static final Type type = new Type();
    
    public static final String RPC_STR = "rpc";
    public static final String DOCUMENT_STR = "document";
    public static final String WRAPPED_STR = "wrapped";
    public static final String MESSAGE_STR = "message";
    
    public static final Style RPC = type.getStyle(RPC_STR);
    public static final Style DOCUMENT = type.getStyle(DOCUMENT_STR);
    public static final Style WRAPPED = type.getStyle(WRAPPED_STR);
    public static final Style MESSAGE = type.getStyle(MESSAGE_STR);

    public static final Style DEFAULT = RPC;
    
    static { type.setDefault(DEFAULT); }

        
    private QName provider;
    private String encoding;
    

    // public int     getValue();
    // public String  getName();
    // public Type    getType();


    public static Style getDefault() { return (Style)type.getDefault(); }
    
    public final QName getProvider() { return provider; }
    public final String getEncoding() { return encoding; }

    public static final Style getStyle(int style) {
        return type.getStyle(style);
    }

    public static final Style getStyle(String style) {
        return type.getStyle(style);
    }
    
    public static final Style getStyle(String style, Style dephault) {
        return type.getStyle(style, dephault);
    }
    
    public static final boolean isValid(String style) {
        return type.isValid(style);
    }
    
    public static final int size() {
        return type.size();
    }
    
    public static final String[] getStyles() {
        return type.getEnumNames();
    }
    
    public static class Type extends Enum.Type {
        private Type() {
            super("style", new Enum[] {
            new Style(0, RPC_STR,
                  WSDDConstants.QNAME_JAVARPC_PROVIDER,
                  Constants.URI_DEFAULT_SOAP_ENC),
            new Style(1, DOCUMENT_STR,
                  WSDDConstants.QNAME_JAVARPC_PROVIDER,
                  Constants.URI_LITERAL_ENC),
            new Style(2, WRAPPED_STR,
                  WSDDConstants.QNAME_JAVARPC_PROVIDER,
                  Constants.URI_LITERAL_ENC),
            new Style(3, MESSAGE_STR,
                  WSDDConstants.QNAME_JAVAMSG_PROVIDER,
                  Constants.URI_LITERAL_ENC)
            });
        }

        public final Style getStyle(int style) {
            return (Style)this.getEnum(style);
        }

        public final Style getStyle(String style) {
            return (Style)this.getEnum(style);
        }

        public final Style getStyle(String style, Style dephault) {
            return (Style)this.getEnum(style, dephault);
        }

        // public final   String getName();
        // public boolean isValid(String enumName);
        // public final int size();
        // public final String[] getEnumNames();
        // public final Enum getEnum(int enum);
        // public final Enum getEnum(String enumName);
        // public final Enum getEnum(String enumName, Enum dephault);
    }

    private Style(int value, String name, QName provider, String encoding) {
        super(type, value, name);
        this.provider = provider;
        this.encoding = encoding;
    }
};
