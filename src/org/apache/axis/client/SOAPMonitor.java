/*
 * Copyright 2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache Liusercense, Version 2.0 (the "License");
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

package org.apache.axis.client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

import javax.xml.parsers.ParserConfigurationException;

import java.lang.reflect.InvocationTargetException;

import org.apache.axis.utils.Options;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.client.AdminClient;
import org.apache.axis.monitor.SOAPMonitorConstants;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Element;

/**
 * This is a SOAP Mointor Application class.  This class provides
 * the user interface for deploying the SOAP monitor service and
 * displaying data from the service.
 *
 * @author Toshiyuki Kimura (toshi@apache.org)
 * @author Brian Price (pricebe@us.ibm.com)
 *
 */
public class SOAPMonitor extends JFrame implements ActionListener {

    /**
     * Private data
     */
    private JPanel      main_panel = null;
    private JTabbedPane tabbed_pane = null;
    private JTabbedPane top_pane    = null;
    private int         port = 5001;
    private String      axisHost = "localhost";
    private int         axisPort = 8080;
    private String      axisURI  = null;
    private Vector      pages = null;
    private final String titleStr = "SOAP Monitor Administration";

	private JPanel set_panel = null;
    private JLabel titleLabel = null;
	
	private JButton add_btn = null;
	private JButton del_btn = null;
	private JButton save_btn = null;
	private JButton login_btn = null;

	private DefaultListModel model1 = null;
	private DefaultListModel model2 = null;
	
	private JList list1 = null;
	private JList list2 = null;
    
    private HashMap serviceMap = null;
    private Document originalDoc = null;
    
    private static String axisUser = null;
    private static String axisPass = null;
	
    private AdminClient adminClient = new AdminClient();

    /**
     * Main method for this class
     */
    public static void main(String args[]) throws Exception {
		SOAPMonitor soapMonitor = null;
        
        Options opts = new Options( args );
        if ( opts.isFlagSet('?') > 0) {
            System.out.println("Usage: SOAPMonitor [-l<url>] [-?]");
            System.exit(0);
        }
        
        // Create an instance
        soapMonitor = new SOAPMonitor();
        
        // GET Axis URI.
        // The default is "http://localhost:8080/axis/servlet/AxisServlet"
        soapMonitor.axisURI = opts.getURL();
        URI uri = new URI(soapMonitor.axisURI);
        soapMonitor.axisHost = uri.getHost();

        // GET User name & Password
        axisUser = opts.getUser();
        axisPass = opts.getPassword();
  
        // Login and start application
        if (soapMonitor.doLogin()) {
            soapMonitor.start();
        }
	}
		
    /**
     * Constructor
     */
    public SOAPMonitor() {
        setTitle("SOAP Monitor Application");
        Dimension d = getToolkit().getScreenSize();
        setSize(640,480);
        setLocation((d.width-getWidth())/2,(d.height-getHeight())/2);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new MyWindowAdapter());

        // Try to use the system look and feel
        try {
           UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e){
        }
        
        // Create main panel to hold notebook
        main_panel = new JPanel();
        main_panel.setBackground(Color.white);
        main_panel.setLayout(new BorderLayout());
        
        top_pane = new JTabbedPane();
        set_panel = new JPanel();
        
