package com.zzcq.net;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.apache.log4j.PropertyConfigurator;

import com.zzcq.ui.WindowTool;

public class NetTestFrame extends JFrame{

	private JTabbedPane jTabbedPane = null;
	private NetServerTestPanel serverPanel ;  //  @jve:decl-index=0:visual-constraint="10,472"
	private NetClientTestPanel clientPanel ;  //  @jve:decl-index=0:visual-constraint="10,472"

	/**
	 * This method initializes 
	 * 
	 */
	public NetTestFrame() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        this.setSize(new java.awt.Dimension(600,480));
        this.setTitle("ÍøÂç²âÊÔ--0.2.2	20070919");
        this.setContentPane(getJTabbedPane());
        serverPanel = new NetServerTestPanel();
        serverPanel.setSize(new java.awt.Dimension(465,437));
        clientPanel = new NetClientTestPanel();
        clientPanel.setSize(new java.awt.Dimension(465,437));
        
        jTabbedPane.add("¿Í»§¶Ë",clientPanel);
        jTabbedPane.add("·þÎñ¼àÌý",serverPanel);	
        
        
        WindowTool.centerWindow(this);
	}

	/**
	 * This method initializes jTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
		}
		return jTabbedPane;
	}
	
	public static void main(String[] args)
	{
		PropertyConfigurator.configure("log.properties");
		NetTestFrame f = new NetTestFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	
}  //  @jve:decl-index=0:visual-constraint="10,10"
