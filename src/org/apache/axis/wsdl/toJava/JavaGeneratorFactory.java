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
package org.apache.axis.wsdl.toJava;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DefaultTypeMappingImpl;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.gen.Generator;
import org.apache.axis.wsdl.gen.GeneratorFactory;
import org.apache.axis.wsdl.gen.NoopGenerator;
import org.apache.axis.wsdl.symbolTable.BaseTypeMapping;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.Element;
import org.apache.axis.wsdl.symbolTable.FaultInfo;
import org.apache.axis.wsdl.symbolTable.MessageEntry;
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.PortTypeEntry;
import org.apache.axis.wsdl.symbolTable.SchemaUtils;
import org.apache.axis.wsdl.symbolTable.ServiceEntry;
import org.apache.axis.wsdl.symbolTable.SymTabEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.Type;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.apache.commons.logging.Log;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.xml.namespace.QName;
import javax.xml.rpc.holders.BooleanHolder;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * This is Wsdl2java's implementation of the GeneratorFactory
 */
public class JavaGeneratorFactory implements GeneratorFactory {
    private static final Log log_ =
        LogFactory.getLog(JavaGeneratorFactory.class.getName());

    /** Field emitter */
    protected Emitter emitter;

    /** Field symbolTable */
    protected SymbolTable symbolTable;

    /** Field COMPLEX_TYPE_FAULT */
    public static String COMPLEX_TYPE_FAULT = "ComplexTypeFault";

    /** Field EXCEPTION_CLASS_NAME */
    public static String EXCEPTION_CLASS_NAME = "ExceptionClassName";

    /** Field EXCEPTION_DATA_TYPE */
    public static String EXCEPTION_DATA_TYPE = "ExceptionDataType";

    /**
     * Default constructor.  Note that this class is unusable until setEmitter
     * is called.
     */
    public JavaGeneratorFactory() {
        addGenerators();
    }    // ctor

    /**
     * Constructor JavaGeneratorFactory
     *
     * @param emitter
     */
    public JavaGeneratorFactory(Emitter emitter) {

        this.emitter = emitter;

        addGenerators();
    }    // ctor

    /**
     * Method setEmitter
     *
     * @param emitter
     */
    public void setEmitter(Emitter emitter) {
        this.emitter = emitter;
    }    // setEmitter

    /**
     * Method addGenerators
     */
    private void addGenerators() {

        addMessageGenerators();
        addPortTypeGenerators();
        addBindingGenerators();
        addServiceGenerators();
        addTypeGenerators();
        addDefinitionGenerators();
    }                                            // addGenerators

    /**
     * These addXXXGenerators are called by the constructor.
     * If an extender of this factory wants to CHANGE the set
     * of generators that are called per WSDL construct, they
     * should override these addXXXGenerators methods.  If all
     * an extender wants to do is ADD a generator, then the
     * extension should simply call addGenerator.
     * (NOTE:  It doesn't quite work this way, yet.  Only the
     * Definition generators fit this model at this point in
     * time.)
     */
    protected void addMessageGenerators() {
    }     // addMessageGenerators

    /**
     * Method addPortTypeGenerators
     */
    protected void addPortTypeGenerators() {
    }    // addPortTypeGenerators

    /**
     * Method addBindingGenerators
     */
    protected void addBindingGenerators() {
    }     // addBindingGenerators

    /**
     * Method addServiceGenerators
     */
    protected void addServiceGenerators() {
    }     // addServiceGenerators

    /**
     * Method addTypeGenerators
     */
    protected void addTypeGenerators() {
    }        // addTypeGenerators

    /**
     * Method addDefinitionGenerators
     */
    protected void addDefinitionGenerators() {

        addGenerator(Definition.class, JavaDefinitionWriter.class);    // for faults
        addGenerator(Definition.class,
                JavaDeployWriter.class);      // for deploy.wsdd
        addGenerator(Definition.class,
                JavaUndeployWriter.class);    // for undeploy.wsdd
        addGenerator(Definition.class,
                JavaBuildFileWriter.class); //add a build file writer

    }                                              // addDefinitionGenerators


    /**
     * Do the Wsdl2java generator pass:
     * - resolve name clashes
     * - construct signatures
     *
     * @param def
     * @param symbolTable
     */
    public void generatorPass(Definition def, SymbolTable symbolTable) {

        this.symbolTable = symbolTable;

        javifyNames(symbolTable);
        setFaultContext(symbolTable);
        resolveNameClashes(symbolTable);
        determineInterfaceNames(symbolTable);

        if (emitter.isAllWanted()) {
            setAllReferencesToTrue();
        } else {
            ignoreNonSOAPBindings(symbolTable);
        }

        constructSignatures(symbolTable);
        determineIfHoldersNeeded(symbolTable);
    }    // generatorPass

    /** Since Wsdl2java doesn't emit anything for Messages, return the No-op generator. */
    private Writers messageWriters = new Writers();

    /**
     * Method getGenerator
     *
     * @param message
     * @param symbolTable
     * @return
     */
    public Generator getGenerator(Message message, SymbolTable symbolTable) {
        if (include(message.getQName())) {
            MessageEntry mEntry = symbolTable.getMessageEntry(message.getQName());
            messageWriters.addStuff(new NoopGenerator(), mEntry, symbolTable);
            return messageWriters;
        }
        else {
            return new NoopGenerator();
        }
    }    // getGenerator

    /** Return Wsdl2java's JavaPortTypeWriter object. */
    private Writers portTypeWriters = new Writers();