        // label for NORTH panel to display the pain title
        titleLabel = new JLabel(titleStr);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 18));

        // list control for WEST panel to list NOT monitored services 
        model1 = new DefaultListModel();
        list1 = new JList(model1);
        list1.setFixedCellWidth(250);
        JScrollPane scroll1 = new JScrollPane(list1);

        // list control for EAST panel to list monitored services
        model2 = new DefaultListModel();
        list2 = new JList(model2);
        list2.setFixedCellWidth(250);
        JScrollPane scroll2 = new JScrollPane(list2);
        
        // buttons for CENTER panel to chage the monitoring state
        add_btn = new JButton("Turn On [ >> ]");
        del_btn = new JButton("[ << ] Turn Off");

        JPanel center_panel = new JPanel();
        GridBagLayout layout=new GridBagLayout();
        center_panel.setLayout(layout);
        GridBagConstraints c=new GridBagConstraints();
        c.gridx=0;
        c.gridy=0;
        c.insets = new Insets(10,10,10,10);
        layout.setConstraints(add_btn,c);
        center_panel.add(add_btn);
        c.gridx=0;
        c.gridy=1;
        c.insets = new Insets(10,10,10,10);
        layout.setConstraints(del_btn,c);
        center_panel.add(del_btn);

        // buttons for SOUTH panel
        save_btn = new JButton("Save changes");
        login_btn = new JButton("Change server");

        JPanel south_panel = new JPanel();
        layout=new GridBagLayout();
        c.gridx=0;
        c.gridy=0;
        c.insets = new Insets(10,10,10,10);
        layout.setConstraints(save_btn,c);
        south_panel.add(save_btn);
        c.gridx=1;
        c.gridy=0;
        c.insets = new Insets(10,10,10,10);
        layout.setConstraints(login_btn,c);
        south_panel.add(login_btn);
        
        // set all controls to the border layout
        set_panel.setLayout(new BorderLayout(5, 5));
        set_panel.add(titleLabel, BorderLayout.NORTH);
        set_panel.add(south_panel, BorderLayout.SOUTH);
        set_panel.add(scroll1, BorderLayout.WEST);
        set_panel.add(scroll2, BorderLayout.EAST);
        set_panel.add(center_panel, BorderLayout.CENTER);

        // register the Action Listener
        add_btn.addActionListener(this);
        del_btn.addActionListener(this);
        save_btn.addActionListener(this);
        login_btn.addActionListener(this);

        // set default button state as 'false'
        add_btn.setEnabled(false);
        del_btn.setEnabled(false);
        save_btn.setEnabled(false);
        login_btn.setEnabled(false);

        top_pane.add("Setting", set_panel);
        top_pane.add("Monitoring", main_panel);
        getContentPane().add(top_pane);
        
        // Create the notebook
        tabbed_pane = new JTabbedPane(JTabbedPane.TOP);
        main_panel.add(tabbed_pane,BorderLayout.CENTER);
        top_pane.setEnabled(false);

        setVisible(true);
    }
    
    /**
     * Do login process
     */
    private boolean doLogin() {
        Dimension d = null;
        
        // Login
        LoginDlg login = new LoginDlg();
        login.show();

        if (!login.isLogin()) {
            login_btn.setEnabled(true);
            return false;
        }
        login.dispose();

        save_btn.setEnabled(false);
        login_btn.setEnabled(false);

        // Get the axisHost & axisPort to be used
        String uri_str = login.getURI();
        try {
            URI uri = new URI( uri_str );
            axisHost = uri.getHost();
            axisPort = uri.getPort();
            if (axisPort==-1) {
                axisPort = 8080;
            }
            String axisPath = uri.getPath();
            axisURI = "http://"+ axisHost +":"+ axisPort + axisPath;
        } catch (URISyntaxException e) {}
        titleLabel.setText(titleStr + " for ["+axisHost+":"+axisPort+"]");

        final JProgressBar progressBar = new JProgressBar(0, 100);
        BarThread stepper = new BarThread(progressBar);
        stepper.start();
        
        JFrame progress = new JFrame();
        d = new Dimension(250,50);
        progress.setSize(d);
        d = getToolkit().getScreenSize();
        progress.getContentPane().add(progressBar);
        progress.setTitle("Now, data loading ...");
        progress.setLocation((d.width-progress.getWidth())/2,(d.height-progress.getHeight())/2);
        progress.show();

        // Add notebook page for default host connection
        pages = new Vector();
        addPage(new SOAPMonitorPage(axisHost));
        
        serviceMap = new HashMap();
        originalDoc = getServerWSDD();

        model1.clear();
        model2.clear();

        if (originalDoc!=null) {
            String ret = null;
            NodeList nl = originalDoc.getElementsByTagName("service");
            for (int i=0; i<nl.getLength(); i++) {
                Node node = nl.item(i);
                NamedNodeMap map = node.getAttributes();
                ret = map.getNamedItem("name").getNodeValue();
                serviceMap.put(ret, node);
                if (!isMonitored(node)) {
                    model1.addElement((String)ret);
                } else {
                    model2.addElement((String)ret);
                }
            }
            if (model1.size()>0) {
                add_btn.setEnabled(true);
            }
            if (model2.size()>0) {
                del_btn.setEnabled(true);
            }
            progress.dispose();
            save_btn.setEnabled(true);
            login_btn.setEnabled(true);
            top_pane.setEnabled(true);

            return true;
        } else {
            progress.dispose();
            login_btn.setEnabled(true);
            
            return false;
        }
    }
    
    /**
     * This class is a thred for a JProgressBar. 
     */
    class BarThread extends Thread {
        private int wait = 50;
        JProgressBar progressBar = null;

        public BarThread(JProgressBar bar) {
            progressBar = bar;
        }

        public void run() {
            int min = progressBar.getMinimum();
            int max = progressBar.getMaximum();
            Runnable runner = new Runnable()
            {
                public void run() {
                    int val = progressBar.getValue();
                    progressBar.setValue(val+1);
                }
            };
            for (int i=min; i<max; i++) {
                try {
                    SwingUtilities.invokeAndWait(runner);
                    Thread.sleep(wait);
                } catch (InterruptedException ignoredException) {
                } catch (InvocationTargetException ignoredException) {}
            }
        }
    }

    /**
     * Get the server-config.wsdd as a document to retrieve deployed services  
     */
    private Document getServerWSDD()
    {
        Document doc = null;
        
        try {
            String [] param = new String [] {"-u"+axisUser,"-w"+axisPass,"-l "+axisURI, "list"};
            String ret = adminClient.process( param );
            doc = XMLUtils.newDocument(new ByteArrayInputStream(ret.getBytes()));
        } catch (Exception e) { 
            JOptionPane pane = new JOptionPane();
            String msg = e.toString();
            pane.setMessageType(JOptionPane.WARNING_MESSAGE);
            pane.setMessage(msg);
            JDialog dlg = pane.createDialog(null, "Login status");
            dlg.setVisible(true);
        }
           
        return doc;
    }
    
    /**
     * Deploy the specified wsdd to change the monitoring state 
     */
    private boolean doDeploy(Document wsdd)
    {
        String deploy = null;
        Options opt = null;

        deploy = XMLUtils.ElementToString( wsdd.getDocumentElement() );
        try {
            String [] param = new String [] {"-u"+axisUser,"-w"+axisPass,"-l "+axisURI, ""};
            opt = new Options( param );
            adminClient.process(opt, new ByteArrayInputStream(deploy.getBytes()));
        } catch (Exception e) {
            return false;
        }

        return true;
    }
    
    /**
     * Get a new document which has the specified node as the document root
     */
    private Document getNewDocumentAsNode(Node target)
    {
        Document doc = null;
        Node node = null;
        
        try {
            doc = XMLUtils.newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        node = doc.importNode(target, true);
        doc.appendChild(node);
        
        return doc;
    }
    
    /**
     * Add needed nodes for monitoring to the specified node
     * 
     * TODO: support JAX-RPC type definition (i.e. <handlerInfoChain/>)
     */
    private Node addMonitor(Node target)
    {
        Document doc = null;
        Node node = null;
        Node newNode = null;
        String ret = null;
        NodeList nl = null;
        final String reqFlow = "requestFlow";
        final String resFlow = "responseFlow";
        final String monitor = "soapmonitor";
        final String handler = "handler";
        final String type = "type";

        doc = getNewDocumentAsNode(target);

        // Add "responseFlow node
        nl = doc.getElementsByTagName(resFlow);
        if (nl.getLength()==0) {
            node = doc.getDocumentElement().getFirstChild();
            newNode = doc.createElement(resFlow);
            doc.getDocumentElement().insertBefore(newNode, node);
        }
        // Add "requestFlow" node
        nl = doc.getElementsByTagName(reqFlow);
        if (nl.getLength()==0) {
            node = doc.getDocumentElement().getFirstChild();
            newNode = doc.createElement(reqFlow);
            doc.getDocumentElement().insertBefore(newNode, node);
        }
        
        // Add "handler" node and "soapmonitor" attribute for "requestFlow"
        nl = doc.getElementsByTagName(reqFlow);
        node = nl.item(0).getFirstChild();
        newNode = doc.createElement(handler);
        ((Element)newNode).setAttribute(type, monitor);
        nl.item(0).insertBefore(newNode, node);
        
        // Add "handler" node and "soapmonitor" attribute for "responseFlow"
        nl = doc.getElementsByTagName(resFlow);
        node = nl.item(0).getFirstChild();
        newNode = doc.createElement(handler);
        ((Element)newNode).setAttribute(type, monitor);
        nl.item(0).insertBefore(newNode, node);

        return (Node)doc.getDocumentElement();
    }
    
    /**
     * Remove a few nodes for stoping monitor from the specified node
     *
     * TODO: support JAX-RPC type definition (i.e. <handlerInfoChain/>)
     */
    private Node delMonitor(Node target)
    {
        Document doc = null;
        Node node = null;
        Node newNode = null;
        String ret = null;
        NodeList nl = null;
        final String reqFlow = "requestFlow";
        final String resFlow = "responseFlow";
        final String monitor = "soapmonitor";
        final String handler = "handler";
        final String type = "type";

        doc = getNewDocumentAsNode(target);
        
        nl = doc.getElementsByTagName(handler);
        int size;
        size = nl.getLength();
        Node [] removeNode = new Node [size];
        
        if (size>0) newNode = nl.item(0).getParentNode();

        for (int i=0; i<size; i++) {
            node = nl.item(i);
            NamedNodeMap map = node.getAttributes();
            ret = map.getNamedItem(type).getNodeValue();
            if (ret.equals(monitor)) {
                removeNode[i] = node;
            }
        }
        for (int i=0; i<size; i++) {
            Node child = removeNode[i];
            if (child!=null) {
                child.getParentNode().removeChild(child);
            }
        }
        return (Node)doc.getDocumentElement();
    }
    
    /**
     * Get a boolean value whether the specified node is monitoring or not
     */
    private boolean isMonitored(Node target)
    {
        Document doc = null;
    	Node node = null;
    	String ret = null;
        NodeList nl = null;
    	final String monitor = "soapmonitor";
        final String handler = "handler";
        final String type = "type";
    	
        doc = getNewDocumentAsNode(target);
		nl = doc.getElementsByTagName(handler);
		for (int i=0; i<nl.getLength(); i++) {
			node = nl.item(i);
			NamedNodeMap map = node.getAttributes();
			ret = map.getNamedItem(type).getNodeValue();
			if (ret.equals(monitor)) {
				return true;
			} else {
				return false;
			}
		}
    	return false;
    }
    
    /**
     * Add a few nodes for authentification
     *
     * TODO: support JAX-RPC type definition (i.e. <handlerInfoChain/>)
     */
    private Node addAuthenticate(Node target)
    {
        Document doc = null;
        Node node = null;
        Node newNode = null;
        String ret = null;
        NodeList nl = null;
        final String reqFlow = "requestFlow";
        final String handler = "handler";
        final String type = "type";
        final String auth = "java:org.apache.axis.handlers.SimpleAuthenticationHandler";
        final String param= "parameter";
        final String name = "name";
        final String role = "allowedRoles";
        final String value= "value";
        final String admin= "admin";
        
        boolean authNode = false;
        boolean roleNode = false;

        doc = getNewDocumentAsNode(target);

        // Add "requestFlow" node
        nl = doc.getElementsByTagName(reqFlow);
        if (nl.getLength()==0) {
            node = doc.getDocumentElement().getFirstChild();
            newNode = doc.createElement(reqFlow);
            doc.getDocumentElement().insertBefore(newNode, node);
        }
        
        // Add "SimpleAuthenticationHandler"
        // (i.e. <handler type="java:org.apache.axis.handlers.SimpleAuthenticationHandler"/>)

        nl = doc.getElementsByTagName(handler);
        for (int i=0; i<nl.getLength(); i++) {
            node = nl.item(i);
            NamedNodeMap map = node.getAttributes();
            ret = map.getNamedItem(type).getNodeValue();
            if (ret.equals(auth)) {
                authNode = true;
                break;
            }
        }
        if (!authNode) {
            nl = doc.getElementsByTagName(reqFlow);
            node = nl.item(0).getFirstChild();
            newNode = doc.createElement(handler);
            ((Element)newNode).setAttribute(type, auth);
            nl.item(0).insertBefore(newNode, node);
        }

        // Add "allowedRoles" (i.e. <parameter name="allowedRoles" value="admin"/> )
        nl = doc.getElementsByTagName(param);
        for (int i=0; i<nl.getLength(); i++) {
            node = nl.item(i);
            NamedNodeMap map = node.getAttributes();
            node =map.getNamedItem(name);
            if (node!=null) { 
                ret = node.getNodeValue();
                if (ret.equals(role)) {
                    roleNode = true;
                    break;
                }
            }
        }
        if (!roleNode) {
            nl = doc.getElementsByTagName(param);
            newNode = doc.createElement(param);
            ((Element)newNode).setAttribute(type, role);
            ((Element)newNode).setAttribute(value, admin);
            doc.insertBefore(newNode, nl.item(0));
        }

        return (Node)doc.getDocumentElement();
    } 
    
    /**
     * Handle the window close event
     */
    class MyWindowAdapter extends WindowAdapter
	{
		public void windowClosing(WindowEvent e){
            System.exit(0);
		}
	}
  
    /**
     * Add a page to the notebook
     */
    private void addPage(SOAPMonitorPage pg) {
        tabbed_pane.addTab("  "+pg.getHost()+"  ", pg);
        pages.addElement(pg);
    }
    
    /**
     * Del all pages to the notebook
     */
    private void delPage() {
        tabbed_pane.removeAll();
        pages.removeAllElements();
    }

    /** 
     * Frame is being displayed 
     */
    public void start() {
        // Tell all pages to start talking to the server
        Enumeration e = pages.elements();
        while (e.hasMoreElements()) {
            SOAPMonitorPage pg = (SOAPMonitorPage) e.nextElement();
            if (pg != null) {
                pg.start();
            }
        }
    }

    /*
     * Frame is no longer displayed
     */
    public void stop() {
        // Tell all pages to stop talking to the server
        Enumeration e = pages.elements();
        while (e.hasMoreElements()) {
            SOAPMonitorPage pg = (SOAPMonitorPage) e.nextElement();
            if (pg != null) {
                pg.stop();
            }
        }
    }

    /**
     * This class is for the Login Dialog
     */
    class LoginDlg extends JDialog implements ActionListener
    {
    	
    	private JButton ok_button = null;
    	private JButton cancel_button = null;
    	private JTextField     user = new JTextField(20);
    	private JPasswordField pass = new JPasswordField(20);
        private JTextField     uri  = new JTextField(20);
    	
    	private boolean loginState = false;
            	
		/**
		 * Constructor (create and layout page)
		 */
		public LoginDlg() {
			setTitle("SOAP Monitor Login");

			UIManager.put("Label.font", new Font("Dialog", Font.BOLD , 12));

            JPanel panel = new JPanel();
            
			ok_button = new JButton("OK");
			ok_button.addActionListener(this);
			cancel_button = new JButton("Cancel");
			cancel_button.addActionListener(this);
            
            // default URI for AxisServlet
            uri.setText( axisURI );
            
            JLabel userLabel = new JLabel("User:");
            JLabel passLabel = new JLabel("Password:");
            JLabel uriLabel  = new JLabel("Axis URI:");

            userLabel.setHorizontalAlignment(JTextField.RIGHT);
            passLabel.setHorizontalAlignment(JTextField.RIGHT);
            uriLabel.setHorizontalAlignment(JTextField.RIGHT);
			
            panel.add(userLabel);
            panel.add(user);
            panel.add(passLabel);
			panel.add(pass);
            panel.add(uriLabel);
            panel.add(uri);
            panel.add(ok_button);
			panel.add(cancel_button);
            setContentPane(panel);

            user.setText(SOAPMonitor.axisUser);
            pass.setText(SOAPMonitor.axisPass);

			GridLayout layout = new GridLayout(4,2);
			layout.setHgap(15);
			layout.setVgap(5);
            panel.setLayout(layout);

            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setModal(true);
            pack();
            Dimension d = getToolkit().getScreenSize();
            setLocation((d.width-getWidth())/2,(d.height-getHeight())/2);
		}
    	
		/**
		 * Listener to handle button actions
		 */
		public void actionPerformed(ActionEvent e) {
			// Check if the user pressed the OK button
			if (e.getSource() == ok_button) {
                loginState=true;
                SOAPMonitor.axisUser = user.getText();
                SOAPMonitor.axisPass = pass.getText();
				this.hide();
			} else if (e.getSource() == cancel_button) {
				this.dispose();
			}
	    }
        
        /**
         * Get the URI of the AxisServlet we are using
         */
        public String getURI() {
            return uri.getText();
        }
        
        /**
         * Get the login status as a boolean
         */
        public boolean isLogin() {
            return loginState; 
        }

    }

    /**
     * This class provides the contents of a notebook page
     * representing a server connection.
     */
    class SOAPMonitorPage extends JPanel 
                          implements Runnable,
                                     ListSelectionListener,
                                     ActionListener {

        /**
         * Status Strings
         */
        private final String STATUS_ACTIVE    = "The SOAP Monitor is started.";
        private final String STATUS_STOPPED   = "The SOAP Monitor is stopped.";
        private final String STATUS_CLOSED    = "The server communication has been terminated.";
        private final String STATUS_NOCONNECT = "The SOAP Monitor is unable to communcate with the server.";

        /**
         * Private data
         */
        private String                host = null;
        private Socket                socket = null;
        private ObjectInputStream     in = null;
        private ObjectOutputStream    out = null;
        private SOAPMonitorTableModel model = null;
        private JTable                table = null;
        private JScrollPane           scroll = null;
        private JPanel                list_panel = null;
        private JPanel                list_buttons = null;
        private JButton               remove_button = null;
        private JButton               remove_all_button = null;
        private JButton               filter_button = null;
        private JPanel                details_panel = null;
        private JPanel                details_header = null;
        private JSplitPane            details_soap = null;
        private JPanel                details_buttons = null;
        private JLabel                details_time = null;
        private JLabel                details_target = null;
        private JLabel                details_status = null;
        private JLabel                details_time_value = null;
        private JLabel                details_target_value = null;
        private JLabel                details_status_value = null;
        private EmptyBorder           empty_border = null;
        private EtchedBorder          etched_border = null;
        private JPanel                request_panel = null;
        private JPanel                response_panel = null;
        private JLabel                request_label = null;
        private JLabel                response_label = null;
        private SOAPMonitorTextArea   request_text = null;
        private SOAPMonitorTextArea   response_text = null;
        private JScrollPane           request_scroll = null;
        private JScrollPane           response_scroll = null;
        private JButton               layout_button = null;
        private JSplitPane            split = null;
        private JPanel                status_area = null;
        private JPanel                status_buttons = null;
        private JButton               start_button = null;
        private JButton               stop_button = null;
        private JLabel                status_text = null;
        private JPanel                status_text_panel = null;
        private SOAPMonitorFilter     filter = null;
        private GridBagLayout         details_header_layout = null;
        private GridBagConstraints    details_header_constraints = null;
        private JCheckBox             reflow_xml = null;

        /**
         * Constructor (create and layout page)
         */
        public SOAPMonitorPage(String host_name) {
            host = host_name;
            // Set up default filter (show all messages)
            filter = new SOAPMonitorFilter();
            // Use borders to help improve appearance
            etched_border = new EtchedBorder();
            // Build top portion of split (list panel) 
            model = new SOAPMonitorTableModel();
            table = new JTable(model);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setRowSelectionInterval(0,0);
            table.setPreferredScrollableViewportSize(new Dimension(600, 96));
            table.getSelectionModel().addListSelectionListener(this);
            scroll = new JScrollPane(table);
            remove_button = new JButton("Remove");
            remove_button.addActionListener(this);
            remove_button.setEnabled(false);
            remove_all_button = new JButton("Remove All");
            remove_all_button.addActionListener(this);
            filter_button = new JButton("Filter ...");
            filter_button.addActionListener(this);
            list_buttons = new JPanel();
            list_buttons.setLayout(new FlowLayout());
            list_buttons.add(remove_button);
            list_buttons.add(remove_all_button);
            list_buttons.add(filter_button);
            list_panel = new JPanel();
            list_panel.setLayout(new BorderLayout());
            list_panel.add(scroll,BorderLayout.CENTER);
            list_panel.add(list_buttons, BorderLayout.SOUTH);
            list_panel.setBorder(empty_border);
            // Build bottom portion of split (message details) 
            details_time = new JLabel("Time: ", SwingConstants.RIGHT);
            details_target = new JLabel("Target Service: ", SwingConstants.RIGHT);
            details_status = new JLabel("Status: ", SwingConstants.RIGHT);
            details_time_value = new JLabel();
            details_target_value = new JLabel();
            details_status_value = new JLabel();
            Dimension preferred_size = details_time.getPreferredSize();
            preferred_size.width = 1;
            details_time.setPreferredSize(preferred_size); 
            details_target.setPreferredSize(preferred_size); 
            details_status.setPreferredSize(preferred_size); 
            details_time_value.setPreferredSize(preferred_size); 
            details_target_value.setPreferredSize(preferred_size); 
            details_status_value.setPreferredSize(preferred_size);
            details_header = new JPanel();
            details_header_layout = new GridBagLayout();
            details_header.setLayout(details_header_layout);
            details_header_constraints = new GridBagConstraints();
            details_header_constraints.fill=GridBagConstraints.BOTH;
            details_header_constraints.weightx=0.5;
            details_header_layout.setConstraints(details_time,details_header_constraints);
            details_header.add(details_time);
            details_header_layout.setConstraints(details_time_value,details_header_constraints);
            details_header.add(details_time_value);
            details_header_layout.setConstraints(details_target,details_header_constraints);
            details_header.add(details_target);
            details_header_constraints.weightx=1.0;
            details_header_layout.setConstraints(details_target_value,details_header_constraints);
            details_header.add(details_target_value);
            details_header_constraints.weightx=.5;
            details_header_layout.setConstraints(details_status,details_header_constraints);
            details_header.add(details_status);
            details_header_layout.setConstraints(details_status_value,details_header_constraints);
            details_header.add(details_status_value);
            details_header.setBorder(etched_border);
            request_label = new JLabel("SOAP Request", SwingConstants.CENTER);
            request_text = new SOAPMonitorTextArea();
            request_text.setEditable(false);
            request_scroll = new JScrollPane(request_text);
            request_panel = new JPanel();
            request_panel.setLayout(new BorderLayout());
            request_panel.add(request_label, BorderLayout.NORTH);
            request_panel.add(request_scroll, BorderLayout.CENTER);
            response_label = new JLabel("SOAP Response", SwingConstants.CENTER);
            response_text = new SOAPMonitorTextArea();
            response_text.setEditable(false);
            response_scroll = new JScrollPane(response_text);
            response_panel = new JPanel();
            response_panel.setLayout(new BorderLayout());
            response_panel.add(response_label, BorderLayout.NORTH);
            response_panel.add(response_scroll, BorderLayout.CENTER);
            details_soap = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            details_soap.setTopComponent(request_panel);
            details_soap.setRightComponent(response_panel);
            details_soap.setResizeWeight(.5);
            details_panel = new JPanel();
            layout_button = new JButton("Switch Layout");
            layout_button.addActionListener(this);
            reflow_xml = new JCheckBox("Reflow XML text");
            reflow_xml.addActionListener(this);
            details_buttons = new JPanel();
            details_buttons.setLayout(new FlowLayout());
            details_buttons.add(reflow_xml);
            details_buttons.add(layout_button);
            details_panel.setLayout(new BorderLayout());
            details_panel.add(details_header,BorderLayout.NORTH);
            details_panel.add(details_soap,BorderLayout.CENTER);
            details_panel.add(details_buttons,BorderLayout.SOUTH);
            details_panel.setBorder(empty_border);
            // Add the two parts to the age split pane
            split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            split.setTopComponent(list_panel);
            split.setRightComponent(details_panel);
            // Build status area
            start_button = new JButton("Start");
            start_button.addActionListener(this);
            stop_button = new JButton("Stop");
            stop_button.addActionListener(this);
            status_buttons = new JPanel();
            status_buttons.setLayout(new FlowLayout());
            status_buttons.add(start_button);
            status_buttons.add(stop_button);
            status_text = new JLabel();
            status_text.setBorder(new BevelBorder(BevelBorder.LOWERED));
            status_text_panel = new JPanel();
            status_text_panel.setLayout(new BorderLayout());
            status_text_panel.add(status_text, BorderLayout.CENTER);
            status_text_panel.setBorder(empty_border);
            status_area = new JPanel();
            status_area.setLayout(new BorderLayout());
            status_area.add(status_buttons, BorderLayout.WEST);
            status_area.add(status_text_panel, BorderLayout.CENTER);
            status_area.setBorder(etched_border);
            // Add the split and status area to page
            setLayout(new BorderLayout());
            add(split, BorderLayout.CENTER);
            add(status_area, BorderLayout.SOUTH);
        }

        /**
         * Get the name of the host we are displaying
         */
        public String getHost() {
            return host;
        }

        /**
         * Set the status text
         */
        public void setStatus(String txt) {
            status_text.setForeground(Color.black);
            status_text.setText("  "+txt);
        }

        /**
         * Set the status text to an error
         */
        public void setErrorStatus(String txt) {
            status_text.setForeground(Color.red);
            status_text.setText("  "+txt);
        }

        /**
         * Start talking to the server
         */
        public void start() {
			String codehost = axisHost;
            if (socket == null) {
                try {

                    // Open the socket to the server
                    socket = new Socket(codehost, port);
                    // Create output stream
                    out = new ObjectOutputStream(socket.getOutputStream());
                    out.flush();
                    // Create input stream and start background
                    // thread to read data from the server
                    in = new ObjectInputStream(socket.getInputStream());
                    new Thread(this).start();
                } catch (Exception e) {
                    // Exceptions here are unexpected, but we can't
                    // really do anything (so just write it to stdout
                    // in case someone cares and then ignore it)
                    System.out.println("Exception! "+e.toString());
                    e.printStackTrace();
                    setErrorStatus(STATUS_NOCONNECT);
                    socket = null;
                }
            } else {
                // Already started
            }
            if (socket != null) {
                // Make sure the right buttons are enabled
                start_button.setEnabled(false);
                stop_button.setEnabled(true);
                setStatus(STATUS_ACTIVE);
            }
        }

        /**
         * Stop talking to the server
         */
        public void stop() {
            if (socket != null) {
                // Close all the streams and socket
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException ioe) {
                    }
                    out = null;
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ioe) {
                    }
                    in = null;
                }
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException ioe) {
                    }
                    socket = null;
                }
            } else {
                // Already stopped
            }
            // Make sure the right buttons are enabled
            start_button.setEnabled(true);
            stop_button.setEnabled(false);
            setStatus(STATUS_STOPPED);
        }

        /**
         * Background thread used to receive data from
         * the server.
         */
        public void run() {
            Long            id;
            Integer         message_type;
            String          target;
            String          soap;
            SOAPMonitorData data;
            int             selected;
            int             row;
            boolean         update_needed;
            while (socket != null) {
                try {
                    // Get the data from the server
                    message_type = (Integer) in.readObject();
                    // Process the data depending on its type
                    switch (message_type.intValue()) {
                        case SOAPMonitorConstants.SOAP_MONITOR_REQUEST:
                            // Get the id, target and soap info
                            id = (Long) in.readObject();
                            target = (String) in.readObject();
                            soap = (String) in.readObject();
                            // Add new request data to the table
                            data = new SOAPMonitorData(id,target,soap);
                            model.addData(data);
                            // If "most recent" selected then update
                            // the details area if needed
                            selected = table.getSelectedRow();
                            if ((selected == 0) && model.filterMatch(data)) {
                                valueChanged(null);
                            }
                            break;
                        case SOAPMonitorConstants.SOAP_MONITOR_RESPONSE:
                            // Get the id and soap info
                            id = (Long) in.readObject();
                            soap = (String) in.readObject();
                            data = model.findData(id);
                            if (data != null) {
                                update_needed = false;
                                // Get the selected row
                                selected = table.getSelectedRow();
                                // If "most recent", then always
                                // update details area
                                if (selected == 0) {
                                    update_needed = true;
                                }
                                // If the data being updated is
                                // selected then update details
                                row = model.findRow(data);
                                if ((row != -1) && (row == selected)) {
                                    update_needed = true;
                                }
                                // Set the response and update table
                                data.setSOAPResponse(soap);
                                model.updateData(data);
                                // Refresh details area (if needed)
                                if (update_needed) {
                                    valueChanged(null);
                                }
                            }
                            break;
                    }

                } catch (Exception e) {
                    // Exceptions are expected here when the
                    // server communication has been terminated.
                    if (stop_button.isEnabled()) {
                        stop();
                        setErrorStatus(STATUS_CLOSED);
                    }
                }
            }
        }

        /**
         * Listener to handle table selection changes
         */
        public void valueChanged(ListSelectionEvent e) {
            int row = table.getSelectedRow();
            // Check if they selected a specific row
            if (row > 0) {
                remove_button.setEnabled(true);
            } else {
                remove_button.setEnabled(false);
            }
            // Check for "most recent" selection
            if (row == 0) {
                row = model.getRowCount() - 1;
                if (row == 0) {
                    row = -1;
                }
            }
            if (row == -1) {
                // Clear the details panel
                details_time_value.setText("");
                details_target_value.setText("");
                details_status_value.setText("");
                request_text.setText("");
                response_text.setText("");
            } else {
                // Show the details for the row
                SOAPMonitorData soap = model.getData(row);
                details_time_value.setText(soap.getTime());
                details_target_value.setText(soap.getTargetService());
                details_status_value.setText(soap.getStatus());
                if (soap.getSOAPRequest() == null) {
                    request_text.setText("");
                } else {
                    request_text.setText(soap.getSOAPRequest());
                    request_text.setCaretPosition(0);
                }
                if (soap.getSOAPResponse() == null) {
                    response_text.setText("");
                } else {
                    response_text.setText(soap.getSOAPResponse());
                    response_text.setCaretPosition(0);
                }
            }
        }

        /**
         * Listener to handle button actions
         */
        public void actionPerformed(ActionEvent e) {
            // Check if the user pressed the remove button
            if (e.getSource() == remove_button) {
                int row = table.getSelectedRow();
                model.removeRow(row);
                table.clearSelection();
                table.repaint();
                valueChanged(null);
            }
            // Check if the user pressed the remove all button
            if (e.getSource() == remove_all_button) {
                model.clearAll();
                table.setRowSelectionInterval(0,0);
                table.repaint();
                valueChanged(null);
            }
            // Check if the user pressed the filter button
            if (e.getSource() == filter_button) {
                filter.showDialog();
                if (filter.okPressed()) {
                    // Update the display with new filter
                    model.setFilter(filter);
                    table.repaint();
                }
            }
            // Check if the user pressed the start button
            if (e.getSource() == start_button) {
                start();
            }
            // Check if the user pressed the stop button
            if (e.getSource() == stop_button) {
                stop();
            }
            // Check if the user wants to switch layout
            if (e.getSource() == layout_button) {
                details_panel.remove(details_soap);
                details_soap.removeAll();
                if (details_soap.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
                    details_soap = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                } else {
                    details_soap = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
                }
                details_soap.setTopComponent(request_panel);
                details_soap.setRightComponent(response_panel);
                details_soap.setResizeWeight(.5);
                details_panel.add(details_soap, BorderLayout.CENTER);
                details_panel.validate();
                details_panel.repaint();
            }
            // Check if the user is changing the reflow option
            if (e.getSource() == reflow_xml) {
                request_text.setReflowXML(reflow_xml.isSelected());
                response_text.setReflowXML(reflow_xml.isSelected());
            }
        }
    }

    /**
     * This class represend the data for a SOAP request/response pair
     */
    class SOAPMonitorData {

        /**
         * Private data
         */
        private Long    id;
        private String  time;
        private String  target;
        private String  soap_request;
        private String  soap_response;

        /**
         * Constructor
         */
        public SOAPMonitorData(Long id, String target, String soap_request) {
            this.id = id;
            // A null id is used to signal that the "most recent" entry
            // is being created.
            if (id == null) {
                this.time = "Most Recent";
                this.target = "---";
                this.soap_request = null;
                this.soap_response = null;
            } else {
                this.time = DateFormat.getTimeInstance().format(new Date());
                this.target = target;
                this.soap_request = soap_request;
                this.soap_response = null;
            }
        }

        /**
         * Get the id for the SOAP message
         */
        public Long getId() {
            return id;
        }

        /**
         * Get the time the SOAP request was received by the application
         */
        public String getTime() {
            return time;
        }

        /**
         * Get the SOAP request target service name
         */
        public String getTargetService() {
            return target;
        }

        /**
         * Get the status of the request
         */
        public String getStatus() {
            String status = "---";
            if (id != null) {
                status = "Complete";
                if (soap_response == null) {
                    status = "Active";
                }
            }
            return status;
        }

        /**
         * Get the request SOAP contents
         */
        public String getSOAPRequest() {
            return soap_request;
        }

        /**
         * Set the resposne SOAP contents
         */
        public void setSOAPResponse(String response) {
            soap_response = response;
        }

        /**
         * Get the response SOAP contents
         */
        public String getSOAPResponse() {
            return soap_response;
        }
    }

    /**
     * This table model is used to manage the table displayed
     * at the top of the page to show all the SOAP messages
     * we have received and to control which message details are
     * to be displayed on the bottom of the page.
     */
    class SOAPMonitorTableModel extends AbstractTableModel {

        /**
         * Column titles
         */
        private final String[] column_names = { "Time",
                                                "Target Service",
                                                "Status" };
        /**                                        
         * Private data
         */
        private Vector  data;
        private Vector  filter_include;
        private Vector  filter_exclude;
        private boolean filter_active;
        private boolean filter_complete;
        private Vector  filter_data;

        /**
         * Constructor
         */
        public SOAPMonitorTableModel() {
            data = new Vector();
            // Add "most recent" entry to top of table
            SOAPMonitorData soap = new SOAPMonitorData(null,null,null);
            data.addElement(soap);
            filter_include = null;
            filter_exclude = null;
            filter_active = false;
            filter_complete = false;
            filter_data = null;
            // By default, exclude NotificationService and
            // EventViewerService messages
            filter_exclude = new Vector();
            filter_exclude.addElement("NotificationService");
            filter_exclude.addElement("EventViewerService");
            filter_data = new Vector();
            filter_data.addElement(soap);
        }

        /**
         * Get column count (part of table model interface)
         */
        public int getColumnCount() {
            return column_names.length;
        }
        
        /**
         * Get row count (part of table model interface)
         */
        public int getRowCount() {
            int count = data.size();
            if (filter_data != null) {
                count = filter_data.size();
            }
            return count;
        }

        /**
         * Get column name (part of table model interface)
         */
        public String getColumnName(int col) {
            return column_names[col];
        }

        /**
         * Get value at (part of table model interface)
         */
        public Object getValueAt(int row, int col) {
            SOAPMonitorData soap;
            String          value = null;
            soap = (SOAPMonitorData) data.elementAt(row);
            if (filter_data != null) {
                soap = (SOAPMonitorData) filter_data.elementAt(row);
            }
            switch (col) {
                case 0:
                    value = soap.getTime();
                    break;
                case 1:
                    value = soap.getTargetService();
                    break;
                case 2:
                    value = soap.getStatus();
                    break;
            }
            return value;
        }

        /**
         * Check if soap data matches filter 
         */
        public boolean filterMatch(SOAPMonitorData soap) {
            boolean match = true;
            if (filter_include != null) {
                // Check for service match
                Enumeration e = filter_include.elements();
                match = false;
                while (e.hasMoreElements() && !match) {
                    String service = (String) e.nextElement();
                    if (service.equals(soap.getTargetService())) {
                        match = true;
                    }
                }
            }
            if (filter_exclude != null) {
                // Check for service match
                Enumeration e = filter_exclude.elements();
                while (e.hasMoreElements() && match) {
                    String service = (String) e.nextElement();
                    if (service.equals(soap.getTargetService())) {
                        match = false;
                    }
                }
            }
            if (filter_active) {
                // Check for active status match
                if (soap.getSOAPResponse() != null) {
                    match = false;
                }
            }
            if (filter_complete) {
                // Check for complete status match
                if (soap.getSOAPResponse() == null) {
                    match = false;
                }
            }
            // The "most recent" is always a match
            if (soap.getId() == null) {
                match = true;
            }
            return match;
        }

        /**
         * Add data to the table as a new row
         */
        public void addData(SOAPMonitorData soap) {
            int row = data.size();
            data.addElement(soap);
            if (filter_data != null) {
                if (filterMatch(soap)) {
                    row = filter_data.size();
                    filter_data.addElement(soap);
                    fireTableRowsInserted(row,row);
                }
            } else {
                fireTableRowsInserted(row,row);
            }
        }

        /**
         * Find the data for a given id
         */
        public SOAPMonitorData findData(Long id) {
            SOAPMonitorData soap = null;
            for (int row=data.size(); (row > 0) && (soap == null); row--) {
                soap = (SOAPMonitorData) data.elementAt(row-1);
                if (soap.getId().longValue() != id.longValue()) {
                    soap = null;
                }
            }
            return soap;
        }

        /**
         * Find the row in the table for a given message id
         */
        public int findRow(SOAPMonitorData soap) {
            int row = -1;
            if (filter_data != null) {
                row = filter_data.indexOf(soap);
            } else {
                row = data.indexOf(soap);
            }
            return row;
        }

        /**
         * Remove all messages from the table (but leave "most recent")
         */
        public void clearAll() {
            int last_row = data.size() - 1;
            if (last_row > 0) {
                data.removeAllElements();
                SOAPMonitorData soap = new SOAPMonitorData(null,null,null);
                data.addElement(soap);
                if (filter_data != null) {
                    filter_data.removeAllElements();
                    filter_data.addElement(soap);
                }
                fireTableDataChanged();
            }
        }

        /**
         * Remove a message from the table
         */
        public void removeRow(int row) {
            SOAPMonitorData soap = null;
            if (filter_data == null) {
                soap = (SOAPMonitorData) data.elementAt(row);
                data.remove(soap);
            } else {
                soap = (SOAPMonitorData) filter_data.elementAt(row);
                filter_data.remove(soap);
                data.remove(soap);
            }         
            fireTableRowsDeleted(row,row);
        }

        /**
         * Set a new filter
         */
        public void setFilter(SOAPMonitorFilter filter) {
            // Save new filter criteria
            filter_include = filter.getFilterIncludeList();
            filter_exclude = filter.getFilterExcludeList();
            filter_active = filter.getFilterActive();
            filter_complete = filter.getFilterComplete();
            applyFilter();
        }

        /**
         * Refilter the list of messages
         */
        public void applyFilter() {
            // Re-filter using new criteria
            filter_data = null;
            if ((filter_include != null) || 
                (filter_exclude != null) ||
                 filter_active || filter_complete ) {
                filter_data = new Vector();
                Enumeration e = data.elements();
                SOAPMonitorData soap;
                while (e.hasMoreElements()) {
                    soap = (SOAPMonitorData) e.nextElement();
                    if (filterMatch(soap)) {
                        filter_data.addElement(soap);
                    }
                }
            }
            fireTableDataChanged();
        }

        /**
         * Get the data for a row
         */
        public SOAPMonitorData getData(int row) {
            SOAPMonitorData soap = null;
            if (filter_data == null) {
                soap = (SOAPMonitorData) data.elementAt(row);
            } else {
                soap = (SOAPMonitorData) filter_data.elementAt(row);
            }
            return soap;
        }

        /**
         * Update a message
         */
        public void updateData (SOAPMonitorData soap) {
           int row;
           if (filter_data == null) {
               // No filter, so just fire table updated
               row = data.indexOf(soap);
               if (row != -1) {
                   fireTableRowsUpdated(row,row);
               }
           } else {
               // Check if the row was being displayed
               row = filter_data.indexOf(soap);
               if (row == -1) {
                   // Row was not displayed, so check for if it
                   // now needs to be displayed
                   if (filterMatch(soap)) {
                       int index = -1;
                       row = data.indexOf(soap) + 1;
                       while ((row < data.size()) && (index == -1)) {
                           index = filter_data.indexOf(data.elementAt(row));
                           if (index != -1) {                   
                               // Insert at this location
                               filter_data.add(index,soap);
                           }
                           row++;
                       }
                       if (index == -1) {
                           // Insert at end
                           index = filter_data.size();
                           filter_data.addElement(soap);
                       }
                       fireTableRowsInserted(index,index);
                   }
               } else {
                   // Row was displayed, so check if it needs to
                   // be updated or removed
                   if (filterMatch(soap)) {
                       fireTableRowsUpdated(row,row);
                   } else {
                       filter_data.remove(soap);
                       fireTableRowsDeleted(row,row);
                   }
               }
           }
        }

    }

    /**
     * Panel with checkbox and list
     */
    class ServiceFilterPanel extends JPanel 
                             implements ActionListener,
                                        ListSelectionListener,
                                        DocumentListener {

        private JCheckBox    service_box = null;
        private Vector       filter_list = null;
        private Vector       service_data = null;
        private JList        service_list = null;
        private JScrollPane  service_scroll = null;
        private JButton      remove_service_button = null;
        private JPanel       remove_service_panel = null;
        private EmptyBorder  indent_border = null;
        private EmptyBorder  empty_border = null;
        private JPanel       service_area = null;
        private JPanel       add_service_area = null;
        private JTextField   add_service_field = null;
        private JButton      add_service_button = null;
        private JPanel       add_service_panel = null;

        /**
         * Constructor
         */
        public ServiceFilterPanel(String text, Vector list) {
            empty_border = new EmptyBorder(5,5,0,5);
            indent_border = new EmptyBorder(5,25,5,5);
            service_box = new JCheckBox(text);
            service_box.addActionListener(this);
            service_data = new Vector();
            if (list != null) {
                service_box.setSelected(true);
                service_data = (Vector) list.clone();
            }
            service_list = new JList(service_data);
            service_list.setBorder(new EtchedBorder());
            service_list.setVisibleRowCount(5);
            service_list.addListSelectionListener(this);
            service_list.setEnabled(service_box.isSelected());
            service_scroll = new JScrollPane(service_list);
            service_scroll.setBorder(new EtchedBorder());
            remove_service_button = new JButton("Remove");
            remove_service_button.addActionListener(this);
            remove_service_button.setEnabled(false);
            remove_service_panel = new JPanel();
            remove_service_panel.setLayout(new FlowLayout());
            remove_service_panel.add(remove_service_button);
            service_area = new JPanel();
            service_area.setLayout(new BorderLayout());
            service_area.add(service_scroll, BorderLayout.CENTER);
            service_area.add(remove_service_panel, BorderLayout.EAST);
            service_area.setBorder(indent_border);
            add_service_field = new JTextField();
            add_service_field.addActionListener(this);
            add_service_field.getDocument().addDocumentListener(this);
            add_service_field.setEnabled(service_box.isSelected());
            add_service_button = new JButton("Add");
            add_service_button.addActionListener(this);
            add_service_button.setEnabled(false);
            add_service_panel = new JPanel();
            add_service_panel.setLayout(new BorderLayout());
            JPanel dummy = new JPanel();
            dummy.setBorder(empty_border);
            add_service_panel.add(dummy, BorderLayout.WEST);
            add_service_panel.add(add_service_button, BorderLayout.EAST);
            add_service_area = new JPanel();
            add_service_area.setLayout(new BorderLayout());
            add_service_area.add(add_service_field, BorderLayout.CENTER);
            add_service_area.add(add_service_panel, BorderLayout.EAST);
            add_service_area.setBorder(indent_border);
            setLayout(new BorderLayout());
            add(service_box, BorderLayout.NORTH);
            add(service_area, BorderLayout.CENTER);
            add(add_service_area, BorderLayout.SOUTH);
            setBorder(empty_border);
        }

        /**
         * Get the current list of services
         */
        public Vector getServiceList() {
            Vector list = null;
            if (service_box.isSelected()) {
                list = service_data;
            }
            return list;
        }

        /**
         * Listener to handle button actions
         */
        public void actionPerformed(ActionEvent e) {
            // Check if the user changed the service filter option
            if (e.getSource() == service_box) {
                service_list.setEnabled(service_box.isSelected());
                service_list.clearSelection();
                remove_service_button.setEnabled(false);
                add_service_field.setEnabled(service_box.isSelected());
                add_service_field.setText("");
                add_service_button.setEnabled(false);
            }
            // Check if the user pressed the add service button
            if ((e.getSource() == add_service_button) ||
                (e.getSource() == add_service_field)) {
                String text = add_service_field.getText();
                if ((text != null) && (text.length() > 0)) {
                    service_data.addElement(text);
                    service_list.setListData(service_data);
                }
                add_service_field.setText("");
                add_service_field.requestFocus();
            }
            // Check if the user pressed the remove service button
            if (e.getSource() == remove_service_button) {
                Object[] sels = service_list.getSelectedValues();
                for (int i=0; i<sels.length; i++) {
                    service_data.removeElement(sels[i]);
                }
                service_list.setListData(service_data);
                service_list.clearSelection();
            }
        }

        /**
         * Handle changes to the text field
         */
        public void changedUpdate(DocumentEvent e) {
            String text = add_service_field.getText();
            if ((text != null) && (text.length() > 0)) {
                add_service_button.setEnabled(true);
            } else {
                add_service_button.setEnabled(false);
            }
        }

        /**
         * Handle changes to the text field
         */
        public void insertUpdate(DocumentEvent e) {
            changedUpdate(e);
        }

        /**
         * Handle changes to the text field
         */
        public void removeUpdate(DocumentEvent e) {
            changedUpdate(e);
        }

        /**
         * Listener to handle service list selection changes
         */
        public void valueChanged(ListSelectionEvent e) {
            if (service_list.getSelectedIndex() == -1) {
                remove_service_button.setEnabled(false);
            } else {
                remove_service_button.setEnabled(true);
            }
        }
    }

    /**
     * Class for showing the filter dialog
     */
    class SOAPMonitorFilter implements ActionListener {

        /**
         * Private data
         */
        private JDialog            dialog = null;
        private JPanel             panel = null;
        private JPanel             buttons = null;
        private JButton            ok_button = null;
        private JButton            cancel_button = null;
        private ServiceFilterPanel include_panel = null;
        private ServiceFilterPanel exclude_panel = null;
        private JPanel             status_panel = null;
        private JCheckBox          status_box = null;
        private EmptyBorder  empty_border = null;
        private EmptyBorder  indent_border = null;
        private JPanel             status_options = null;
        private ButtonGroup        status_group = null;
        private JRadioButton       status_active = null;
        private JRadioButton       status_complete = null;
        private Vector             filter_include_list = null;
        private Vector             filter_exclude_list = null;
        private boolean            filter_active = false;
        private boolean            filter_complete = false;
        private boolean            ok_pressed = false;

        /**
         * Constructor
         */
        public SOAPMonitorFilter() {
            // By default, exclude NotificationService and
            // EventViewerService messages
            filter_exclude_list = new Vector();
            filter_exclude_list.addElement("NotificationService");
            filter_exclude_list.addElement("EventViewerService");
        }

        /**
         * Get list of services to be included
         */
        public Vector getFilterIncludeList() {
            return filter_include_list;
        }

        /**
         * Get list of services to be excluded
         */
        public Vector getFilterExcludeList() {
            return filter_exclude_list;
        }

        /**
         * Check if filter active messages
         */
        public boolean getFilterActive() {
            return filter_active;
        }

        /**
         * Check if filter complete messages
         */
        public boolean getFilterComplete() {
            return filter_complete;
        }

        /**
         * Show the filter dialog
         */
        public void showDialog() {
            empty_border = new EmptyBorder(5,5,0,5);
            indent_border = new EmptyBorder(5,25,5,5);
            include_panel = new ServiceFilterPanel("Include messages based on target service:",
                                                   filter_include_list);
            exclude_panel = new ServiceFilterPanel("Exclude messages based on target service:",
                                                   filter_exclude_list);
            status_box = new JCheckBox("Filter messages based on status:");
            status_box.addActionListener(this);
            status_active = new JRadioButton("Active messages only");
            status_active.setSelected(true);
            status_active.setEnabled(false);
            status_complete = new JRadioButton("Complete messages only");
            status_complete.setEnabled(false);
            status_group = new ButtonGroup();
            status_group.add(status_active);
            status_group.add(status_complete);
            if (filter_active || filter_complete) {
                status_box.setSelected(true);
                status_active.setEnabled(true);
                status_complete.setEnabled(true);
                if (filter_complete) {
                    status_complete.setSelected(true);
                }
            }
            status_options = new JPanel();
            status_options.setLayout(new BoxLayout(status_options, BoxLayout.Y_AXIS));
            status_options.add(status_active);
            status_options.add(status_complete);
            status_options.setBorder(indent_border);
            status_panel = new JPanel();
            status_panel.setLayout(new BorderLayout());
            status_panel.add(status_box, BorderLayout.NORTH);
            status_panel.add(status_options, BorderLayout.CENTER);
            status_panel.setBorder(empty_border);
            ok_button = new JButton("Ok");
            ok_button.addActionListener(this);
            cancel_button = new JButton("Cancel");
            cancel_button.addActionListener(this);
            buttons = new JPanel();
            buttons.setLayout(new FlowLayout());
            buttons.add(ok_button);
            buttons.add(cancel_button);
            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(include_panel);
            panel.add(exclude_panel);
            panel.add(status_panel);
            panel.add(buttons);
            dialog = new JDialog();
            dialog.setTitle("SOAP Monitor Filter");
            dialog.setContentPane(panel);
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialog.setModal(true);
            dialog.pack();
            Dimension d = dialog.getToolkit().getScreenSize();
            dialog.setLocation((d.width-dialog.getWidth())/2,
                               (d.height-dialog.getHeight())/2);
            ok_pressed = false;
            dialog.show();
        }

        /**
         * Listener to handle button actions
         */
        public void actionPerformed(ActionEvent e) {
            // Check if the user pressed the ok button
            if (e.getSource() == ok_button) {
                filter_include_list = include_panel.getServiceList();
                filter_exclude_list = exclude_panel.getServiceList();
                if (status_box.isSelected()) {
                    filter_active = status_active.isSelected();
                    filter_complete = status_complete.isSelected();
                } else {
                    filter_active = false;
                    filter_complete = false;
                }
                ok_pressed = true;
                dialog.dispose();
            }
            // Check if the user pressed the cancel button
            if (e.getSource() == cancel_button) {
                dialog.dispose();
            }
            // Check if the user changed the status filter option
            if (e.getSource() == status_box) {
                status_active.setEnabled(status_box.isSelected());
                status_complete.setEnabled(status_box.isSelected());
            }
        }

        /**
         * Check if the user pressed the ok button
         */
        public boolean okPressed() {
            return ok_pressed;
        }
    }

    /**
     * Text panel class that supports XML reflow
     */
    class SOAPMonitorTextArea extends JTextArea {

        /**
         * Private data
         */
        private boolean format = false;
        private String  original = "";
        private String  formatted = null; 

        /**
         * Constructor
         */
        public SOAPMonitorTextArea() {
        }

        /** 
         * Override setText to do formatting
         */
        public void setText(String text) {
            original = text;
            formatted = null;
            if (format) {
                doFormat();
                super.setText(formatted);
            } else {
                super.setText(original);
            }
        }

        /**
         * Turn reflow on or off
         */
        public void setReflowXML(boolean reflow) {
            format = reflow;
            if (format) {
                if (formatted == null) {
                    doFormat();
                }
                super.setText(formatted);
            } else {
                super.setText(original);
            }
        }

        /**
         * Reflow XML
         */
        public void doFormat() {
            Vector       parts = new Vector();
            char[]       chars = original.toCharArray();
            int          index = 0;
            int          first = 0;
            String       part = null;
            while (index < chars.length) {
                // Check for start of tag
                if (chars[index] == '<') {
                    // Did we have data before this tag?
                    if (first < index) {
                        part = new String(chars,first,index-first);
                        part = part.trim();
                        // Save non-whitespace data
                        if (part.length() > 0) {
                            parts.addElement(part);
                        }
                    }
                    // Save the start of tag
                    first = index;
                }
                // Check for end of tag
                if (chars[index] == '>') {
                    // Save the tag
                    part = new String(chars,first,index-first+1);
                    parts.addElement(part);
                    first = index+1;
                }
                // Check for end of line
                if ((chars[index] == '\n') || (chars[index] == '\r')) {
                    // Was there data on this line?
                    if (first < index) {
                        part = new String(chars,first,index-first);
                        part = part.trim();
                        // Save non-whitespace data
                        if (part.length() > 0) {
                            parts.addElement(part);
                        }
                    }
                    first = index+1;
                }
                index++;
            }
            // Reflow as XML
            StringBuffer buf = new StringBuffer();
            Object[] list = parts.toArray();
            int indent = 0;
            int pad = 0;
            index = 0;
            while (index < list.length) {
                part = (String) list[index];
                if (buf.length() == 0) {
                    // Just add first tag (should be XML header)
                    buf.append(part);
                } else {
                    // All other parts need to start on a new line
                    buf.append('\n');
                    // If we're at an end tag then decrease indent
                    if (part.startsWith("</")) {
                        indent--;
                    }            
                    // Add any indent
                    for (pad = 0; pad < indent; pad++) {
                        buf.append("  ");
                    }
                    // Add the tag or data
                    buf.append(part);
                    // If this is a start tag then increase indent
                    if (part.startsWith("<") &&
                        !part.startsWith("</") &&
                        !part.endsWith("/>")) {
                        indent++;
                        // Check for special <tag>data</tag> case
                        if ((index + 2) < list.length) {
                            part = (String) list[index+2];
                            if (part.startsWith("</")) {
                                part = (String) list[index+1];
                                if (!part.startsWith("<")) {
                                    buf.append(part);
                                    part = (String) list[index+2];
                                    buf.append(part);
                                    index = index + 2;
                                    indent--;
                                }
                            }
                        }
                    }
                }
                index++;
            }
            formatted = new String(buf);
        }
    }

	/**
	 * Listener to handle button actions
	 */
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		
		if (obj == add_btn) {
			int selected[] = list1.getSelectedIndices();
			int len = selected.length - 1;
			for (int i=len; i>=0; i--) {
				model2.addElement(model1.getElementAt(selected[i]));
				model1.remove(selected[i]);
			}
            if (model1.size()==0) {
                add_btn.setEnabled(false);
            }
            if (model2.size()>0) {
                del_btn.setEnabled(true);
            } 
		} else if (obj == del_btn) {
			int selected[] = list2.getSelectedIndices();
			int len = selected.length - 1;
			for (int i=len; i>=0; i--) {
				model1.addElement(model2.getElementAt(selected[i]));
				model2.remove(selected[i]);
			}
            if (model2.size()==0) {
                del_btn.setEnabled(false);
            }
            if (model1.size()>0) {
                add_btn.setEnabled(true);
            }
		} else if (obj == login_btn) {
            if (doLogin()) {
                delPage();
                addPage(new SOAPMonitorPage(axisHost));
                start();
            } else {
                add_btn.setEnabled(false);
                del_btn.setEnabled(false);
            }
        } else if (obj == save_btn) {
            String service = null;
            Node node = null;
            Node impNode = null;
            Document wsdd = null;

            JOptionPane pane  = null;
            JDialog dlg = null;
            String msg = null;
            final String title = "Deployment status";
            
            final String deploy = "<deployment name=\"SOAPMonitor\"" +
                " xmlns=\"http://xml.apache.org/axis/wsdd/\"" +
                " xmlns:java=\"http://xml.apache.org/axis/wsdd/providers/java\">\n"+
                " <handler name=\"soapmonitor\"" +
                " type=\"java:org.apache.axis.handlers.SOAPMonitorHandler\" />\n" +
                " </deployment>";
 
            
            // Create a new wsdd document
            try {
                wsdd = XMLUtils.newDocument(new ByteArrayInputStream(deploy.getBytes()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            Collection col = serviceMap.keySet();
            Iterator ite = col.iterator();
            
            // Add all of service nodes to the new wsdd
            while (ite.hasNext()) {
                service = (String)ite.next();
                node = (Node)serviceMap.get(service);
                if (model2.contains(service)) {
                    if (isMonitored(node)) {  // It's already been monitored
                        impNode = wsdd.importNode(node, true);
                    } else {                  // It's to be monitored
                        impNode = wsdd.importNode(addMonitor(node), true);
                    }
                } else {
                    if (isMonitored(node)) {  // It's not to be monitored
                        impNode = wsdd.importNode(delMonitor(node), true);
                    } else {                  // It's not already been monitored
                        impNode = wsdd.importNode(node, true);
                    }
                }
                if (service.equals("AdminService")) {
                    // Add "SimpleAuthenticationHandler" and "allowedRoles" parameter
                    // with "admin" as a user account
                    addAuthenticate(node);
                }
                wsdd.getDocumentElement().appendChild(impNode);
            }
            
            // Show the result of deployment
            pane = new JOptionPane();
            if (doDeploy(wsdd)) {
                msg = "The deploy was successful.";
                pane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
            } else {
                msg = "The deploy was NOT successful.";
                pane.setMessageType(JOptionPane.WARNING_MESSAGE);
            }
            pane.setMessage(msg);
            dlg = pane.createDialog(null, title);
            dlg.setVisible(true);
        }
    }
}
