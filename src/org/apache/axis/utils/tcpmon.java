/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
import java.util.* ;
import java.io.* ;
import java.text.* ;

/**
 * @author Doug Davis (dug@us.ibm.com)
 */

public class tcpmon extends JFrame {
  private JTabbedPane  notebook = null ;

  class AdminPage extends JPanel {
    public JTextField  port, host, tport ;
    public JTabbedPane noteb ;

    public AdminPage( JTabbedPane notebook, String name ) {
      JPanel   mainPane  = null ;
      JPanel   buttons   = null ;
      JButton  addButton = null ;

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
  
      add( new JScrollPane( mainPane ), BorderLayout.CENTER );

      // addButton.setEnabled( false );
      addButton.addActionListener( new ActionListener() {
          public void actionPerformed(ActionEvent event) {
            if ( "Add".equals(event.getActionCommand()) ) {
              int lPort = Integer.parseInt(port.getText());
              String tHost = host.getText();
              int tPort = Integer.parseInt(tport.getText());
              new Listener( noteb, null, lPort, tHost, tPort );

              port.setText(null);
              host.setText(null);
              tport.setText(null);
            }
          };
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
    Connection    conn;
    Socket        inSocket  = null ;
    Socket        outSocket  = null ;
    JTextArea     textArea ;
    InputStream   in = null ;
    OutputStream  out = null ;

    public SocketRR(Connection c, Socket i, JTextArea t, Socket o) {
      conn = c ;
      inSocket = i ;
      outSocket = o ;
      textArea = t ;
      start();
    }
    public void run() {
      try {
        byte[]      buffer = new byte[4096];
        int         len ;

        in     = inSocket.getInputStream();
        out    = outSocket.getOutputStream();
        for ( ;; ) {
          len = in.available();
          if ( len == 0 ) len = 1 ;
          if ( len > 4096 ) len = 4096 ;
          len = in.read(buffer,0,len);
          if ( len == -1 ) break ;
          textArea.append( new String( buffer, 0, len ) );
          out.write( buffer, 0, len );
          this.sleep(5);
        }
        halt();
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

    public Connection(Listener l, Socket s ) {
      listener = l ;
      inSocket = s ;
      start();
    }

    public void run() {
      try {
        InetAddress  h  = inSocket.getInetAddress();
        DateFormat   df = new SimpleDateFormat("MM/dd/yy hh:mm:ss aa");
  
        active        = true ;
        fromHost      = h.getHostName();
        time          = df.format( new Date() );
  
        int count = listener.connections.size();
        listener.tableModel.insertRow(count+1, new Object[] { "Active",
                                                              fromHost, time });
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
          // listener.xmlFormatButton.setEnabled(true);
          listener.saveButton.setEnabled(true);
          listener.outPane.setDividerLocation(divLoc);
          listener.outPane.setVisible( true );
        }
  
        outSocket = new Socket(listener.hostField.getText(),
                               Integer.parseInt(listener.tPortField.getText()));
  
        rr1 = new SocketRR( this, inSocket, inputText, outSocket );
        rr2 = new SocketRR( this, outSocket, outputText, inSocket );
  
        while( rr1.isAlive() || rr2.isAlive() ) {
                Thread.sleep( 10 );
        }
        rr1 = null ;
        rr2 = null ;
  
        active = false ;
        inSocket.close();
        inSocket = null ;
        outSocket.close();
        outSocket = null ;

        int index = listener.connections.indexOf( this );
        if ( index >= 0 )
          listener.tableModel.setValueAt( "Done", 1+index, 0 );
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
    public  JButton     stopButton      = null ;
    public  JButton     removeButton    = null ;
    public  JButton     removeAllButton = null ;
    public  JButton     xmlFormatButton = null ;
    public  JButton     saveButton      = null ;
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
                    int listenPort, String host, int targetPort)
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
      top.add( hostField = new JTextField( host, 15 ) );
      top.add( new JLabel( "  Port: ", SwingConstants.RIGHT ) );
      top.add( tPortField = new JTextField( ""+targetPort, 3 ) );

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
      // and the input/output text areas on the bottom
      /////////////////////////////////////////////////////////////////////

      tableModel = new DefaultTableModel(new String[] {"State",
                                                       "Host",
                                                       "Time"}, 
                                         0 );

      connectionTable = new JTable(1,2);
      connectionTable.setModel( tableModel );
      connectionTable.setSelectionMode(ListSelectionModel.
                                          MULTIPLE_INTERVAL_SELECTION);
      
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
            xmlFormatButton.setEnabled(false);
            saveButton.setEnabled(false);
          }
          else {
            int row = m.getLeadSelectionIndex();
            if ( row == 0 ) {
              if ( connections.size() == 0 ) {
                setLeft(new JLabel(" Waiting for connection..."));
                setRight(new JLabel(""));
                removeButton.setEnabled(false);
                removeAllButton.setEnabled(false);
                xmlFormatButton.setEnabled(false);
                saveButton.setEnabled(false);
              }
              else {
                Connection conn = (Connection) connections.lastElement();
                setLeft( conn.inputScroll );
                setRight( conn.outputScroll );
                removeButton.setEnabled(false);
                removeAllButton.setEnabled(true);
                // xmlFormatButton.setEnabled(true);
                saveButton.setEnabled(true);
              }
            }
            else {
              Connection conn = (Connection) connections.get(row-1);
              setLeft( conn.inputScroll );
              setRight( conn.outputScroll );
              removeButton.setEnabled(true);
              removeAllButton.setEnabled(true);
              // xmlFormatButton.setEnabled(true);
              saveButton.setEnabled(true);
            }
          }
          outPane.setDividerLocation(divLoc);
        }} );
      tableModel.addRow( new Object[] { "---", "Most Recent", "---" } );

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

      // Add Output Section
      /////////////////////////////////////////////////////////////////////
      JPanel     pane2     = new JPanel();
      pane2.setLayout( new BorderLayout() );

      leftPanel = new JPanel();
      leftPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
      leftPanel.setLayout( new BoxLayout(leftPanel, BoxLayout.Y_AXIS) );
      leftPanel.add( new JLabel("  Input") );
      leftPanel.add( new JLabel(" Waiting for connection" ));

      rightPanel = new JPanel();
      rightPanel.setLayout( new BoxLayout(rightPanel, BoxLayout.Y_AXIS) );
      rightPanel.add( new JLabel("  Output") );
      rightPanel.add( new JLabel("") );

      outPane = new JSplitPane(0, leftPanel, rightPanel );
      outPane.setDividerSize(4);
      pane2.add( outPane, BorderLayout.CENTER );

      JPanel bottomButtons = new JPanel();
      bottomButtons.setLayout( new BoxLayout(bottomButtons, BoxLayout.X_AXIS));
      bottomButtons.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
      bottomButtons.add( xmlFormatButton = new JButton( "XML Format" ) );
      bottomButtons.add( Box.createRigidArea(new Dimension(5,0)) );
      bottomButtons.add( saveButton = new JButton( "Save" ) );
      bottomButtons.add( Box.createRigidArea(new Dimension(5,0)) );
      bottomButtons.add( switchButton = new JButton( "Switch Layout" ) );
      bottomButtons.add( Box.createHorizontalGlue() );
      bottomButtons.add( closeButton = new JButton( "Close" ) );
      pane2.add( bottomButtons, BorderLayout.SOUTH );

      xmlFormatButton.setEnabled( false );
      xmlFormatButton.addActionListener( new ActionListener() {
          public void actionPerformed(ActionEvent event) {
            if ( "XML Format".equals(event.getActionCommand()) ) xmlFormat();
          };
        });

      saveButton.setEnabled( false );
      saveButton.addActionListener( new ActionListener() {
          public void actionPerformed(ActionEvent event) {
            if ( "Save".equals(event.getActionCommand()) ) save();
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

    public void xmlFormat() {
      System.err.println("Formatting...");
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
  
          out.write( (new String("==== Input ====\n" )).getBytes() );
          out.write( conn.inputText.getText().getBytes() );
  
          out.write( (new String("==== Output ====\n" )).getBytes() );
          out.write( conn.outputText.getText().getBytes() );
  
          out.close();
        }
        catch( Exception e ) {
          e.printStackTrace();
        }
      }
    }
  };

  public tcpmon(int listenPort, String targetHost, int targetPort) {
    super( "TCPMonitor" );

    notebook = new JTabbedPane();
    this.getContentPane().add( notebook );

    new AdminPage( notebook, "Admin" );

    if ( listenPort != 0 ) {
      new Listener( notebook, null, listenPort, targetHost, targetPort );
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