    /**
     * Method getGenerator
     *
     * @param portType
     * @param symbolTable
     * @return
     */
    public Generator getGenerator(PortType portType, SymbolTable symbolTable) {
        if (include(portType.getQName())) {
            PortTypeEntry ptEntry =
                    symbolTable.getPortTypeEntry(portType.getQName());
            portTypeWriters.addStuff(new NoopGenerator(), ptEntry, symbolTable);
            return portTypeWriters;
        }
        else {
            return new NoopGenerator();
        }
    }    // getGenerator

    /** Return Wsdl2java's JavaBindingWriter object. */
    protected Writers bindingWriters = new Writers();

    /**
     * Method getGenerator
     *
     * @param binding
     * @param symbolTable
     * @return
     */
    public Generator getGenerator(Binding binding, SymbolTable symbolTable) {
        if (include(binding.getQName())) {
            Generator writer = new JavaBindingWriter(emitter, binding,
                    symbolTable);
            BindingEntry bEntry = symbolTable.getBindingEntry(binding.getQName());
            bindingWriters.addStuff(writer, bEntry, symbolTable);
            return bindingWriters;
        }
        else {
            return new NoopGenerator();
        }
    }    // getGenerator

    /** Return Wsdl2java's JavaServiceWriter object. */
    protected Writers serviceWriters = new Writers();

    /**
     * Method getGenerator
     *
     * @param service
     * @param symbolTable
     * @return
     */
    public Generator getGenerator(Service service, SymbolTable symbolTable) {
        if (include(service.getQName())) {
            Generator writer = new JavaServiceWriter(emitter, service,
                    symbolTable);
            ServiceEntry sEntry = symbolTable.getServiceEntry(service.getQName());
            serviceWriters.addStuff(writer, sEntry, symbolTable);
            return serviceWriters;
        }
        else {
            return new NoopGenerator();
        }
    }    // getGenerator

    /** Return Wsdl2java's JavaTypeWriter object. */
    private Writers typeWriters = new Writers();

    /**
     * Method getGenerator
     *
     * @param type
     * @param symbolTable
     * @return
     */
    public Generator getGenerator(TypeEntry type, SymbolTable symbolTable) {
        if (include(type.getQName())) {
            Generator writer = new JavaTypeWriter(emitter, type, symbolTable);
            typeWriters.addStuff(writer, type, symbolTable);
            return typeWriters;
        }
        else {
            return new NoopGenerator();
        }
    }    // getGenerator

    /** Return Wsdl2java's JavaDefinitionWriter object. */
    private Writers defWriters = new Writers();

    /**
     * Method getGenerator
     *
     * @param definition
     * @param symbolTable
     * @return
     */
    public Generator getGenerator(Definition definition,
                                  SymbolTable symbolTable) {
        if (include(definition.getQName())) {
            defWriters.addStuff(null, definition, symbolTable);
            return defWriters;
        }
        else {
            return new NoopGenerator();
        }
    }    // getGenerator

    // Hack class just to play with the idea of adding writers

    /**
     * Class Writers
     *
     * @version %I%, %G%
     */
    protected class Writers implements Generator {

        /** Field writers */
        Vector writers = new Vector();

        /** Field symbolTable */
        SymbolTable symbolTable = null;

        /** Field baseWriter */
        Generator baseWriter = null;

        // entry or def, but not both, will be a parameter.

        /** Field entry */
        SymTabEntry entry = null;

        /** Field def */
        Definition def = null;

        /**
         * Method addGenerator
         *
         * @param writer
         */
        public void addGenerator(Class writer) {
            writers.add(writer);
        }    // addWriter

        /**
         * Method addStuff
         *
         * @param baseWriter
         * @param entry
         * @param symbolTable
         */
        public void addStuff(Generator baseWriter, SymTabEntry entry,
                             SymbolTable symbolTable) {

            this.baseWriter = baseWriter;
            this.entry = entry;
            this.symbolTable = symbolTable;
        }    // addStuff

        /**
         * Method addStuff
         *
         * @param baseWriter
         * @param def
         * @param symbolTable
         */
        public void addStuff(Generator baseWriter, Definition def,
                             SymbolTable symbolTable) {

            this.baseWriter = baseWriter;
            this.def = def;
            this.symbolTable = symbolTable;
        }    // addStuff

        /**
         * Method generate
         *
         * @throws IOException
         */
        public void generate() throws IOException {

            if (baseWriter != null) {
                baseWriter.generate();
            }

            Class[] formalArgs = null;
            Object[] actualArgs = null;

            if (entry != null) {
                formalArgs = new Class[]{Emitter.class, entry.getClass(),
                                         SymbolTable.class};
                actualArgs = new Object[]{emitter, entry, symbolTable};
            } else {
                formalArgs = new Class[]{Emitter.class, Definition.class,
                                         SymbolTable.class};
                actualArgs = new Object[]{emitter, def, symbolTable};
            }

            for (int i = 0; i < writers.size(); ++i) {
                Class wClass = (Class) writers.get(i);
                Generator gen = null;

                try {
                    Constructor ctor = wClass.getConstructor(formalArgs);

                    gen = (Generator) ctor.newInstance(actualArgs);
                } catch (Throwable t) {
                    throw new IOException(Messages.getMessage("exception01",
                            t.getMessage()));
                }

                gen.generate();
            }
        }    // generate
    }    // class Writers

