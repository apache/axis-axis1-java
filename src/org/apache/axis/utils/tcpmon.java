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
package org.apache.axis.utils ;

import javax.swing.* ;
import java.awt.* ;
import java.awt.event.* ;
import java.net.* ;
import javax.swing.table.* ;
import javax.swing.text.* ;
import javax.swing.event.* ;
import javax.swing.plaf.basic.* ;
import java.util.* ;
import java.io.* ;
import java.text.* ;

/**
 * @author Doug Davis (dug@us.ibm.com)
 */

public class tcpmon extends JFrame {
    private JTabbedPane  notebook = null ;

    static private int STATE_COLUMN    = 0 ;
    static private int TIME_COLUMN     = 1 ;
    static private int INHOST_COLUMN   = 2 ;
    static private int OUTHOST_COLUMN  = 3 ;

    class AdminPage extends JPanel {
        public JTextField  port, host, tport ;
        public JTabbedPane noteb ;
        public JCheckBox   proxyBox ;

        public AdminPage( JTabbedPane notebook, String name ) {
            JPanel     mainPane  = null ;
            JPanel     buttons   = null ;
            JButton    addButton = null ;

            setLayout( new BorderLayout() );
            noteb = notebook ;

            GridBagLayout       layout        = new GridBagLayout();
            GridBagConstraints  c             = new GridBagConstraints();

            mainPane = new JPanel(layout);
            
            c.anchor    = GridBagConstraints.WEST ;
            c.gridwidth = GridBagConstraints.REMAINDER;
            mainPane.add( new JLabel("Create a new TCP/IP Monitor... "), c );
            
            c.anchor    = GridBagConstraints.WEST ;
            c.gridwidth = 1 ;
            mainPane.add( new JLabel("Listen Port # "), c );
            
            c.anchor    = GridBagConstraints.WEST ;
            c.gridwidth = GridBagConstraints.REMAINDER ;
            mainPane.add( port = new JTextField(3), c );
            
            c.anchor    = GridBagConstraints.WEST ;
            c.gridwidth = 1 ;
            mainPane.add( new JLabel("Target Hostname "), c );
            
            c.anchor    = GridBagConstraints.WEST ;
            c.gridwidth = GridBagConstraints.REMAINDER ;
            mainPane.add( host = new JTextField(30), c );
            
            c.anchor    = GridBagConstraints.WEST ;
            c.gridwidth = 1 ;
            mainPane.add( new JLabel("Target Port # "), c );
            
            c.anchor    = GridBagConstraints.WEST ;
            c.gridwidth = GridBagConstraints.REMAINDER ;
            mainPane.add( tport = new JTextField(3), c );
            
            c.anchor    = GridBagConstraints.WEST ;
            c.gridwidth = 1 ;
            mainPane.add( addButton = new JButton( "Add" ), c );
            
            c.anchor    = GridBagConstraints.WEST ;
            c.gridwidth = 1 ;
            mainPane.add( proxyBox = new JCheckBox( "Act As A Proxy" ), c );
            
            add( new JScrollPane( mainPane ), BorderLayout.CENTER );

            // addButton.setEnabled( false );
            addButton.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                if ( "Add".equals(event.getActionCommand()) ) {
                String tmp ;
                int    lPort = Integer.parseInt(port.getText());
                String tHost = host.getText();
                int    tPort = 0 ;
                tmp = tport.getText();
                if ( tmp != null && !tmp.equals("") )
                tPort = Integer.parseInt(tmp );
                new Listener( noteb, null, lPort, tHost, tPort, 
                proxyBox.isSelected() );

                port.setText(null);
                host.setText(null);
                tport.setText(null);
                }
                };
                });

            proxyBox.addChangeListener( new BasicButtonListener(proxyBox) {
                public void stateChanged(ChangeEvent event) {
                JCheckBox box = (JCheckBox) event.getSource();
                boolean state = box.isSelected();
                tport.setEnabled( !state );
                host.setEnabled( !state );
                }
                });

            notebook.addTab( name, this );
            notebook.repaint();
            notebook.setSelectedIndex( notebook.getTabCount()-1 );
        }
    };

    class SocketWaiter extends Thread {
        ServerSocket  sSocket = null ;
        Listener      listener ;
        int           port ;
        boolean       pleaseStop = false ;

        public SocketWaiter(Listener l, int p) {
            listener = l ;
            port = p ;
            start();
        }

        public void run() {
            try {
                listener.setLeft( new JLabel(" Waiting for Connection..." ) );
                sSocket = new ServerSocket( port );
                for(;;) {
                    Socket inSocket = sSocket.accept();
                    if ( pleaseStop ) break ;
                    new Connection( listener, inSocket );
                    inSocket = null ;
                }
            }
            catch( Exception exp ) {
                JLabel tmp = new JLabel( exp.toString() );
                tmp.setForeground( Color.red );
                listener.setLeft( tmp );
                listener.setRight( new JLabel("") );
                listener.stop();
            }
        }

        public void halt() {
            try {
                pleaseStop = true ;
                new Socket( "127.0.0.1", port );
                if ( sSocket != null ) sSocket.close();
            }
            catch( Exception e ) {
                e.printStackTrace();
            }
        }
    }

    class SocketRR extends Thread {
        Socket        inSocket  = null ;
        Socket        outSocket  = null ;
        JTextArea     textArea ;
        InputStream   in = null ;
        OutputStream  out = null ;
        boolean       xmlFormat ;
        boolean       done = false ;

        public SocketRR(Socket inputSocket, InputStream inputStream, 
                        Socket outputSocket, OutputStream outputStream, 
                        JTextArea _textArea, boolean format) {
            inSocket = inputSocket ;
            in       = inputStream ;
            outSocket = outputSocket ;
            out       = outputStream ;
            textArea  = _textArea ;
            xmlFormat = format ;
            start();
        }

        public boolean isDone() {
          return( done );
        }

        public void run() {
            try {
                byte[]      buffer = new byte[4096];
                byte[]      tmpbuffer = new byte[8192];
                int         saved = 0 ;
                int         len ;
                int         i1, i2 ;
                int         i ;

                int   thisIndent, nextIndent=0 ;

                for ( ;; ) {
                    len = in.available();
                    if ( len == 0 ) len = 1 ;
                    if ( saved+len > 4096 ) len = 4096-saved ;
                    len = in.read(buffer,saved,len);
                    if ( len == -1 ) break ;

                    // No matter how we may (or may not) format it, send it
                    // on unformatted - we don't want to mess with how its
                    // sent to the other side, just how its displayed
                    if ( out != null ) 
                      out.write( buffer, saved, len );

                    if ( xmlFormat ) {
                        // Do XML Formatting
                        i1 = 0 ;
                        i2 = 0 ;
                        saved = 0 ;
                        for( ; i1 < len ; i1++ ) {
                            if ( buffer[i1] != '<' && buffer[i1] != '/' )
                                tmpbuffer[i2++] = buffer[i1];
                            else {
                                if ( i1+1 < len ) {
                                    byte b1 = buffer[i1];
                                    byte b2 = buffer[i1+1];
                                    thisIndent = -1 ;

                                    if ( b1 == '<' ) {
                                        if ( b2 != '/' )  thisIndent = nextIndent++ ;
                                        else              thisIndent = --nextIndent ;
                                    }
                                    else if ( b1 == '/' ) {
                                        if ( b2 == '>' ) nextIndent-- ;
                                    }

                                    if ( thisIndent != -1 ) {
                                        tmpbuffer[i2++] = (byte) '\n' ;
                                        for ( i = 0 ; i < thisIndent ; i++ )
                                            tmpbuffer[i2++] = (byte) ' ' ;
                                    }

                                    tmpbuffer[i2++] = buffer[i1];
                                }
                                else {
                                    // last char is special - save it
                                    saved = 1 ;
                                }
                            }
                        }
                        textArea.append( new String( tmpbuffer, 0, i2 ) );
                    }
                    else {
                        textArea.append( new String( buffer, 0, len ) );
                    }
                    this.sleep(3);  // Let other threads have a chance to run
                }
                // halt();
                done = true ;
            }
            catch( Exception e ) {
                // e.printStackTrace();
            }
        }
        public void halt() {
            try {
                if ( inSocket != null )  inSocket.close();
                if ( outSocket != null ) outSocket.close();
                inSocket  = null ;
                outSocket = null ;
                if ( in != null ) in.close();
                if ( out != null ) out.close();
                in = null ;
                out = null ;
            }
            catch( Exception e ) {
                e.printStackTrace();
            }
        }
    }

    class Connection extends Thread {
        Listener     listener ;
        boolean      active ;
        String       fromHost ;
        String       time ;
        JTextArea    inputText ;
        JScrollPane  inputScroll ;
        JTextArea    outputText ;
        JScrollPane  outputScroll ;
        Socket       inSocket ;
        Socket       outSocket ;
        Thread       clientThread ;
        Thread       serverThread ;
        SocketRR     rr1 = null ;
        SocketRR     rr2 = null ;
        InputStream  inputStream ;

        public Connection(Listener l, Socket s ) {
            listener = l ;
            inSocket = s ;
            start();
        }

        public Connection(Listener l, InputStream in ) {
            listener = l ;
            inputStream = in ;
            start();
        }

        public void run() {
            try {
                active        = true ;

                if ( inSocket != null ) {
                  InetAddress  h  = inSocket.getInetAddress();
                  fromHost      = h.getHostName();
                }
                else {
                  fromHost = "resend" ;
                }

                DateFormat   df = new SimpleDateFormat("MM/dd/yy hh:mm:ss aa");
                time = df.format( new Date() );
                
                int count = listener.connections.size();
                listener.tableModel.insertRow(count+1, new Object[] { "Active",
                                              time,
                                              fromHost, 
                                              listener.hostField.getText() });
                listener.connections.add( this );
                inputText  = new JTextArea( null, null, 20, 80 );
                inputScroll = new JScrollPane( inputText );
                outputText = new JTextArea( null, null, 20, 80 );
                outputScroll = new JScrollPane( outputText );
                
                ListSelectionModel lsm = listener.connectionTable.getSelectionModel();
                if ( count == 0 || lsm.getLeadSelectionIndex() == 0 ) {
                    listener.outPane.setVisible( false );
                    int divLoc = listener.outPane.getDividerLocation();

                    listener.setLeft( inputScroll );
                    listener.setRight( outputScroll );

                    listener.removeButton.setEnabled(false);
                    listener.removeAllButton.setEnabled(true);
                    listener.saveButton.setEnabled(true);
                    listener.resendButton.setEnabled(true);
                    listener.outPane.setDividerLocation(divLoc);
                    listener.outPane.setVisible( true );
                }

                String targetHost = listener.hostField.getText();
                int    targetPort = Integer.parseInt(listener.tPortField.getText());
                
                InputStream  tmpIn1  = inputStream ;
                OutputStream tmpOut1 = null ;

                InputStream  tmpIn2  = null ;
                OutputStream tmpOut2 = null ;

                if ( tmpIn1 == null )
                  tmpIn1  = inSocket.getInputStream();
                
                if ( inSocket != null ) 
                  tmpOut1 = inSocket.getOutputStream();

                String         bufferedData = null ;
                StringBuffer   buf = null ;

                if ( listener.isProxyBox.isSelected() ) {
                    // Check if we're a proxy
                    int          ch ;
                    byte[]       b = new byte[1];
                    buf = new StringBuffer();
                    String       s ;

                    for ( ;; ) {
                        int len ;

                        len = tmpIn1.read(b,0,1);
                        if ( len == -1 ) break ;
                        s = new String( b );
                        buf.append( s );
                        if ( b[0] != '\n' ) continue ;
                        break ;
                    }

                    bufferedData = buf.toString();
                    inputText.append( bufferedData );

                    if ( bufferedData.startsWith( "GET " ) ||
                         bufferedData.startsWith( "POST " ) ) {
                        int  start, end ;
                        URL  url ;

                        start = bufferedData.indexOf( ' ' )+1;
                        while( bufferedData.charAt(start) == ' ' ) start++ ;
                        end   = bufferedData.indexOf( ' ', start );
                        String tmp = bufferedData.substring( start, end );
                        if ( tmp.charAt(0) == '/' ) tmp = tmp.substring(1);
                        url = new URL( tmp );
                        targetHost = url.getHost();
                        targetPort = url.getPort();
                        if ( targetPort == -1 ) targetPort = 80 ;
                        int index = listener.connections.indexOf( this );
                        listener.tableModel.setValueAt( targetHost, index+1, 
                                                        OUTHOST_COLUMN );

                        bufferedData = bufferedData.substring( 0, start) +
                                       url.getFile() +
                                       bufferedData.substring( end );
                    }
                }

                if ( targetPort == -1 ) targetPort = 80 ;
                outSocket = new Socket(targetHost, targetPort );
                
                tmpIn2  = outSocket.getInputStream();
                tmpOut2 = outSocket.getOutputStream();

                if ( bufferedData != null ) {
                    byte[] b = bufferedData.getBytes();
                    tmpOut2.write( b );
                }

                boolean format = listener.xmlFormatBox.isSelected();

                rr1 = new SocketRR( inSocket, tmpIn1, outSocket, 
                                    tmpOut2, inputText, format );
                rr2 = new SocketRR( outSocket, tmpIn2, inSocket, 
                                    tmpOut1, outputText, format );

                // while( rr1.isAlive() || rr2.isAlive() ) {
                // Only loop as long as the connection to the target
                // machine is available - once that's gone we can stop.
                // The old way, loop until both are closed, left us
                // looping forever since no one closed the 1st one.
                while( !rr2.isDone() ) {
                    Thread.sleep( 10 );
                }
                rr1.halt();
                rr2.halt();

                rr1 = null ;
                rr2 = null ;
                
                active = false ;
                /*
                if ( inSocket != null ) {
                  inSocket.close();
                  inSocket = null ;
                }
                outSocket.close();
                outSocket = null ;
                */

                int index = listener.connections.indexOf( this );
                if ( index >= 0 )
                    listener.tableModel.setValueAt( "Done", 1+index, STATE_COLUMN );
            }
            catch( Exception e ) {
                e.printStackTrace();
            }
        }

        public void halt() {
            try {
                // if ( inSocket  != null ) inSocket.close();
                // inSocket = null ;
                // if ( outSocket != null ) outSocket.close();
                // outSocket = null ;
                if ( rr1 != null ) rr1.halt();
                if ( rr2 != null ) rr2.halt();
            }
            catch( Exception e ) {
                e.printStackTrace();
            }
        }

        public void remove() {
            try {
                halt();
                int index = listener.connections.indexOf( this );
                listener.tableModel.removeRow( index+1 );
                listener.connections.remove( index );
            }
            catch( Exception e ) {
                e.printStackTrace();
            }
        }
    }
    
    class Listener extends JPanel {
        public  Socket      inputSocket     = null ;
        public  Socket      outputSocket    = null ;
        public  JTextField  portField       = null ;
        public  JTextField  hostField       = null ;
        public  JTextField  tPortField      = null ;
        public  JCheckBox   isProxyBox      = null ;
        public  JButton     stopButton      = null ;
        public  JButton     removeButton    = null ;
        public  JButton     removeAllButton = null ;
        public  JCheckBox   xmlFormatBox    = null ;
        public  JButton     saveButton      = null ;
        public  JButton     resendButton    = null ;
        public  JButton     switchButton    = null ;
        public  JButton     closeButton     = null ;
        public  JTable      connectionTable = null ;
        public  DefaultTableModel  tableModel      = null ;
        public  JSplitPane  outPane         = null ;
        public  ServerSocket sSocket        = null ;
        public  SocketWaiter sw = null ;
        public  JPanel      leftPanel       = null ;
        public  JPanel      rightPanel      = null ;
        public  JTabbedPane notebook        = null ;

        final public Vector connections = new Vector();

        public Listener(JTabbedPane _notebook, String name, 
                        int listenPort, String host, int targetPort,
                        boolean isProxy)
        {
            notebook = _notebook ;
            if ( name == null ) name = "Port " + listenPort ;

            setLayout( new BorderLayout() );

            // 1st component is just a row of labels and 1-line entry fields
            /////////////////////////////////////////////////////////////////////
            JPanel top = new JPanel();
            top.setLayout( new BoxLayout(top, BoxLayout.X_AXIS) );
            top.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            top.add( stopButton = new JButton( "Start" ) );
            top.add( Box.createRigidArea(new Dimension(5,0)) );
            top.add( new JLabel( "  Listen Port: ", SwingConstants.RIGHT ) );
            top.add( portField = new JTextField( ""+listenPort, 3 ) );
            top.add( new JLabel( "  Host:", SwingConstants.RIGHT ) );
            top.add( hostField = new JTextField( host, 30 ) );
            top.add( new JLabel( "  Port: ", SwingConstants.RIGHT ) );
            top.add( tPortField = new JTextField( ""+targetPort, 3 ) );
            top.add( Box.createRigidArea(new Dimension(5,0)) );
            top.add( isProxyBox = new JCheckBox("Proxy") );

            isProxyBox.addChangeListener( new BasicButtonListener(isProxyBox) {
                public void stateChanged(ChangeEvent event) {
                JCheckBox box = (JCheckBox) event.getSource();
                boolean state = box.isSelected();
                tPortField.setEnabled( !state );
                hostField.setEnabled( !state );
                }
                });
            isProxyBox.setSelected(isProxy);

            portField.setEditable(false);
            portField.setMaximumSize(new Dimension(50, Short.MAX_VALUE) );
            hostField.setEditable(false);
            hostField.setMaximumSize(new Dimension(85,Short.MAX_VALUE) );
            tPortField.setEditable(false);
            tPortField.setMaximumSize(new Dimension(50,Short.MAX_VALUE) );

            stopButton.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                if ( "Stop".equals(event.getActionCommand()) ) stop();
                if ( "Start".equals(event.getActionCommand()) ) start();
                };
                });

            add( top, BorderLayout.NORTH );

            // 2nd component is a split pane with a table on the top
            // and the request/response text areas on the bottom
            /////////////////////////////////////////////////////////////////////

            tableModel = new DefaultTableModel(new String[] {"State",
                                               "Time",
                                               "Request Host",
                                               "Target Host"}, 
                                               0 );

            connectionTable = new JTable(1,2);
            connectionTable.setModel( tableModel );
            connectionTable.setSelectionMode(ListSelectionModel.
                                                                MULTIPLE_INTERVAL_SELECTION);
            TableColumn col ;
            col = connectionTable.getColumnModel().getColumn(STATE_COLUMN);
            col.setMaxWidth( col.getPreferredWidth()/2 );
            
            ListSelectionModel sel = connectionTable.getSelectionModel();
            sel.addListSelectionListener( new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting()) return ;
                ListSelectionModel m = (ListSelectionModel) event.getSource();
                int divLoc = outPane.getDividerLocation();
                if (m.isSelectionEmpty()) {
                setLeft( new JLabel(" Waiting for Connection..." ) );
                setRight( new JLabel("") );
                removeButton.setEnabled(false);
                removeAllButton.setEnabled(false);
                saveButton.setEnabled(false);
                resendButton.setEnabled(false);
                }
                else {
                int row = m.getLeadSelectionIndex();
                if ( row == 0 ) {
                if ( connections.size() == 0 ) {
                setLeft(new JLabel(" Waiting for connection..."));
                setRight(new JLabel(""));
                removeButton.setEnabled(false);
                removeAllButton.setEnabled(false);
                saveButton.setEnabled(false);
                resendButton.setEnabled(false);
                }
                else {
                Connection conn = (Connection) connections.lastElement();
                setLeft( conn.inputScroll );
                setRight( conn.outputScroll );
                removeButton.setEnabled(false);
                removeAllButton.setEnabled(true);
                saveButton.setEnabled(true);
                resendButton.setEnabled(true);
                }
                }
                else {
                Connection conn = (Connection) connections.get(row-1);
                setLeft( conn.inputScroll );
                setRight( conn.outputScroll );
                removeButton.setEnabled(true);
                removeAllButton.setEnabled(true);
                saveButton.setEnabled(true);
                resendButton.setEnabled(true);
                }
                }
                outPane.setDividerLocation(divLoc);
                }} );
            tableModel.addRow( new Object[] { "---", "Most Recent", "---", "---" } );

            JPanel  tablePane = new JPanel();
            tablePane.setLayout( new BorderLayout() );

            JScrollPane tableScrollPane = new JScrollPane( connectionTable );
            tablePane.add( tableScrollPane, BorderLayout.CENTER );
            JPanel buttons = new JPanel();
            buttons.setLayout( new BoxLayout(buttons, BoxLayout.X_AXIS) );
            buttons.setBorder( BorderFactory.createEmptyBorder(5,5,5,5) );
            buttons.add( removeButton = new JButton("Remove Selected") );
            buttons.add( Box.createRigidArea(new Dimension(5,0)) );
            buttons.add( removeAllButton = new JButton("Remove All") );
            tablePane.add( buttons, BorderLayout.SOUTH );

            removeButton.setEnabled( false );
            removeButton.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                if ( "Remove Selected".equals(event.getActionCommand()) ) remove();
                };
                });

            removeAllButton.setEnabled( false );
            removeAllButton.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                if ( "Remove All".equals(event.getActionCommand()) ) removeAll();
                };
                });

            // Add Response Section
            /////////////////////////////////////////////////////////////////////
            JPanel     pane2     = new JPanel();
            pane2.setLayout( new BorderLayout() );

            leftPanel = new JPanel();
            leftPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
            leftPanel.setLayout( new BoxLayout(leftPanel, BoxLayout.Y_AXIS) );
            leftPanel.add( new JLabel("  Request") );
            leftPanel.add( new JLabel(" Waiting for connection" ));

            rightPanel = new JPanel();
            rightPanel.setLayout( new BoxLayout(rightPanel, BoxLayout.Y_AXIS) );
            rightPanel.add( new JLabel("  Response") );
            rightPanel.add( new JLabel("") );

            outPane = new JSplitPane(0, leftPanel, rightPanel );
            outPane.setDividerSize(4);
            pane2.add( outPane, BorderLayout.CENTER );

            JPanel bottomButtons = new JPanel();
            bottomButtons.setLayout( new BoxLayout(bottomButtons, BoxLayout.X_AXIS));
            bottomButtons.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            bottomButtons.add( xmlFormatBox = new JCheckBox( "XML Format" ) );
            bottomButtons.add( Box.createRigidArea(new Dimension(5,0)) );
            bottomButtons.add( saveButton = new JButton( "Save" ) );
            bottomButtons.add( Box.createRigidArea(new Dimension(5,0)) );
            bottomButtons.add( resendButton = new JButton( "Resend" ) );
            bottomButtons.add( Box.createRigidArea(new Dimension(5,0)) );
            bottomButtons.add( switchButton = new JButton( "Switch Layout" ) );
            bottomButtons.add( Box.createHorizontalGlue() );
            bottomButtons.add( closeButton = new JButton( "Close" ) );
            pane2.add( bottomButtons, BorderLayout.SOUTH );

            saveButton.setEnabled( false );
            saveButton.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                if ( "Save".equals(event.getActionCommand()) ) save();
                };
                });

            resendButton.setEnabled( false );
            resendButton.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                if ( "Resend".equals(event.getActionCommand()) ) resend();
                };
                });

            switchButton.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                if ("Switch Layout".equals(event.getActionCommand()) ) {
                int v = outPane.getOrientation();
                if ( v == 0 )  // top/bottom
                outPane.setOrientation(1);
                else  // left/right
                outPane.setOrientation(0);
                outPane.setDividerLocation(0.5);
                }
                };
                });

            closeButton.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                if ("Close".equals(event.getActionCommand()) )
                close();
                };
                });

            JSplitPane  pane1 = new JSplitPane( 0 );
            pane1.setDividerSize(4);
            pane1.setTopComponent( tablePane );
            pane1.setBottomComponent( pane2 );
            pane1.setDividerLocation( 150 );
            add( pane1, BorderLayout.CENTER );

            // 
            /////////////////////////////////////////////////////////////////////
            sel.setSelectionInterval(0,0);
            outPane.setDividerLocation( 150 );
            notebook.addTab( name, this );
            start();
        };

        public void setLeft(Component left) {
            leftPanel.remove(1);
            leftPanel.add(left);
        }

        public void setRight(Component right) {
            rightPanel.remove(1);
            rightPanel.add(right);
        }

        public void start() {
            int  port = Integer.parseInt( portField.getText() );
            portField.setText( ""+port );
            int i = notebook.indexOfComponent( this );
            notebook.setTitleAt( i, "Port " + port );

            int  tmp = Integer.parseInt( tPortField.getText() );
            tPortField.setText( ""+tmp );

            sw = new SocketWaiter( this, port );
            stopButton.setText( "Stop" );

            portField.setEditable(false);
            hostField.setEditable(false);
            tPortField.setEditable(false);
            isProxyBox.setEnabled(false);
        }

        public void close() {
            stop();
            notebook.remove( this );
        }

        public void stop() {
            try {
                for ( int i = 0 ; i < connections.size() ; i++ ) {
                    Connection conn = (Connection) connections.get( i );
                    conn.halt();
                }
                sw.halt();
                stopButton.setText( "Start" );
                portField.setEditable(true);
                hostField.setEditable(true);
                tPortField.setEditable(true);
                isProxyBox.setEnabled(true);
            }
            catch( Exception e ) {
                e.printStackTrace();
            }
        }

        public void remove() {
            ListSelectionModel lsm = connectionTable.getSelectionModel();
            int bot = lsm.getMinSelectionIndex();
            int top = lsm.getMaxSelectionIndex();

            for ( int i = top ; i >= bot ; i-- ) {
                ((Connection) connections.get(i-1)).remove();
            }
            if ( bot > connections.size() ) bot = connections.size();
            lsm.setSelectionInterval(bot,bot);
        }

        public void removeAll() {
            while ( connections.size() > 0 )
                ((Connection)connections.get(0)).remove();
            ListSelectionModel lsm = connectionTable.getSelectionModel();
            lsm.clearSelection();
            lsm.setSelectionInterval(0,0);
        }

        public void save() {
            JFileChooser  dialog = new JFileChooser( "." );
            int rc = dialog.showSaveDialog( this );
            if ( rc == JFileChooser.APPROVE_OPTION ) {
                try {
                    File             file = dialog.getSelectedFile();
                    FileOutputStream out  = new FileOutputStream( file );
                    
                    ListSelectionModel lsm = connectionTable.getSelectionModel();
                    rc = lsm.getLeadSelectionIndex();
                    if ( rc == 0 ) rc = connections.size();
                    Connection conn = (Connection) connections.get( rc-1 );
                    
                    rc = Integer.parseInt( portField.getText() );
                    out.write( (new String("Listen Port: " + rc + "\n" )).getBytes() );
                    out.write( (new String("Target Host: " + hostField.getText() + 
                                           "\n" )).getBytes() );
                    rc = Integer.parseInt( tPortField.getText() );
                    out.write( (new String("Target Port: " + rc + "\n" )).getBytes() );
                    
                    out.write( (new String("==== Request ====\n" )).getBytes() );
                    out.write( conn.inputText.getText().getBytes() );
                    
                    out.write( (new String("==== Response ====\n" )).getBytes() );
                    out.write( conn.outputText.getText().getBytes() );
                    
                    out.close();
                }
                catch( Exception e ) {
                    e.printStackTrace();
                }
            }
        }

        public void resend() {
            int rc ;
            try {
                ListSelectionModel lsm = connectionTable.getSelectionModel();
                rc = lsm.getLeadSelectionIndex();
                if ( rc == 0 ) rc = connections.size();
                Connection conn = (Connection) connections.get( rc-1 );
                if ( rc > 0 ) {
                  lsm.clearSelection();
                  lsm.setSelectionInterval(0,0);
                }
                
                InputStream in = null ;
                in = new ByteArrayInputStream( conn.inputText.getText().getBytes() );
                new Connection( this, in );
            }
            catch( Exception e ) {
                e.printStackTrace();
            }
        }
    };

    public tcpmon(int listenPort, String targetHost, int targetPort) {
        super( "TCPMonitor" );

        notebook = new JTabbedPane();
        this.getContentPane().add( notebook );

        new AdminPage( notebook, "Admin" );

        if ( listenPort != 0 ) {
            if ( targetHost == null )
                new Listener( notebook, null, listenPort, 
                              targetHost, targetPort, true );
            else
                new Listener( notebook, null, listenPort, 
                              targetHost, targetPort, false );
            notebook.setSelectedIndex( 1 );
        }

        this.pack();
        this.setSize( 600, 600 );
        this.setVisible( true );
    }

    protected void processWindowEvent(WindowEvent event) {
        switch( event.getID() ) {
        case WindowEvent.WINDOW_CLOSING: exit();
                                         break ;
        default: super.processWindowEvent(event);
                 break ;
        }
    }

    private void exit() {
        System.exit(0);
    }

    public void setInputPort(int port) {
    }

    public void setOutputHostPort(char hostName, int port) {
    }

    public static void main(String[] args) {
        try {
            if ( args.length == 3 ) {
                int p1 = Integer.parseInt( args[0] );
                int p2 = Integer.parseInt( args[2] );
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new tcpmon( p1, args[1], p2 );
            }
            else if ( args.length == 1 ) {
                int p1 = Integer.parseInt( args[0] );
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new tcpmon( p1, null, 0 );
            }
            else if ( args.length != 0 ) {
                System.err.println( "Usage: " +
                                    "tcpmon [listenPort targetHost targetPort]\n");
            }
            else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new tcpmon(0,null,0);
            }
        }
        catch( Throwable exp ) {
            exp.printStackTrace();
        }
    }
}
