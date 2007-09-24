package com.zzcq.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Window;

import javax.swing.UIManager;

public class WindowTool {
	public static void centerWindow(Window window) {
		Dimension dim = window.getToolkit().getScreenSize();
		window.setLocation(dim.width / 2 - window.getWidth() / 2, dim.height
				/ 2 - window.getHeight() / 2);
	}

	public static Frame getParentFrame(Component compOnApplet) {
		Container c = compOnApplet.getParent();
		while (c != null) {
			if (c instanceof Frame)
				return (Frame) c;
			c = c.getParent();
		}
		return null;
	}	
}
