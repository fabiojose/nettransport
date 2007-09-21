package com.zzcq.net;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;

import com.jhe.hexed.JHexEditor;

public class NetClientTestPanel extends JPanel implements IoHandler {	
	private JLabel jLabel = null;

	private JTextField jTextFieldPort = null;

	private JButton jButtonStart = null;

	private JHexEditor jTextArea = null;// �յ�������

	private JHexEditor jTextArea_send = null;// ���͵�����
	
	private JHexEditor jTextFieldSend = null;

	private JButton jButtonSend = null;

	private JButton jButtonSendFile = null;

	private JPanel jPanel = null;

	private JScrollPane jScrollPane = null;

	private IoSession session = null;

	private boolean bStart = false;

	private boolean fileOpened = false;

	private BufferedOutputStream bufOut = null;

	SocketConnector connector;

	ActionListener sendListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			send();
		}
	};

	private JLabel jLabelip = null;

	private JTextField jTextFieldIp = null;

	private JTextField jTextFieldFileSave = null;

	private JButton jButtonSave = null;

	ByteBuffer buffer_read = ByteBuffer.allocate(1024 * 100);

	ByteBuffer buffer_send = ByteBuffer.allocate(1024 * 100);

	/**
	 * This is the default constructor
	 */
	public NetClientTestPanel() {
		super();
		initialize();
	}

	public void openOrSaveFile() {
		if (!fileOpened) {
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File("."));
			int returnVal = chooser.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				String fileName = chooser.getSelectedFile().getAbsolutePath();
				this.jTextFieldFileSave.setText(fileName);

				try {
					FileOutputStream out = new FileOutputStream(fileName);
					bufOut = new BufferedOutputStream(out);
				} catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(this, "���ܱ�����ļ�", "���ļ�����",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				fileOpened = true;
				this.jButtonSave.setText("����");
			}
		} else {
			try {
				bufOut.flush();
				bufOut.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "���ܱ�����ļ�", "�����ļ�����",
						JOptionPane.ERROR_MESSAGE);
			}
			fileOpened = false;
			this.jButtonSave.setText("�򿪱���");

		}
	}

	public void startOrStopServer() {
		if (bStart) {
			stop();
			bStart = false;
			jButtonStart.setText("��������");
		} else {
			// Bind
			start();
		}
	}

	private void start() {
		String port = this.jTextFieldPort.getText();
		if (port == null || port.equals("")) {
			JOptionPane.showMessageDialog(this, "������˿ں�", "���Ӵ���",
					JOptionPane.ERROR_MESSAGE);
			jTextFieldPort.requestFocus();
			jTextFieldPort.selectAll();
			return;
		}
		int portNo = 0;
		try {
			portNo = Integer.parseInt(port);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "�˿ںŴ���,����������", "���Ӵ���",
					JOptionPane.ERROR_MESSAGE);
			jTextFieldPort.requestFocus();
			jTextFieldPort.selectAll();
			return;
		}
		try {
			// Create TCP/IP connector.
			SocketConnectorConfig cfg = new SocketConnectorConfig();
			cfg.setConnectTimeout(1);
			connector = new SocketConnector();

			// Start communication.
			ConnectFuture future=connector.connect(new InetSocketAddress(
					this.jTextFieldIp.getText(), portNo), this,cfg);
			future.join();
			session = future.getSession();
			bStart = true;
			jButtonStart.setText("�ر�����");
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "���ܴ����ӣ�" + e, "���Ӵ���",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void send(byte[] data) {
		if (session == null)
			return;
		// append("����" + data.limit() + "���ַ�,��������:");
		// append(data.getHexDump());

		ByteBuffer wb = ByteBuffer.allocate(data.length);
		wb.put(data);
		wb.flip();

		session.write(wb);

		buffer_send.limit(buffer_send.capacity());
		buffer_send.put(data);
		showSend();
	}

	public void send() {
		byte[] data = jTextFieldSend.getBuffer();
		if (data.length > 0 && session != null) {
			send(data);
			jTextFieldSend.setBuffer("".getBytes());
			jTextFieldSend.repaint();
		}
	}

	public void sendFile() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String fileName = chooser.getSelectedFile().getAbsolutePath();
			File f = new File(fileName);
			long length = f.length();
			if (length > 2048) {
				JOptionPane.showMessageDialog(this, "���ܷ��ʹ���2048�ֽڵ��ļ�", "���ʹ���",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				int size = (int) length;
				byte[] data = new byte[size];
				FileInputStream in = new FileInputStream(f);
				in.read(data);
				in.close();

				send(data);
			} catch (Exception e) {

			}
		}
	}

	

	private void stop() {

		if (session != null)
			session.close();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		jLabelip = new JLabel("IP");
		
		jLabel = new JLabel("�˿ں�");

		this.setSize(581, 421);

		JPanel topPanel = new JPanel();
		JPanel centerPanel = new JPanel();
		JPanel bottomPanel = new JPanel();

		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
		topPanel.add(jLabelip);
		topPanel.add(getJTextFieldIp());
		topPanel.add(jLabel);
		topPanel.add(getJTextFieldPort());
		topPanel.add(getJButtonStart());

		centerPanel.setLayout(new GridLayout(2, 1));
		// JScrollPane sp = new JScrollPane();
		jTextArea = new JHexEditor("".getBytes());
		// sp.setViewportView(jTextArea);
		jTextArea.setBorder(new javax.swing.border.TitledBorder("�յ�"));

		// JScrollPane sps = new JScrollPane();
		jTextArea_send = new JHexEditor("".getBytes());
		// sps.setViewportView(jTextArea_send);
		jTextArea_send.setBorder(new javax.swing.border.TitledBorder("����"));

		centerPanel.add(jTextArea);
		centerPanel.add(jTextArea_send);

		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
		// bottomPanel.add(jLabel1);
		bottomPanel.add(getJTextFieldSend());
		bottomPanel.add(getJButtonSend());
		// bottomPanel.add(getJTextFieldFileSave());
		bottomPanel.setBorder(new javax.swing.border.TitledBorder("׼���������ݣ�"));

		this.setLayout(new BorderLayout());
		this.add(topPanel, BorderLayout.NORTH);
		this.add(centerPanel, BorderLayout.CENTER);
		this.add(bottomPanel, BorderLayout.SOUTH);
		getJTextFieldSend().setEditable(true);

	}

	/**
	 * This method initializes jTextFieldPort
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldPort() {
		if (jTextFieldPort == null) {
			jTextFieldPort = new JTextField("8888");
			jTextFieldPort.setBounds(new java.awt.Rectangle(268, 24, 79, 35));
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
			jButtonStart.setBounds(new java.awt.Rectangle(361, 24, 156, 35));
			jButtonStart.setActionCommand("��������");
			jButtonStart.setText("��������");
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
	private JHexEditor getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JHexEditor("".getBytes());

		}
		return jTextArea;
	}

	/**
	 * This method initializes jTextFieldSend
	 * 
	 * @return javax.swing.JTextField
	 */
	private JHexEditor getJTextFieldSend() {
		if (jTextFieldSend == null) {
			jTextFieldSend = new JHexEditor("".getBytes());

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
			jButtonSend.setBounds(new java.awt.Rectangle(320, 349, 74, 28));
			jButtonSend.setText("����");
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
			jButtonSendFile.setBounds(new java.awt.Rectangle(398, 349, 94, 28));
			jButtonSendFile.setText("�����ļ�");
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

			jPanel.setLayout(new GridLayout(2, 1));
			jPanel.setBounds(new java.awt.Rectangle(18, 118, 557, 227));
			jPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3,
					0, 3));
			// jPanel.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
			JScrollPane sp = new JScrollPane();
			jTextArea = new JHexEditor("".getBytes());

			sp.setViewportView(jTextArea);
			sp.setBorder(new javax.swing.border.TitledBorder("�յ�"));
			jPanel.add(sp);

			JScrollPane sps = new JScrollPane();
			jTextArea_send = new JHexEditor("".getBytes());

			sps.setViewportView(jTextArea_send);
			sps.setBorder(new javax.swing.border.TitledBorder("����"));
			jPanel.add(sps);
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

	// public void append(String msg) {
	// jTextArea.append(msg + NEW_LINE);
	// jTextArea.setCaretPosition(jTextArea.getText().length());
	// }

	public void sessionCreated(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	public void sessionOpened(IoSession s) throws Exception {
		// append("��һ������,��ַ:" + session.getRemoteAddress());

		session = s;

	}

	public void sessionClosed(IoSession s) throws Exception {
		// append("�ر�һ������,��ַ:" + session.getRemoteAddress());
		session = null;
		bStart=false;
		jButtonStart.setText("��������");
	}

	public void sessionIdle(IoSession arg0, IdleStatus arg1) throws Exception {
		// TODO Auto-generated method stub

	}

	public void exceptionCaught(IoSession session, Throwable arg1)
			throws Exception {
		session.close();

	}

	/**
	 * This method initializes jTextFieldIp
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldIp() {
		if (jTextFieldIp == null) {
			jTextFieldIp = new JTextField();
			jTextFieldIp.setBounds(new java.awt.Rectangle(62, 24, 146, 35));
			jTextFieldIp.setText("127.0.0.1");
		}
		return jTextFieldIp;
	}

	/**
	 * This method initializes jTextFieldFileSave
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldFileSave() {
		if (jTextFieldFileSave == null) {
			jTextFieldFileSave = new JTextField();
			jTextFieldFileSave.setBounds(new java.awt.Rectangle(325, 387, 244,
					19));
			jTextFieldFileSave.setEditable(false);
		}
		return jTextFieldFileSave;
	}

	/**
	 * This method initializes jButtonSave
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonSave() {
		if (jButtonSave == null) {
			jButtonSave = new JButton();
			jButtonSave.setBounds(new java.awt.Rectangle(215, 388, 102, 20));
			jButtonSave.setText("�򿪱���");
			jButtonSave.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					openOrSaveFile();
				}
			});
		}
		return jButtonSave;
	}

	public void showSend() {
		buffer_send.limit(buffer_send.position());
		buffer_send.position(0);
		byte[] data = new byte[buffer_send.limit()];
		buffer_send.get(data);
		String ret = new String(data);
		jTextArea_send.setBuffer(ret.getBytes());
		// jTextArea_send.setCaretPosition(jTextArea_send.getText().length());
	}

	public void showRead() {
		// jTextArea.append(msg + NEW_LINE);
		buffer_read.limit(buffer_read.position());
		buffer_read.position(0);
		byte[] data = new byte[buffer_read.limit()];
		buffer_read.get(data);
		String ret = new String(data);
		jTextArea.setBuffer(ret.getBytes());
		// jTextArea.setCaretPosition(jTextArea.getText().length());

	}

	public void messageReceived(IoSession arg0, Object obj) throws Exception {
		ByteBuffer buf = (ByteBuffer) obj;
		// append("�յ����ݣ����ȣ�" + buf.limit() + "�����ݣ�");
		// append("" + buf.getHexDump());
		buffer_read.limit(buffer_read.capacity());
		buffer_read.put(buf);
		showRead();
		// int length=buf.remaining();
		// byte[] data = new byte[length];
		// buf.get(data);
		// append("text:"+new String(data));
		// try
		// {
		// if (fileOpened)
		// bufOut.write(data);
		// }
		// catch (Exception e)
		// {
		//			
		// }
	}

	public void messageSent(IoSession arg0, Object arg1) throws Exception {
		// TODO Auto-generated method stub

	}

} // @jve:decl-index=0:visual-constraint="10,10"