    /**
     * Method addGenerator
     *
     * @param wsdlClass
     * @param generator
     */
    public void addGenerator(Class wsdlClass, Class generator) {

        // This is just a hack right now... it just works with Service
        if (Message.class.isAssignableFrom(wsdlClass)) {
            messageWriters.addGenerator(generator);
        } else if (PortType.class.isAssignableFrom(wsdlClass)) {
            portTypeWriters.addGenerator(generator);
        } else if (Binding.class.isAssignableFrom(wsdlClass)) {
            bindingWriters.addGenerator(generator);
        } else if (Service.class.isAssignableFrom(wsdlClass)) {
            serviceWriters.addGenerator(generator);
        } else if (TypeEntry.class.isAssignableFrom(wsdlClass)) {
            typeWriters.addGenerator(generator);
        } else if (Definition.class.isAssignableFrom(wsdlClass)) {
            defWriters.addGenerator(generator);
        }
    }    // addGenerator

    /**
     * Fill in the names of each SymTabEntry with the javaified name.
     * Note: This method also ensures that anonymous types are
     * given unique java type names.
     *
     * @param symbolTable
     */
    protected void javifyNames(SymbolTable symbolTable) {

        int uniqueNum = 0;
        HashMap anonQNames = new HashMap();
        Iterator it = symbolTable.getHashMap().values().iterator();

        while (it.hasNext()) {
            Vector v = (Vector) it.next();

            for (int i = 0; i < v.size(); ++i) {
                SymTabEntry entry = (SymTabEntry) v.elementAt(i);

                if (entry.getName() != null) {
                    continue;
                }

                // Use the type or the referenced type's QName to generate the java name.
                if (entry instanceof TypeEntry) {
                    uniqueNum = javifyTypeEntryName(symbolTable, (TypeEntry) entry, anonQNames, uniqueNum);
                }

                // If it is not a type, then use this entry's QName to
                // generate its name.
                else {
                    entry.setName(emitter.getJavaName(entry.getQName()));
                }
            }
        }
    }    // javifyNames

    /** Refactored to call recursively for JAX-RPC 1.1 spec 4.2.5. */
    protected int javifyTypeEntryName(SymbolTable symbolTable, TypeEntry entry, HashMap anonQNames, int uniqueNum) {
        TypeEntry tEntry = (TypeEntry) entry;
        String dims = tEntry.getDimensions();
        TypeEntry refType = tEntry.getRefType();
        while (refType != null) {
            tEntry = refType;
            dims += tEntry.getDimensions();
            refType = tEntry.getRefType();
        }

        TypeEntry te = tEntry;
        while (te != null) {
            TypeEntry base = SchemaUtils.getBaseType(te, symbolTable);
            if (base == null)
                break;

            uniqueNum = javifyTypeEntryName(symbolTable, base, anonQNames, uniqueNum);

            if (Utils.getEnumerationBaseAndValues(te.getNode(), symbolTable) == null
                    &&SchemaUtils.getContainedAttributeTypes(te.getNode(), symbolTable) == null) {
                if (base.isSimpleType()) {
                    // Case 1:
                    // <simpleType name="mySimpleStringType">
                    //   <restriction base="xs:string">
                    //   </restriction>
                    // </simpleType>
                    te.setSimpleType(true);
                    te.setName(base.getName());
                    te.setRefType(base);
                }

                if (base.isBaseType()) {
                    // Case 2:
                    // <simpleType name="FooString">
                    //   <restriction base="foo:mySimpleStringType">
                    //   </restriction>
                    // </simpleType>
                    te.setBaseType(true);
                    te.setName(base.getName());
                    te.setRefType(base);
                }
            }

            if (!te.isSimpleType())
                break;

            te = base;
        }

        // Need to javify the ref'd TypeEntry if it was not
        // already processed
        if (tEntry.getName() == null) {
            // Get the QName of the ref'd TypeEntry, which
            // is will be used to javify the name
            QName typeQName = tEntry.getQName();

            // In case of <xsd:list itemType="...">,
            // set typeQName to the value of the itemType attribute.
            QName itemType = SchemaUtils.getListItemType(tEntry.getNode());
            if (itemType != null) {
                typeQName = itemType;
            }

            if ((typeQName.getLocalPart().
                    indexOf(SymbolTable.ANON_TOKEN) < 0)) {
                // Normal Case: The ref'd type is not anonymous
                // Simply construct the java name from
                // the qName
                tEntry.setName(emitter.getJavaName(typeQName));
            } else {
                // This is an anonymous type name.
                // Axis uses '>' as a nesting token to generate
                // unique qnames for anonymous types.
                // Only consider the localName after the last '>'
                // when generating the java name
                // String localName = typeQName.getLocalPart();
                // localName =
                // localName.substring(
                // localName.lastIndexOf(
                // SymbolTable.ANON_TOKEN)+1);
                // typeQName = new QName(typeQName.getNamespaceURI(),
                // localName);
                String localName = typeQName.getLocalPart();

                // Check to see if this is an anonymous type,
                // if it is, replace Axis' ANON_TOKEN with
                // an underscore to make sure we don't run
                // into name collisions with similarly named
                // non-anonymous types
                StringBuffer sb = new StringBuffer(localName);
                int aidx = -1;

                while ((aidx = sb.toString().indexOf(
                        SymbolTable.ANON_TOKEN)) > -1) {
                    sb.replace(
                            aidx,
                            aidx + SymbolTable.ANON_TOKEN.length(),
                            "_");
                }

                localName = sb.toString();
                typeQName = new QName(typeQName.getNamespaceURI(),
                        localName);

                if (emitter.isTypeCollisionProtection()) {
                    // If there is already an existing type,
                    // there will be a collision.
                    // If there is an existing anon type,
                    // there will be a  collision.
                    // In both cases, mangle the name.
                    symbolTable.getType(typeQName);
                    
                    if (anonQNames.get(typeQName) != null) {
                        localName += "Type" + uniqueNum++;
                        typeQName =
                            new QName(typeQName.getNamespaceURI(),
                                      localName);
                    }
                    
                    anonQNames.put(typeQName, typeQName);
                }

                // Now set the name with the constructed qname
                tEntry.setName(emitter.getJavaName(typeQName));
            }
        }
        // Set the entry with the same name as the ref'd entry
        // but add the appropriate amount of dimensions
        entry.setName(tEntry.getName() + dims);

        return uniqueNum;
    }

