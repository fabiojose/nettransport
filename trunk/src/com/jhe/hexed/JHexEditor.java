package com.jhe.hexed;

import static com.jhe.hexed.JHexEditor.font;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Created by IntelliJ IDEA.
 * User: laullon
 * Date: 08-abr-2003
 * Time: 13:21:09
 */
public class JHexEditor extends JPanel implements FocusListener,AdjustmentListener,MouseWheelListener
{
    byte[] buffer;
    String encoding="UTF-8";
    public int cursor;
    protected static Font font=new Font("Monospaced",0,12);
    protected int border=2;
    public boolean DEBUG=false;    
    private JScrollBar sb;
    private int inicio=0;// start line number
    private int lineas=1;//show line count
    
    boolean bMacHex=true;//标刻显示
    boolean bHex = false;//
    boolean editable = false;

    JTextArea textArea = new JTextArea();
    CardLayout cardLayout =new CardLayout();
    JPanel root;//
    
    public JHexEditor(byte[] buff)
    {
        super();
        this.buffer=buff;
        
        this.addMouseWheelListener(this);

        sb=new JScrollBar(JScrollBar.VERTICAL);
        sb.addAdjustmentListener(this);
        sb.setMinimum(0);
        //sb.setMaximum(buff.length/getLineas());
        sb.setMaximum((buffer.length+15)/16);


        JPanel p1,p2,p3;
        //centro
        p1=new JPanel(new BorderLayout(1,1));
        p1.add(new JHexEditorHEX(this),BorderLayout.CENTER);
        p1.add(new Columnas(),BorderLayout.NORTH);

        // izq.
        p2=new JPanel(new BorderLayout(1,1));
        p2.add(new Filas(),BorderLayout.CENTER);
        p2.add(new Caja(),BorderLayout.NORTH);

        // der
        p3=new JPanel(new BorderLayout(1,1));
        p3.add(sb,BorderLayout.EAST);
        p3.add(new JHexEditorASCII(this),BorderLayout.CENTER);
        p3.add(new Caja(),BorderLayout.NORTH);

        JPanel panel=new JPanel();
        panel.setLayout(new BorderLayout(1,1));
        panel.add(p1,BorderLayout.CENTER);
        panel.add(p2,BorderLayout.WEST);
        panel.add(p3,BorderLayout.EAST);

        this.setLayout(new BorderLayout(1,1));
        
        root = new JPanel();
        root.setLayout(cardLayout);
        
        root.add(panel,"H");
        JScrollPane sp = new JScrollPane();	
        sp.setViewportView(textArea);
        root.add(sp,"S");
        
        
        
        this.add(root,BorderLayout.CENTER);     
        JButton b = new JButton("字符串/二进制");
        b.addActionListener(new ActionListener()
        {

			public void actionPerformed(ActionEvent e) {
				changeHex();
			}        	
        }
        );
       
        this.add(b,BorderLayout.NORTH);
        textArea.setEditable(false);
        cardLayout.show(root, "S"); 
    }
  
    
    private void changeHex()
    {
    	bHex = !bHex;
    	if (bHex)
    	{
    		int pos=textArea.getCaretPosition();
    		String ret=textArea.getText();
    		
    		int hpos=0;
    		for (int i=0;i<pos;i++)
    		{
    			char c = ret.charAt(i);
    			if (c<128)
    				hpos=hpos+1;
    			else
    				hpos=hpos+2;
    		}
    		cursor=hpos;
    		actualizaCursor();
    		//buffer=textArea.getText().getBytes();
                
    		cardLayout.show(root, "H");
    		if (cursor >= buffer.length)
    			cursor = buffer.length-1;
    	}
    	else
    	{
    		String ret=new String(buffer);
    		textArea.setText(ret);
    		
    		int pos=0;
    		for (int i=0;i<ret.length();i++)
    		{
    			if (pos>=cursor)
    				break;
    			char c = ret.charAt(i);
    			if (c<128)
    				pos=pos+1;
    			else
    				pos=pos+2;
    		}
    		textArea.setCaretPosition(pos);
    		cardLayout.show(root, "S"); 
    		textArea.requestFocus();
    	}
    	//this.repaint();
    }

