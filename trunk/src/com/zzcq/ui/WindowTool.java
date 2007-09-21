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
	public static void setFont() {
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		Font[] fonts = ge.getAllFonts();
		boolean findFont = false;
		for (int i = 0; i < fonts.length; i++) {
			if (fonts[i].getFontName().equals("宋体")) {
				findFont = true;
				break;
			}
		}
		Font font = null;
		/*
		 * if (!findFont) font = new Font(UIResource.getString(
		 * "Default_font"),Font.PLAIN,12); else font=new
		 * Font("宋体",Font.PLAIN,12);
		 */
		if (!findFont)
			return;

		font = new Font("宋体", Font.PLAIN, 12);
		// FontUIResource fontRes = new javax.swing.plaf.FontUIResource(myFont);
		// setUIFont(fontRes);
		UIManager.put("Button.font", font);
		UIManager.put("ToggleButton.font", font);
		UIManager.put("RadioButton.font", font);
		UIManager.put("CheckBox.font", font);
		UIManager.put("ColorChooser.font", font);
		UIManager.put("ToggleButton.font", font);
		UIManager.put("ComboBox.font", font);
		UIManager.put("ComboBoxItem.font", font);
		UIManager.put("InternalFrame.titleFont", font);
		UIManager.put("Label.font", font);
		UIManager.put("List.font", font);
		UIManager.put("MenuBar.font", font);
		UIManager.put("Menu.font", font);
		UIManager.put("MenuItem.font", font);
		UIManager.put("RadioButtonMenuItem.font", font);
		UIManager.put("CheckBoxMenuItem.font", font);
		UIManager.put("PopupMenu.font", font);
		UIManager.put("OptionPane.font", font);
		UIManager.put("Panel.font", font);
		UIManager.put("ProgressBar.font", font);
		UIManager.put("ScrollPane.font", font);
		UIManager.put("Viewport", font);
		UIManager.put("TabbedPane.font", font);
		UIManager.put("TableHeader.font", font);
		UIManager.put("TextField.font", font);
		UIManager.put("PasswordFiled.font", font);
		UIManager.put("TextArea.font", font);
		UIManager.put("TextPane.font", font);
		UIManager.put("EditorPane.font", font);
		UIManager.put("TitledBorder.font", font);
		UIManager.put("ToolBar.font", font);
		UIManager.put("ToolTip.font", font);
		UIManager.put("Tree.font", font);
	}
}