    /**
     * setFaultContext:
     * Processes the symbol table and sets the COMPLEX_TYPE_FAULT
     * on each TypeEntry that is a complexType and is referenced in
     * a fault message.  TypeEntries that are the base or derived
     * from such a TypeEntry are also marked with COMPLEX_TYPE_FAULT.
     * The containing MessageEntry is marked with cOMPLEX_TYPE_FAULT, and
     * all MessageEntries for faults are tagged with the
     * EXCEPTION_CLASS_NAME variable, which indicates the java exception
     * class name.
     *
     * @param symbolTable SymbolTable
     */
    private void setFaultContext(SymbolTable symbolTable) {

        Iterator it = symbolTable.getHashMap().values().iterator();

        while (it.hasNext()) {
            Vector v = (Vector) it.next();

            for (int i = 0; i < v.size(); ++i) {
                SymTabEntry entry = (SymTabEntry) v.elementAt(i);

                // Inspect each BindingEntry in the Symbol Table
                if (entry instanceof BindingEntry) {
                    BindingEntry bEntry = (BindingEntry) entry;
                    HashMap allOpFaults = bEntry.getFaults();
                    Iterator ops = allOpFaults.values().iterator();

                    // set the context for all faults for this binding.
                    while (ops.hasNext()) {
                        ArrayList faults = (ArrayList) ops.next();

                        for (int j = 0; j < faults.size(); ++j) {
                            FaultInfo info = (FaultInfo) faults.get(j);

                            setFaultContext(info, symbolTable);
                        }
                    }
                }
            }
        }
    }    // setFaultContext

    /**
     * setFaultContext:
     * Helper routine for the setFaultContext method above.
     * Examines the indicated fault and sets COMPLEX_TYPE_FAULT
     * EXCEPTION_DATA_TYPE and EXCEPTION_CLASS_NAME as appropriate.
     *
     * @param fault       FaultInfo to analyze
     * @param symbolTable SymbolTable
     */
    private void setFaultContext(FaultInfo fault, SymbolTable symbolTable) {

        QName faultXmlType = null;
        Vector parts = new Vector();

        // Get the parts of the fault's message.
        // An IOException is thrown if the parts cannot be
        // processed.  Skip such parts for this analysis
        try {
            symbolTable.getParametersFromParts(
                    parts, fault.getMessage().getOrderedParts(null), false,
                    fault.getName(), null);
        } catch (IOException e) {
        }

        // Inspect each TypeEntry referenced in a Fault Message Part
        String exceptionClassName = null;

        for (int j = 0; j < parts.size(); j++) {
            TypeEntry te = ((Parameter) (parts.elementAt(j))).getType();

            // If the TypeEntry is an element, advance to the type.
            // This occurs if the message part uses the element= attribute
            TypeEntry elementTE = null;

            if (te instanceof Element) {
                elementTE = te;
                te = te.getRefType();
            }

            // remember the QName of the type.
            faultXmlType = te.getQName();

            // Determine if the te should be processed using the
            // simple type mapping or the complex type mapping
            // NOTE: treat array types as simple types
            if ((te.getBaseType() != null) || te.isSimpleType()
                    || ((te.getDimensions().length() > 0)
                    && (te.getRefType().getBaseType() != null))) {

                // Simple Type Exception
            } else {

                // Complex Type Exception
                Boolean isComplexFault = (Boolean) te.getDynamicVar(
                        JavaGeneratorFactory.COMPLEX_TYPE_FAULT);

                if ((isComplexFault == null) || !isComplexFault.booleanValue()) {

                    // Mark the type as a complex type fault
                    te.setDynamicVar(JavaGeneratorFactory.COMPLEX_TYPE_FAULT,
                            Boolean.TRUE);

                    if (elementTE != null) {
                        te.setDynamicVar(
                                JavaGeneratorFactory.COMPLEX_TYPE_FAULT,
                                Boolean.TRUE);
                    }

                    // Mark all derived types as Complex Faults
                    HashSet derivedSet =
                            org.apache.axis.wsdl.symbolTable.Utils.getDerivedTypes(
                                    te, symbolTable);
                    Iterator derivedI = derivedSet.iterator();

                    while (derivedI.hasNext()) {
                        TypeEntry derivedTE = (TypeEntry) derivedI.next();

                        derivedTE.setDynamicVar(
                                JavaGeneratorFactory.COMPLEX_TYPE_FAULT,
                                Boolean.TRUE);
                    }

                    // Mark all base types as Complex Faults
                    TypeEntry base =
                            SchemaUtils.getComplexElementExtensionBase(te.getNode(),
                                    symbolTable);

                    while (base != null) {
                        base.setDynamicVar(
                                JavaGeneratorFactory.COMPLEX_TYPE_FAULT,
                                Boolean.TRUE);

                        base = SchemaUtils.getComplexElementExtensionBase(
                                base.getNode(), symbolTable);
                    }
                }

                // The exception class name is the name of the type
                exceptionClassName = te.getName();
            }
        }

        String excName = getExceptionJavaNameHook(fault.getMessage().getQName());     //     for derived class
        if (excName != null) {
            exceptionClassName = excName;
        }

        // Set the name of the exception and
        // whether the exception is a complex type
        MessageEntry me =
                symbolTable.getMessageEntry(fault.getMessage().getQName());

        if (me != null) {
            me.setDynamicVar(JavaGeneratorFactory.EXCEPTION_DATA_TYPE,
                    faultXmlType);

            if (exceptionClassName != null) {
                me.setDynamicVar(JavaGeneratorFactory.COMPLEX_TYPE_FAULT,
                        Boolean.TRUE);
                me.setDynamicVar(JavaGeneratorFactory.EXCEPTION_CLASS_NAME,
                        exceptionClassName);
            } else {
                me.setDynamicVar(JavaGeneratorFactory.EXCEPTION_CLASS_NAME,
                        emitter.getJavaName(me.getQName()));
            }
        }
    }

