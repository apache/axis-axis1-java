<%@ page import="java.io.InputStream,
                 java.io.IOException"%>
<%!

    /*
     * Happiness tests for axis. These look at the classpath and warn if things
     * are missing. Normally addng this much code in a JSP page is mad
     * but here we want to validate JSP compilation too, and have a drop-in
     * page for easy re-use
     * @author Steve 'configuration problems' Loughran
     */


    /**
     * Get a string providing install information.
     * TODO: make this platform aware and give specific hints
     */
    public String getInstallHints(HttpServletRequest request) {
        String hint=
            "On Tomcat 4.x, you may need to put libraries that contain "
            +"java.* or javax.* packages into CATALINA_HOME/commons/lib";
        return hint;
    }

    /**
     * test for a class existing
     * @param classname
     * @return true iff present
     */
    boolean classExists(String classname) {
        try {
            Class t = Class.forName(classname);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * test for resource on the classpath
     * @param resource
     * @return true iff present
     */
    boolean resourceExists(String resource) {
        boolean found;
        InputStream instream=this.getClass().getResourceAsStream(resource);
        found=instream!=null;
        if(instream!=null) {
            try {
                instream.close();
            } catch (IOException e) {
            }
        }
        return found;
    }

    /**
     * probe for a class, print an error message is missing
     * @param out stream to print stuff
     * @param category text like "warning" or "error"
     * @param classname class to look for
     * @param jarFile where this class comes from
     * @param errorText extra error text
     * @param homePage where to d/l the library
     * @return the number of missing classes
     * @throws IOException
     */
    int probeClass(JspWriter out,
                   String category,
                   String classname,
                   String jarFile,
                   String errorText,
                   String homePage) throws IOException {

       if(!classExists(classname))  {
            String url="";
            if(homePage!=null) {
                url="<br>  See <a href="+homePage+">"+homePage+"</a>";
            }
            out.write("<p>"+category+": could not find class "+classname
                    +" from file "+jarFile
                    +"<br>  "+errorText
                    +url
                    +"<p>");
            return 1;
        } else {
            out.write("Found "+jarFile+"<br>");
            return 0;
        }
    }

    /**
     * a class we need if a class is missing
     * @param out stream to print stuff
     * @param classname class to look for
     * @param jarFile where this class comes from
     * @param errorText extra error text
     * @param homePage where to d/l the library
     * @throws IOException when needed
     * @return the number of missing libraries (0 or 1)
     */
    int needClass(JspWriter out,
                   String classname,
                   String jarFile,
                   String errorText,
                   String homePage) throws IOException {
        return probeClass(out,
                "<b>Error</b>",
                classname,
                jarFile,
                errorText,
                homePage);
    }

    /**
     * print warning message if a class is missing
     * @param out stream to print stuff
     * @param classname class to look for
     * @param jarFile where this class comes from
     * @param errorText extra error text
     * @param homePage where to d/l the library
     * @throws IOException when needed
     * @return the number of missing libraries (0 or 1)
     */
    int wantClass(JspWriter out,
                   String classname,
                   String jarFile,
                   String errorText,
                   String homePage) throws IOException {
        return probeClass(out,
                "<b>Warning</b>",
                classname,
                jarFile,
                errorText,
                homePage);
    }

    /**
     * probe for a resource existing,
     * @param out
     * @param resource
     * @param errorText
     * @throws Exception
     */
    int wantResource(JspWriter out,
                      String resource,
                      String errorText) throws Exception {
        if(!resourceExists(resource)) {
            out.write("<p><b>Warning</b>: could not find resource "+resource
                        +"<br>"
                        +errorText);
            return 0;
        } else {
            out.write("found "+resource+"<br>");
            return 1;
        }
    }
    %>
<html><head><title>Axis Happiness Page</title></head>
<body>
<h2>Examining webapp configuration</h2>

<p>
<h3>Needed Components</h3>
<%
    int needed,wanted;

    /**
     * the essentials, without these Axis is not going to work
     */
    needed=needClass(out, "javax.xml.soap.SOAPMessage",
            "saaj.jar",
            "from Apache or Sun",
            "http://xml.apache.org/axis/");

    needed+=needClass(out, "javax.xml.rpc.Service",
            "jaxrpc.jar",
            "JAX-RPC API",
            "http://xml.apache.org/axis/");

    needed+=needClass(out, "org.apache.axis.transport.http.AxisServlet",
            "axis.jar",
            "Apache-Axis itself",
            "http://xml.apache.org/axis/");

    needed+=needClass(out, "org.apache.commons.logging.Log",
            "commons-logging.jar",
            "Jakarta-commons logging",
            "http://jakarta.apache.org/commons/logging.html");

    //should we search for a javax.wsdl file here, to hint that it needs
    //to go into an approved directoy? because we dont seem to need to do that.
    needed+=needClass(out, "com.ibm.wsdl.factory.WSDLFactoryImpl",
            "wsdl4j.jar",
            "IBM's WSDL4Java",
            null);

%>
<h3>Optional Components</h3>
<%
    /*
     * now the stuff we can live without
     */

    wanted=wantClass(out,"javax.activation.DataHandler",
            "activation.jar",
            "Soap With Attachments will not work.",
            "http://java.sun.com/products/javabeans/glasgow/jaf.html");

    wanted+=wantClass(out,"javax.mail.internet.MimeMessage",
            "mail.jar",
            "Soap With Attachments will not work;",
            "http://java.sun.com/products/javamail/");

    wanted+=wantClass(out,"org.apache.xml.security.Init",
            "xmlsec.jar",
            "XML security is not supported;",
            "http://xml.apache.org/security/");

    /*
     * resources on the classpath path
     */
    wantResource(out,"server-config.wsdd",
            "There is no a server configuration file;"
            +"run AdminClient to create one");

    /* add more libraries here */

    //is everythng we need here
    if(needed==0) {
       //yes, be happy
        out.write("<h2 align=center><i>The core axis libraries are present</i></h2>");
    } else {
        //no, be very unhappy
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        out.write("<h2 align=center><i>"
                +needed
                +" core axis librar"
                +(needed==1?"y is":"ies are")
                +" missing</i></h2>");
    }
    //now look at wanted stuff
    if(wanted>0) {
        out.write("<h2 align=center>"
                +wanted
                +" optional axis librar"
                +(wanted==1?"y is":"ies are")
                +" missing</i></h2>");
    } else {
        out.write("<h2 align=center>The optonal components are present</h2>");
    }
    //hint if anything is missing
    if(needed>0 || wanted>0 ) {
       out.write(getInstallHints(request));
    }

    %>
    <p>
    <hr>
    <p>
    Even if everything this page probes for is present, there is no guarantee your
    web service will work, because there are many configuration options that we do
    not check for. These tests are <i>necessary</i> but not <i>sufficient</i>
</body>
</html>