    public void paint(Graphics g)
    {
        
        super.paint(g);
    }

    protected void actualizaCursor()
    {
        int n=(cursor/16);

      //  System.out.print("- "+inicio+"<"+n+"<"+(lineas+inicio)+"("+lineas+")");

        if(n<inicio) inicio=n;
        else if(n>=inicio+lineas) inicio=n-(lineas-1);

       // System.out.println(" - "+inicio+"<"+n+"<"+(lineas+inicio)+"("+lineas+")");

        repaint();
    }

    protected int getInicio()
    {
        return inicio;
    }

    protected int getLineas()
    {
        return lineas;
    }

    protected void fondo(Graphics g,int x,int y,int s)
    {
        FontMetrics fn=getFontMetrics(font);
        g.fillRect(((fn.stringWidth(" ")+1)*x)+border,(fn.getHeight()*y)+border,((fn.stringWidth(" ")+1)*s),fn.getHeight()+1);
    }

    protected void cuadro(Graphics g,int x,int y,int s)
    {
        FontMetrics fn=getFontMetrics(font);
        g.drawRect(((fn.stringWidth(" ")+1)*x)+border,(fn.getHeight()*y)+border,((fn.stringWidth(" ")+1)*s),fn.getHeight()+1);
    }

    protected void printString(Graphics g,String s,int x,int y)
    {
        FontMetrics fn=getFontMetrics(font);
        g.drawString(s,((fn.stringWidth(" ")+1)*x)+border,((fn.getHeight()*(y+1))-fn.getMaxDescent())+border);
    }

    public void focusGained(FocusEvent e)
    {
        this.repaint();
    }

    public void focusLost(FocusEvent e)
    {
        this.repaint();
    }

    public void adjustmentValueChanged(AdjustmentEvent e)
    {
        inicio=e.getValue();
        if(inicio<0) inicio=0;
        FontMetrics fn=getFontMetrics(font);
        Rectangle rec=this.getBounds();
        lineas=(rec.height/fn.getHeight());
        int n=(buffer.length+15)/16;
        if(lineas>n) { lineas=n; inicio=0; }

        //sb.setValues(getInicio(),getLineas(),0,(buffer.length+15)/16);
        //sb.setValueIsAdjusting(true);
        sb.setValue(getInicio());
        repaint();
    }

    public void mouseWheelMoved(MouseWheelEvent e)
    {
        inicio+=(e.getUnitsToScroll());
        if((inicio+lineas)>=buffer.length/16) inicio=(buffer.length/16)-lineas;
        if(inicio<0) inicio=0;
        repaint();
    }

    public void keyPressed(KeyEvent e)
    {
        switch(e.getKeyCode())
        {
            case 33:    // rep
                if(cursor>=(16*lineas)) cursor-=(16*lineas);
                actualizaCursor();
                break;
            case 34:    // fin
                if(cursor<(buffer.length-(16*lineas))) cursor+=(16*lineas);
                actualizaCursor();
                break;
            case 35:    // fin
                cursor=buffer.length-1;
                actualizaCursor();
                break;
            case 36:    // ini
                cursor=0;
                actualizaCursor();
                break;
            case 37:    // <--
                if(cursor!=0) cursor--;
                actualizaCursor();
                break;
            case 38:    // <--
                if(cursor>15) cursor-=16;
                actualizaCursor();
                break;
            case 39:    // -->
                if(cursor!=(buffer.length-1)) cursor++;
                actualizaCursor();
                break;
            case 40:    // -->
                if(cursor<(buffer.length-16)) cursor+=16;
                actualizaCursor();
                break;
        }
    }

    private class Columnas extends JPanel
    {
        public Columnas()
        {
            this.setLayout(new BorderLayout(1,1));
        }
        public Dimension getPreferredSize()
        {
            return getMinimumSize();
        }