    protected String getExceptionJavaNameHook(QName qname) {
        return null;
    }

    /**
     * Method determineInterfaceNames
     *
     * @param symbolTable
     */
    protected void determineInterfaceNames(SymbolTable symbolTable) {

        Iterator it = symbolTable.getHashMap().values().iterator();

        while (it.hasNext()) {
            Vector v = (Vector) it.next();

            for (int i = 0; i < v.size(); ++i) {
                SymTabEntry entry = (SymTabEntry) v.elementAt(i);

                if (entry instanceof BindingEntry) {

                    // The SEI (Service Endpoint Interface) name
                    // is always the portType name.
                    BindingEntry bEntry = (BindingEntry) entry;
                    String seiName = null;
                    PortTypeEntry ptEntry = symbolTable.getPortTypeEntry(
                            bEntry.getBinding().getPortType().getQName());

                    seiName = getServiceEndpointInterfaceJavaNameHook(ptEntry, bEntry);
                    if (seiName == null) {
                        seiName = ptEntry.getName();
                    }

                    bEntry.setDynamicVar(JavaBindingWriter.INTERFACE_NAME,
                            seiName);
                } else if (entry instanceof ServiceEntry) {
                    ServiceEntry sEntry = (ServiceEntry) entry;
                    String siName = getServiceInterfaceJavaNameHook(sEntry);    // for derived class
                    if (siName != null) {
                        sEntry.setName(siName);
                    }

                    Service service = sEntry.getService();
                    Map portMap = service.getPorts();
                    Iterator portIterator = portMap.values().iterator();

                    while (portIterator.hasNext()) {
                        Port p = (Port) portIterator.next();

                        Binding binding = p.getBinding();
                        BindingEntry bEntry =
                                symbolTable.getBindingEntry(binding.getQName());

                        // If this isn't a SOAP binding, skip it
                        if (bEntry.getBindingType() != BindingEntry.TYPE_SOAP) {
                            continue;
                        }

                        String portName = getPortJavaNameHook(p.getName());   // for derived class
                        if (portName != null) {
                            bEntry.setDynamicVar(JavaServiceWriter.PORT_NAME + ":" + p.getName(),
                                    portName);
                        }
                    }
                }
            }
        }
    }    // determineInterfaceNames

    protected String getServiceEndpointInterfaceJavaNameHook(PortTypeEntry ptEntry, BindingEntry bEntry) {
        return null;
    }

    protected String getServiceInterfaceJavaNameHook(ServiceEntry sEntry) {
        return null;
    }

    protected String getPortJavaNameHook(String portName) {
        return null;
    }

