/*
 * Copyright 2002-2004 The Apache Software Foundation.
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
package test.holders;

import org.apache.axis.client.Call;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.enum.Style;
import org.apache.axis.enum.Use;
import test.GenericLocalTest;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Confirm that faults using beans work 
 */
public class TestBook extends GenericLocalTest {
    private QName TYPE_ARRAY_OF_BOOK =
            new QName("http://holdertest.org/xsd", "ArrayOfBook");
    private QName TYPE_BOOK = new QName("http://holdertest.org/xsd", "Book");

    public TestBook() {
        super("service");
    }

    public TestBook(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
        super.setUp(false); // don't deploy here
        TypeMapping tm = (TypeMapping)config.getTypeMappingRegistry().
                        getDefaultTypeMapping();
        tm.register(Book.class, TYPE_BOOK,
                new BeanSerializerFactory(Book.class, TYPE_BOOK),
                new BeanDeserializerFactory(Book.class, TYPE_BOOK));
        tm.register(ArrayOfBook.class, TYPE_ARRAY_OF_BOOK,
                new BeanSerializerFactory(ArrayOfBook.class,
                        TYPE_ARRAY_OF_BOOK),
                new BeanDeserializerFactory(ArrayOfBook.class,
                        TYPE_ARRAY_OF_BOOK));
        deploy("service", this.getClass(), Style.RPC, Use.LITERAL);
    }

    public void testInOutBook() throws Exception {
        Call call = getCall();
        call.setOperationStyle("rpc");
        call.setOperationUse("literal");
        call.setEncodingStyle("");
        call.registerTypeMapping(Book.class, TYPE_BOOK,
                new BeanSerializerFactory(Book.class, TYPE_BOOK),
                new BeanDeserializerFactory(Book.class, TYPE_BOOK));
        call.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        call.addParameter("varBook", TYPE_BOOK, ParameterMode.INOUT);
        Book data = new Book();
        data.setAuthor("author1");
        data.setTitle("title1");
        data.setIsbn(1);
        call.invoke("echoInOutBook", new Object []{data});
        List l = call.getOutputValues();
        assertEquals(1, l.size());
        assertEquals("author2", ((Book)l.get(0)).getAuthor());
        assertEquals("title2", ((Book)l.get(0)).getTitle());
        assertEquals(2, ((Book)l.get(0)).getIsbn());
    }

    public void testInOutBookArray() throws Exception {
        Call call = getCall();
        call.setOperationStyle("rpc");
        call.setOperationUse("literal");
        call.setEncodingStyle("");
        call.registerTypeMapping(ArrayOfBook.class, TYPE_ARRAY_OF_BOOK,
                new BeanSerializerFactory(ArrayOfBook.class,
                        TYPE_ARRAY_OF_BOOK),
                new BeanDeserializerFactory(ArrayOfBook.class,
                        TYPE_ARRAY_OF_BOOK));
        call.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        call.addParameter("varBook", TYPE_ARRAY_OF_BOOK, ParameterMode.INOUT);
        Book b0 = new Book();
        b0.setAuthor("author0");
        b0.setTitle("title0");
        b0.setIsbn(0);
        Book b1 = new Book();
        b1.setAuthor("author1");
        b1.setTitle("title1");
        b1.setIsbn(1);
        Book b[] = new Book[2];
        b[0] = b0;
        b[1] = b1;
        ArrayOfBook aob = new ArrayOfBook();
        aob.setArrayOfBook(b);
        call.invoke("echoInOutBookArray", new Object []{aob});
        List l = call.getOutputValues();
        assertEquals(1, l.size());
        ArrayOfBook aob2 = (ArrayOfBook)l.get(0);
        Book b2[] = aob2.getArrayOfBook();
        assertEquals(2, b2.length);
        assertEquals(b2[0].getAuthor(), b[1].getAuthor());
        assertEquals(b2[1].getTitle(), b[0].getTitle());
    }

    public void echoInOutBook(test.holders.holders.BookHolder varBook)
            throws java.rmi.RemoteException {
        Book b = varBook.value;
        b.setAuthor("author2");
        b.setTitle("title2");
        b.setIsbn(2);
        varBook.value = b;
    }

    public void echoInOutBookArray(test.holders.holders.ArrayOfBookHolder varBook)
            throws java.rmi.RemoteException {
        ArrayOfBook v = varBook.value;
        Book[] b = v.getArrayOfBook();
        if (b.length != 2) throw new RemoteException("array size not 2");
        String author = b[0].getAuthor();
        String title = b[0].getTitle();
        int isbn = b[0].getIsbn();
        b[0].setAuthor(b[1].getAuthor());
        b[0].setTitle(b[1].getTitle());
        b[0].setIsbn(b[1].getIsbn());
        b[1].setAuthor(author);
        b[1].setTitle(title);
        b[1].setIsbn(isbn);
        v.setArrayOfBook(b);
        varBook.value = v;
    }
}
