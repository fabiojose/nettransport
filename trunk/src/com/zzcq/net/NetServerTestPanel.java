package com.zzcq.net;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;


public class NetServerTestPanel extends JPanel implements IoHandler {
	
	private final String NEW_LINE = "\r\n";

	private JLabel jLabel = null;

	private JTextField jTextFieldPort = null;

	private JButton jButtonStart = null;

	private JTextArea jTextArea = null;

	private JLabel jLabel1 = null;

	private JLabel jLabel2 = null;

	private JTextField jTextFieldSend = null;

	private JButton jButtonSend = null;

	private JButton jButtonSendFile = null;

	private JLabel jLabel3 = null;

	private JPanel jPanel = null;

	private JScrollPane jScrollPane = null;
	IoAcceptor acceptor = new SocketAcceptor();
	

	private HashSet<IoSession> sessions = new HashSet<IoSession>();
	private boolean bStart = false;
	ActionListener sendListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            send();
        }
    };
	/**
	 * This is the default constructor
	 */
	public NetServerTestPanel() {
		super();
		initialize();
	}

	public void startOrStopServer() {
		if (bStart) {
			stop();
			bStart = false;
			jButtonStart.setText("开启监听");
		} else {
			// Bind
			String port = this.jTextFieldPort.getText();
			if (port == null || port.equals("")) {
				JOptionPane.showMessageDialog(this, "请输入端口号", "连接错误",
						JOptionPane.ERROR_MESSAGE);
				jTextFieldPort.requestFocus();
				jTextFieldPort.selectAll();
				return;
			}
			int portNo = 0;
			try {
				portNo = Integer.parseInt(port);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "端口号错误,请重新输入", "连接错误",
						JOptionPane.ERROR_MESSAGE);
				jTextFieldPort.requestFocus();
				jTextFieldPort.selectAll();
				return;
			}
			start(portNo);
		}
	}

	public void send(ByteBuffer data)
	{
		if (sessions.size()==0)
			return;
		append("发送"+data.limit()+"个字符,内容如下:");
		append(data.getHexDump());
		synchronized (sessions)
		{
			for (IoSession s:sessions)
			{
				s.write(data);
			}
		}
	}
	
	public void send() {
		String message=jTextFieldSend.getText();
		if (message==null || message.length()<1)
			return;
		message=message+NEW_LINE;
		ByteBuffer buf=ByteBuffer.allocate(1024);
		buf.setAutoExpand(true);
		buf.put(message.getBytes());
		buf.flip();		
		send(buf);
		jTextFieldSend.setText("");
	}	
	

	public void sendFile() {
		JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            String fileName=chooser.getSelectedFile().getAbsolutePath();
            File f = new File(fileName);
            long length=f.length();
            if (length>1024)
            {
            	JOptionPane.showMessageDialog(this, "不能发送大于1024字节的文件", "发送错误",
    					JOptionPane.ERROR_MESSAGE);
            	return;
            }
            try
            {
            	int size=(int)length;
            	byte[] data = new byte[size];
            	FileInputStream in = new FileInputStream(f);
            	in.read(data);
            	in.close();
            	ByteBuffer buf = ByteBuffer.allocate(size);
            	buf.put(data);
            	buf.flip();
            	
            	send(buf);
            }
            catch (Exception e)
            {
            	
            }
        }
	}

	private void start(int port) {
		try {
		
		  SocketAcceptorConfig cfg = new SocketAcceptorConfig();
	        cfg.setReuseAddress( false );
	        acceptor.bind(
			        new InetSocketAddress( port ),
			        this, cfg );
		
		
			
			bStart = true;
			jButtonStart.setText("关闭监听");
			jTextArea.setText("> Server Started on Port: " + port + NEW_LINE);
			append("> ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "不能开启监听," + e, "连接错误",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void stop() {
		//...todo
		append("> Server stopped");
		append("> ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		synchronized (sessions)
		{
			Iterator<IoSession> it=sessions.iterator();
			while (it.hasNext())
			{
				IoSession s = it.next();
				s.close();
				it.remove();
			}
		}
		acceptor.unbindAll();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		jLabel3 = new JLabel();
		jLabel3.setBounds(new java.awt.Rectangle(20, 78, 59, 26));
		jLabel3.setText("消息");
		jLabel2 = new JLabel();
		jLabel2.setBounds(new java.awt.Rectangle(18, 388, 38, 26));
		jLabel2.setText("消息");
		jLabel1 = new JLabel();
		jLabel1.setBounds(new java.awt.Rectangle(18, 354, 54, 26));
		jLabel1.setText("广播发送");
		jLabel = new JLabel();
		jLabel.setBounds(new java.awt.Rectangle(20, 29, 43, 26));
		jLabel.setText("端口号");
		this.setLayout(null);
		this.setSize(581, 421);
		this.add(jLabel, null);
		this.add(getJTextFieldPort(), null);
		this.add(getJButtonStart(), null);
		this.add(jLabel1, null);
		this.add(jLabel2, null);
		this.add(getJTextFieldSend(), null);
		this.add(getJButtonSend(), null);
		this.add(getJButtonSendFile(), null);
		this.add(jLabel3, null);
		this.add(getJPanel(), null);
	}

	/**
	 * This method initializes jTextFieldPort
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldPort() {
		if (jTextFieldPort == null) {
			jTextFieldPort = new JTextField("8888");
			jTextFieldPort.setBounds(new java.awt.Rectangle(83, 31, 109, 29));
		}
		return jTextFieldPort;
	}

	/**
	 * This method initializes jButtonStart
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonStart() {
		if (jButtonStart == null) {
			jButtonStart = new JButton();
			jButtonStart.setBounds(new java.awt.Rectangle(230, 30, 156, 35));
			jButtonStart.setText("开启监听");
			jButtonStart.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					startOrStopServer();
				}
			});
		}
		return jButtonStart;
	}

	/**
	 * This method initializes jTextArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setEditable(false);
			jTextArea.setLineWrap(true);
		}
		return jTextArea;
	}

	/**
	 * This method initializes jTextFieldSend
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldSend() {
		if (jTextFieldSend == null) {
			jTextFieldSend = new JTextField();
			jTextFieldSend.setBounds(new java.awt.Rectangle(66, 388, 186, 26));
			jTextFieldSend.addActionListener(sendListener);
		}
		return jTextFieldSend;
	}

	/**
	 * This method initializes jButtonSend
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonSend() {
		if (jButtonSend == null) {
			jButtonSend = new JButton();
			jButtonSend.setBounds(new java.awt.Rectangle(282, 385, 74, 28));
			jButtonSend.setText("发送");
			jButtonSend.addActionListener(sendListener);
		}
		return jButtonSend;
	}

	/**
	 * This method initializes jButtonSendFile
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonSendFile() {
		if (jButtonSendFile == null) {
			jButtonSendFile = new JButton();
			jButtonSendFile.setBounds(new java.awt.Rectangle(360, 385, 94, 28));
			jButtonSendFile.setText("发送文件");
			jButtonSendFile
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							sendFile();
						}
					});
		}
		return jButtonSendFile;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BorderLayout(0, 5));
			jPanel.setBounds(new java.awt.Rectangle(18,118,557,227));
			jPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3,
					0, 3));
			jPanel.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
		}
		return jPanel;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTextArea());
		}
		return jScrollPane;
	}

	public void append(String msg) {
		jTextArea.append(msg + NEW_LINE);
		String text=jTextArea.getText();
		if (text.length()>1024*50)
		{
			text = text.substring(1024*20);
			jTextArea.setText(text);
		}
		
		jTextArea.setCaretPosition(jTextArea.getText().length());
		
		
	}

	public void sessionCreated(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	public void sessionOpened(IoSession session) throws Exception {
		append("打开一个连接,地址:" + session.getRemoteAddress());
		synchronized (sessions)
		{
			sessions.add(session);
		}
	}

	public void sessionClosed(IoSession session) throws Exception {
		append("关闭一个连接,地址:" + session.getRemoteAddress());
		synchronized (sessions)
		{
			sessions.remove(session);
		}
	}

	public void sessionIdle(IoSession arg0, IdleStatus arg1) throws Exception {
		// TODO Auto-generated method stub

	}

	public void exceptionCaught(IoSession session, Throwable arg1)
			throws Exception {
		session.close();

	}

	

	public void messageReceived(IoSession arg0, Object obj) throws Exception {
		ByteBuffer buf=(ByteBuffer )obj;
		append("收到数据，长度：" + buf.limit() + "，内容：");
		append("" + buf.getHexDump());
		append("");
		int len=buf.limit();
		byte[] bytebuf = new byte[len];
		buf.get(bytebuf);
		append(new String(bytebuf));
	}

	public void messageSent(IoSession arg0, Object arg1) throws Exception {
		// TODO Auto-generated method stub
		
	}

} // @jve:decl-index=0:visual-constraint="10,10"