    /**
     * Messages, PortTypes, Bindings, and Services can share the same name.  If they do in this
     * Definition, force their names to be suffixed with _PortType and _Service, respectively.
     *
     * @param symbolTable
     */
    protected void resolveNameClashes(SymbolTable symbolTable) {

        // Keep a list of anonymous types so we don't try to resolve them twice.
        HashSet anonTypes = new HashSet();
        Iterator it = symbolTable.getHashMap().values().iterator();

        while (it.hasNext()) {
            Vector v = new Vector(
                    (Vector) it.next());    // New vector we can temporarily add to it

            // Remove MessageEntries since they are not mapped
            int index = 0;

            while (index < v.size()) {
                if (v.elementAt(index) instanceof MessageEntry) {
                    v.removeElementAt(index);
                } else {
                    index++;
                }
            }

            if (v.size() > 1) {
                boolean resolve = true;

                // Common Special Case:
                // If a Type and Element have the same QName, and the Element
                // references the Type, then they are the same class so
                // don't bother mangling.
                if ((v.size() == 2) && (((v.elementAt(
                        0) instanceof Element) && (v.elementAt(
                                1) instanceof Type)) || ((v.elementAt(
                                        1) instanceof Element) && (v.elementAt(
                                                0) instanceof Type)))) {
                    Element e = null;

                    if (v.elementAt(0) instanceof Element) {
                        e = (Element) v.elementAt(0);
                    } else {
                        e = (Element) v.elementAt(1);
                    }

                    BooleanHolder forElement = new BooleanHolder();
                    QName eType = Utils.getTypeQName(e.getNode(),
                            forElement, false);

                    if ((eType != null) && eType.equals(e.getQName())
                            && !forElement.value) {
                        resolve = false;
                    }
                }

                // Other Special Case:
                // If the names are already different, no mangling is needed.
                if (resolve) {
                    resolve = false;           // Assume false

                    String name = null;

                    for (int i = 0; (i < v.size()) && !resolve; ++i) {
                        SymTabEntry entry = (SymTabEntry) v.elementAt(i);

                        if ((entry instanceof MessageEntry)
                                || (entry instanceof BindingEntry)) {
                            ;                  // Don't process these
                        } else if (name == null) {
                            name = entry.getName();
                        } else if (name.equals(entry.getName())) {
                            resolve = true;    // Need to do resolution
                        }
                    }
                }

                // Full Mangle if resolution is necessary.
                if (resolve) {
                    boolean firstType = true;

                    for (int i = 0; i < v.size(); ++i) {
                        SymTabEntry entry = (SymTabEntry) v.elementAt(i);

                        if (entry instanceof Element) {
                            entry.setName(mangleName(entry.getName(),
                                    "_ElemType"));

                            // If this global element was defined using
                            // an anonymous type, then need to change the
                            // java name of the anonymous type to match.
                            QName anonQName =
                                    new QName(entry.getQName().getNamespaceURI(),
                                            SymbolTable.ANON_TOKEN
                                    + entry.getQName().getLocalPart());
                            TypeEntry anonType =
                                    symbolTable.getType(anonQName);

                            if (anonType != null) {
                                anonType.setName(entry.getName());
                                anonTypes.add(anonType);
                            }
                        } else if (entry instanceof TypeEntry) {

                            // Search all other types for java names that match this one.
                            // The sameJavaClass method returns true if the java names are
                            // the same (ignores [] ).
                            if (firstType) {
                                firstType = false;

                                Iterator types =
                                        symbolTable.getTypeIndex().values().iterator();

                                while (types.hasNext()) {
                                    TypeEntry type = (TypeEntry) types.next();

                                    if ((type != entry)
                                            && (type.getBaseType() == null)
                                            && sameJavaClass(entry.getName(),
                                                    type.getName())) {
                                        v.add(type);
                                    }
                                }
                            }

                            // If this is an anonymous type, it's name was resolved in
                            // the previous if block.  Don't reresolve it.
                            if (!anonTypes.contains(entry)) {
                                entry.setName(mangleName(entry.getName(),
                                        "_Type"));
                            }
                        } else if (entry instanceof PortTypeEntry) {
                            entry.setName(mangleName(entry.getName(), "_Port"));
                        } else if (entry instanceof ServiceEntry) {
                            entry.setName(mangleName(entry.getName(),
                                    "_Service"));
                        }

                        // else if (entry instanceof MessageEntry) {
                        // we don't care about messages
                        // }
                        else if (entry instanceof BindingEntry) {
                            BindingEntry bEntry = (BindingEntry) entry;

                            // If there is no literal use, then we never see a
                            // class named directly from the binding name.  They
                            // all have suffixes:  Stub, Skeleton, Impl.
                            // If there IS literal use, then the SDI will be
                            // named after the binding name, so there is the
                            // possibility of a name clash.
                            if (bEntry.hasLiteral()) {
                                entry.setName(mangleName(entry.getName(),
                                        "_Binding"));
                            }
                        }
                    }
                }
            }
        }
    }                                          // resolveNameClashes

    /**
     * Change the indicated type name into a mangled form using the mangle string.
     *
     * @param name
     * @param mangle
     * @return
     */
    private String mangleName(String name, String mangle) {

        int index = name.indexOf("[");

        if (index >= 0) {
            String pre = name.substring(0, index);
            String post = name.substring(index);

            return pre + mangle + post;
        } else {
            return name + mangle;
        }
    }

    /**
     * Returns true if same java class, ignore []
     *
     * @param one
     * @param two
     * @return
     */
    private boolean sameJavaClass(String one, String two) {

        int index1 = one.indexOf("[");
        int index2 = two.indexOf("[");

        if (index1 > 0) {
            one = one.substring(0, index1);
        }

        if (index2 > 0) {
            two = two.substring(0, index2);
        }

        return one.equals(two);
    }

    /**
     * The --all flag is set on the command line (or generateAll(true) is called
     * on WSDL2Java). Set all symbols as referenced (except nonSOAP bindings
     * which we don't know how to deal with).
     */
    protected void setAllReferencesToTrue() {

        Iterator it = symbolTable.getHashMap().values().iterator();

        while (it.hasNext()) {
            Vector v = (Vector) it.next();

            for (int i = 0; i < v.size(); ++i) {
                SymTabEntry entry = (SymTabEntry) v.elementAt(i);

                if ((entry instanceof BindingEntry)
                        && ((BindingEntry) entry).getBindingType()
                        != BindingEntry.TYPE_SOAP) {
                    entry.setIsReferenced(false);
                } else {
                    entry.setIsReferenced(true);
                }
            }
        }
    }    // setAllReferencesToTrue