        public Dimension getMinimumSize()
        {
            Dimension d=new Dimension();
            FontMetrics fn=getFontMetrics(font);
            int h=fn.getHeight();
            int nl=1;
            d.setSize(((fn.stringWidth(" ")+1)*+((16*3)-1))+(border*2)+1,h*nl+(border*2)+1);
            return d;
        }

        public void paint(Graphics g)
        {
            Dimension d=getMinimumSize();
            g.setColor(Color.white);
            g.fillRect(0,0,d.width,d.height);
            g.setColor(Color.black);
            g.setFont(font);

            for(int n=0;n<16;n++)
            {
                if(n==(cursor%16)) cuadro(g,n*3,0,2);
                String s="";
                if (bMacHex)
                	s="00"+Integer.toHexString(n);
                else
                	s="00"+n;
                s=s.substring(s.length()-2);
                printString(g,s,n*3,0);
            }
        }
    }

    private class Caja extends JPanel implements MouseListener
    {
    	public Caja()
    	{
    		this.addMouseListener(this);
    	}
        public Dimension getPreferredSize()
        {
            return getMinimumSize();
        }

        public Dimension getMinimumSize()
        {
            Dimension d=new Dimension();
            FontMetrics fn=getFontMetrics(font);
            int h=fn.getHeight();
            d.setSize((fn.stringWidth(" ")+1)+(border*2)+1,h+(border*2)+1);
            return d;
        }

		@Override
		protected void paintComponent(Graphics g) {
			FontMetrics fn=getFontMetrics(font);
			int x= (this.getSize().width - fn.stringWidth("H")) /2;
			int y = (this.getSize().height-fn.getHeight() ) /2+fn.getHeight()-fn.getMaxDescent();
			g.setColor(Color.gray);
			if (bMacHex)
				g.drawString("H", x, y);
			else
				g.drawString("D", x, y);
		}

		public void mouseClicked(MouseEvent e) {
			bMacHex= !bMacHex;
			JHexEditor.this.repaint();
		}

		public void mouseEntered(MouseEvent e) {
			
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
			
		}
    }

    private class Filas extends JPanel
    {
        public Filas()
        {
            this.setLayout(new BorderLayout(1,1));
        }
        public Dimension getPreferredSize()
        {
        	 Dimension d=new Dimension();
             FontMetrics fn=getFontMetrics(font);
             int h=fn.getHeight();
             int nl=getLineas();
             d.setSize((fn.stringWidth(" ")+1)*(8)+(border*2)+1,h*nl+(border*2)+1);
             return d;
        }

        public Dimension getMinimumSize()
        {
            Dimension d=new Dimension();
            FontMetrics fn=getFontMetrics(font);
            int h=fn.getHeight();
            int nl=1;
            d.setSize((fn.stringWidth(" ")+1)*(8)+(border*2)+1,h*nl+(border*2)+1);
            return d;
        }

        public void paint(Graphics g)
        {
            Dimension d=getMinimumSize();
            g.setColor(Color.white);
            g.fillRect(0,0,d.width,d.height);
            g.setColor(Color.black);
            g.setFont(font);

            int ini=getInicio();
            int fin=ini+getLineas();
            int y=0;
            for(int n=ini;n<fin;n++)
            {
                if(n==(cursor/16)) cuadro(g,0,y,8);
                String s="";
                if (bMacHex)
                	s="0000000000000"+Integer.toHexString(n)+"H";
                else
                	s="0000000000000"+n;
               
                s=s.substring(s.length()-8);
               
                printString(g,s,0,y++);
            }
        }
    }

	public byte[] getBuffer() {
		if (bHex)
			return buffer;
		else
			return textArea.getText().getBytes();
	}

	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
		//if (bHex)
                    sb.setMaximum((buffer.length+15)/16);
                    try{
                        textArea.setText(new String(buffer,encoding));
                    }
                    catch(Exception e)
                    {
                        textArea.setText(encoding+"编码不支持");
                    }
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		textArea.setEditable(editable);
	}

}
