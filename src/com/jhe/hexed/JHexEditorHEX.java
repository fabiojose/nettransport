package com.jhe.hexed;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by IntelliJ IDEA.
 * User: laullon
 * Date: 09-abr-2003
 * Time: 12:47:32
 */
public class JHexEditorHEX extends JComponent implements MouseListener,KeyListener
{
    private JHexEditor he;
    private int cursor=0;

    public JHexEditorHEX(JHexEditor he)
    {
        this.he=he;
        addMouseListener(this);
        addKeyListener(this);
        addFocusListener(he);
    }

    public Dimension getPreferredSize()
    {
        debug("getPreferredSize()");
        return getMinimumSize();
    }

    public Dimension getMaximumSize()
    {
        debug("getMaximumSize()");
        Dimension d=new Dimension();
        FontMetrics fn=getFontMetrics(he.font);
        int h=fn.getHeight();
        int nl=he.getLineas();
        d.setSize(((fn.stringWidth(" ")+1)*+((16*3)-1))+(he.border*2)+1,h*nl+(he.border*2)+1);
        return d;
    }

    public Dimension getMinimumSize()
    {
        debug("getMinimumSize()");

        Dimension d=new Dimension();
        FontMetrics fn=getFontMetrics(he.font);
        int h=fn.getHeight();
        int nl=1;
        d.setSize(((fn.stringWidth(" ")+1)*+((16*3)-1))+(he.border*2)+1,h*nl+(he.border*2)+1);
        return d;
    }

    public void paint(Graphics g)
    {
        debug("paint("+g+")");
        debug("cursor="+he.cursor+" buff.length="+he.buffer.length);
        Dimension d=getMinimumSize();
        g.setColor(Color.white);
        g.fillRect(0,0,d.width,d.height);
        g.setColor(Color.black);

        g.setFont(he.font);

        int ini=he.getInicio()*16;
        int fin=ini+(he.getLineas()*16);
        if(fin>he.buffer.length) fin=he.buffer.length;

        //datos hex
        int x=0;
        int y=0;
        for(int n=ini;n<fin;n++)
        {
            if(n==he.cursor)
            {
                if(hasFocus())
                {
                    g.setColor(Color.black);
                    he.fondo(g,(x*3),y,2);
                    g.setColor(Color.blue);
                    he.fondo(g,(x*3)+cursor,y,1);
                } else
                {
                    g.setColor(Color.blue);
                    he.cuadro(g,(x*3),y,2);
                }

                if(hasFocus()) g.setColor(Color.white); else g.setColor(Color.black);
            } else
            {
                g.setColor(Color.black);
            }

            String s=("0"+Integer.toHexString(he.buffer[n]));
            s=s.substring(s.length()-2);
            he.printString(g,s,((x++)*3),y);
            if(x==16)
            {
                x=0;
                y++;
            }
        }
    }

    private void debug(String s)
    {
        if(he.DEBUG) System.out.println("JHexEditorHEX ==> "+s);
    }

    // calcular la posicion del raton
    public int calcularPosicionRaton(int x,int y)
    {
        FontMetrics fn=getFontMetrics(he.font);
        x=x/((fn.stringWidth(" ")+1)*3);
        y=y/fn.getHeight();
        debug("x="+x+" ,y="+y);
        return x+((y+he.getInicio())*16);
    }

    // mouselistener
    public void mouseClicked(MouseEvent e)
    {
        debug("mouseClicked("+e+")");
        he.cursor=calcularPosicionRaton(e.getX(),e.getY());
        this.requestFocus();
        he.repaint();
    }

    public void mousePressed(MouseEvent e)
    {
    }

    public void mouseReleased(MouseEvent e)
    {
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }

    //KeyListener
    public void keyTyped(KeyEvent e)
    {
        debug("keyTyped("+e+")");
        if (!he.isEditable())
        	return;

        char c=e.getKeyChar();
        if(((c>='0')&&(c<='9'))||((c>='A')&&(c<='F'))||((c>='a')&&(c<='f')))
        {
            char[] str=new char[2];
            String n="00"+Integer.toHexString((int)he.buffer[he.cursor]);
            if(n.length()>2) n=n.substring(n.length()-2);
            str[1-cursor]=n.charAt(1-cursor);
            str[cursor]=e.getKeyChar();
            he.buffer[he.cursor]=(byte)Integer.parseInt(new String(str),16);

            if(cursor!=1) cursor=1;
            else if(he.cursor!=(he.buffer.length-1)){ he.cursor++; cursor=0;}
            he.actualizaCursor();
        }
    }

    public void keyPressed(KeyEvent e)
    {
        debug("keyPressed("+e+")");
        he.keyPressed(e);
    }

    public void keyReleased(KeyEvent e)
    {
        debug("keyReleased("+e+")");
    }

    public boolean isFocusTraversable()
    {
        return true;
    }
}