    /**
     * If a binding's type is not TYPE_SOAP, then we don't use that binding
     * or that binding's portType.
     *
     * @param symbolTable
     */
    protected void ignoreNonSOAPBindings(SymbolTable symbolTable) {

        // Look at all uses of the portTypes.  If none of the portType's bindings are of type
        // TYPE_SOAP, then turn off that portType's isReferenced flag.
        Vector unusedPortTypes = new Vector();
        Vector usedPortTypes = new Vector();
        Iterator it = symbolTable.getHashMap().values().iterator();

        while (it.hasNext()) {
            Vector v = (Vector) it.next();

            for (int i = 0; i < v.size(); ++i) {
                SymTabEntry entry = (SymTabEntry) v.elementAt(i);

                if (entry instanceof BindingEntry) {
                    BindingEntry bEntry = (BindingEntry) entry;
                    Binding binding = bEntry.getBinding();
                    PortType portType = binding.getPortType();
                    PortTypeEntry ptEntry =
                            symbolTable.getPortTypeEntry(portType.getQName());

                    if (bEntry.getBindingType() == BindingEntry.TYPE_SOAP) {

                        // If a binding is of type TYPE_SOAP, then mark its portType used
                        // (ie., add it to the usedPortTypes list.  If the portType was
                        // previously marked as unused, unmark it (in other words, remove it
                        // from the unusedPortTypes list).
                        usedPortTypes.add(ptEntry);

                        if (unusedPortTypes.contains(ptEntry)) {
                            unusedPortTypes.remove(ptEntry);
                        }
                    } else {
                        bEntry.setIsReferenced(false);

                        // If a binding is not of type TYPE_SOAP, then mark its portType as
                        // unused ONLY if it hasn't already been marked as used.
                        if (!usedPortTypes.contains(ptEntry)) {
                            unusedPortTypes.add(ptEntry);
                        }
                    }
                }
            }
        }

        // Go through all the portTypes that are marked as unused and set their isReferenced flags
        // to false.
        for (int i = 0; i < unusedPortTypes.size(); ++i) {
            PortTypeEntry ptEntry = (PortTypeEntry) unusedPortTypes.get(i);

            ptEntry.setIsReferenced(false);
        }
    }    // ignoreNonSOAPBindings

    /**
     * Method constructSignatures
     *
     * @param symbolTable
     */
    protected void constructSignatures(SymbolTable symbolTable) {

        Iterator it = symbolTable.getHashMap().values().iterator();

        while (it.hasNext()) {
            Vector v = (Vector) it.next();

            for (int i = 0; i < v.size(); ++i) {
                SymTabEntry entry = (SymTabEntry) v.elementAt(i);

                if (entry instanceof BindingEntry) {
                    BindingEntry bEntry = (BindingEntry) entry;
                    Binding binding = bEntry.getBinding();
                    PortTypeEntry ptEntry = symbolTable.getPortTypeEntry(
                            binding.getPortType().getQName());
                    PortType portType = ptEntry.getPortType();
                    Iterator operations =
                            portType.getOperations().iterator();

                    while (operations.hasNext()) {
                        Operation operation =
                                (Operation) operations.next();
                        String wsdlOpName = operation.getName();
                        OperationType type = operation.getStyle();

                        String javaOpName = getOperationJavaNameHook(bEntry, wsdlOpName);      // for derived class
                        if (javaOpName == null) {
                            javaOpName = operation.getName();
                        }

                        Parameters parameters =
                                bEntry.getParameters(operation);

                        if (type == OperationType.SOLICIT_RESPONSE) {
                            parameters.signature =
                                    "    // "
                                    + Messages.getMessage("invalidSolResp00", javaOpName);

                            System.err.println(
                                    Messages.getMessage("invalidSolResp00", javaOpName));
                        } else if (type == OperationType.NOTIFICATION) {
                            parameters.signature =
                                    "    // "
                                    + Messages.getMessage("invalidNotif00", javaOpName);

                            System.err.println(
                                    Messages.getMessage("invalidNotif00", javaOpName));
                        } else {    // ONE_WAY or REQUEST_RESPONSE
                            if (parameters != null) {
                                String returnType = getReturnTypeJavaNameHook(bEntry, wsdlOpName);
                                if (returnType != null) {
                                    if (parameters.returnParam != null) {   // 'void' return type???
                                        parameters.returnParam.getType().setName(returnType);
                                    }
                                }
                                for (int j = 0; j < parameters.list.size(); ++j) {
                                    Parameter p = (Parameter) parameters.list.get(j);
                                    String paramType = getParameterTypeJavaNameHook(bEntry, wsdlOpName, j);
                                    if (paramType != null) {
                                        p.getType().setName(paramType);
                                    }
                                }
                                parameters.signature =
                                        constructSignature(parameters, javaOpName);
                            }
                        }
                    }
                }
            }
        }
    }                               // constructSignatures

    protected String getOperationJavaNameHook(BindingEntry bEntry, String wsdlOpName) {
        return null;
    }

    protected String getReturnTypeJavaNameHook(BindingEntry bEntry, String wsdlOpName) {
        return null;
    }

    protected String getParameterTypeJavaNameHook(BindingEntry bEntry, String wsdlOpName, int pos) {
        return null;
    }

    /**
     * Construct the signature, which is used by both the interface and the stub.
     *
     * @param parms
     * @param opName
     * @return
     */
    private String constructSignature(Parameters parms, String opName) {

        String name = Utils.xmlNameToJava(opName);
        String ret = "void";

        if ((parms != null) && (parms.returnParam != null)) {
            ret = Utils.getParameterTypeName(parms.returnParam);
        }

        String signature = "    public " + ret + " " + name + "(";
        boolean needComma = false;

        for (int i = 0; (parms != null) && (i < parms.list.size()); ++i) {
            Parameter p = (Parameter) parms.list.get(i);

            if (needComma) {
                signature = signature + ", ";
            } else {
                needComma = true;
            }

            String javifiedName = Utils.xmlNameToJava(p.getName());

            if (p.getMode() == Parameter.IN) {
                signature = signature + Utils.getParameterTypeName(p) + " "
                        + javifiedName;
            } else {
                signature =
                        signature
                        + Utils.holder(p.getMIMEInfo(), p.getType(), emitter) + " "
                        + javifiedName;
            }
        }

        signature = signature + ") throws java.rmi.RemoteException";

        if ((parms != null) && (parms.faults != null)) {

            // Collect the list of faults into a single string, separated by commas.
            Iterator i = parms.faults.values().iterator();

            while (i.hasNext()) {
                Fault fault = (Fault) i.next();
                String exceptionName =
                        Utils.getFullExceptionName(fault.getMessage(), symbolTable);

                if (exceptionName != null) {
                    signature = signature + ", " + exceptionName;
                }
            }
        }

        return signature;
    }    // constructSignature

    /**
     * Find all inout/out parameters and add a flag to the Type of that parameter saying a holder
     * is needed.
     *
     * @param symbolTable
     */
    protected void determineIfHoldersNeeded(SymbolTable symbolTable) {

        Iterator it = symbolTable.getHashMap().values().iterator();

        while (it.hasNext()) {
            Vector v = (Vector) it.next();

            for (int i = 0; i < v.size(); ++i) {
                if (v.get(i) instanceof BindingEntry) {

                    // If entry is a BindingEntry, look at all the Parameters
                    // in its portType
                    BindingEntry bEntry = (BindingEntry) v.get(i);

                    // PortTypeEntry ptEntry =
                    // symbolTable.getPortTypeEntry(bEntry.getBinding().getPortType().getQName());
                    Iterator operations =
                            bEntry.getParameters().values().iterator();

                    while (operations.hasNext()) {
                        Parameters parms = (Parameters) operations.next();

                        for (int j = 0; j < parms.list.size(); ++j) {
                            Parameter p = (Parameter) parms.list.get(j);

                            // If the given parameter is an inout or out parameter, then
                            // set a HOLDER_IS_NEEDED flag using the dynamicVar design.
                            if (p.getMode() != Parameter.IN) {
                                TypeEntry typeEntry = p.getType();

                                typeEntry.setDynamicVar(
                                        JavaTypeWriter.HOLDER_IS_NEEDED,
                                        Boolean.TRUE);

                                // If this is a complex then set the HOLDER_IS_NEEDED
                                // for the reftype too.
                                if (!typeEntry.isSimpleType()
                                        && (typeEntry.getRefType() != null)) {
                                    typeEntry.getRefType().setDynamicVar(
                                            JavaTypeWriter.HOLDER_IS_NEEDED,
                                            Boolean.TRUE);
                                }

                                // If the type is a DefinedElement, need to
                                // set HOLDER_IS_NEEDED on the anonymous type.
                                QName anonQName =
                                        SchemaUtils.getElementAnonQName(
                                                p.getType().getNode());

                                if (anonQName != null) {
                                    TypeEntry anonType =
                                            symbolTable.getType(anonQName);

                                    if (anonType != null) {
                                        anonType.setDynamicVar(
                                                JavaTypeWriter.HOLDER_IS_NEEDED,
                                                Boolean.TRUE);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }    // determineIfHoldersNeeded

    /**
     * Get TypeMapping to use for translating
     * QNames to java base types
     */
    BaseTypeMapping btm = null;

    /**
     * Method setBaseTypeMapping
     *
     * @param btm
     */
    public void setBaseTypeMapping(BaseTypeMapping btm) {
        this.btm = btm;
    }

    /**
     * Method getBaseTypeMapping
     *
     * @return
     */
    public BaseTypeMapping getBaseTypeMapping() {

        if (btm == null) {
            btm = new BaseTypeMapping() {

                TypeMapping defaultTM = DefaultTypeMappingImpl.getSingleton();

                public String getBaseName(QName qNameIn) {

                    javax.xml.namespace.QName qName =
                            new javax.xml.namespace.QName(qNameIn.getNamespaceURI(),
                                    qNameIn.getLocalPart());
                    Class cls =
                            defaultTM.getClassForQName(qName);

                    if (cls == null) {
                        return null;
                    } else {
                        return JavaUtils.getTextClassName(cls.getName());
                    }
                }
            };
        }

        return btm;
    }

    /**
     * Determines whether the QName supplied should be generated by comparing
     * the namespace for the QName against the included and excluded names.
     <p/>
     <ul>
     <li>if both the includes and excludes are both empty,
             the element is generated</li>
     <li>if the namespace is in the includes,
             the element is generated</li>
     <li>if the namespace is not in the excludes and the includes are empty,
             the element will be generated.
     <li>if the namespace is only in the excludes,
             the element is not generated</li>
     <li>if the namespace is not in the includes and the includes are not
             empty, the element is not generated</li>
        @param qName
        @return
     */
    protected boolean include(QName qName) {
        String namespace =
            (qName != null && qName.getNamespaceURI() != null)
                ? qName.getNamespaceURI()
                : "";

        boolean doInclude = false;
        NamespaceSelector selector = new NamespaceSelector(namespace);
        if (qName == null
            || emitter == null
            || emitter.getNamespaceIncludes().contains(selector)
            || (emitter.getNamespaceIncludes().size() == 0
                && !emitter.getNamespaceExcludes().contains(selector))) {
            doInclude = true;
        }
        else {
            log_.info(
                "excluding code generation for non-included QName:" + qName);

        }
        return doInclude;
    }

}    // class JavaGeneratorFactory
