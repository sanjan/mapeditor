/*
*
* Author: Grero, Kondagamage Sanjan Chamara
* 
* Student ID: A1204014
*
* Assignment: Map Editor
*
* Subject: Event Driven Computing 
*
* Class: Singapore - Trimester 2, 2013
*
*/

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;

public class PlaceIcon extends JComponent
    implements PlaceListener, MouseListener, MouseMotionListener
{
    private static final int R=32;

    private Place p;
    private int x0;
    private int y0;
    private boolean isSelected;
    int prw,prh,px,py;
    private MapPanel mp;


    public PlaceIcon(Place p, MapPanel mpa)
    {
	this.mp=mpa;
        this.p= p;
        this.px=p.getX();
        this.py=p.getY();
	isSelected= false;
	this.addMouseMotionListener(this);
	this.addMouseListener(this);
	this.setBounds(p.getX(),p.getY(),R+1,R+1);
	this.setVisible(true);
        this.setToolTipText(p.getName());
	repaint();
    }

    public void setSelected(boolean b)
    {
	if( isSelected==b ){
	    return;
	}

	isSelected= b;
	repaint();
    }

    public boolean isSelected()
    {
	return isSelected;
    }


    @Override
    protected void paintComponent(Graphics gg)
    {
  	super.paintComponent(gg);
	Graphics2D g= (Graphics2D)gg;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //g.clipRect(0, 0, R+1, R+1);
        g.setFont(new Font("default", Font.PLAIN, 10));
        
        if ((p.isStartPlace())&&(p.isEndPlace())){
            if (isSelected){
             g.setColor(new Color(109,207,246));
            }else{
            g.setColor(new Color(255,247,153));
            }
            g.fillRect(0, 0, R, R);
            g.setColor(Color.black);
            g.drawString("S&E", 1, 30);
        }
        else if(p.isStartPlace()){
            if (isSelected){
             g.setColor(new Color(109,207,246));
            }else{
            g.setColor(new Color(240,110,170));
            }
            g.fillRect(0, 0, R, R);
            g.setColor(Color.black);
            g.drawString("start", 1, 30);
        }
        else if(p.isEndPlace()){
            if (isSelected){
             g.setColor(new Color(109,207,246));
            }else{
            g.setColor(new Color(242,108,79));
            }
	    g.fillRect(0, 0, R, R);
            g.setColor(Color.black);            
            g.drawString("end", 1, 30);
        }
        else if ( isSelected ){
	    g.setColor(new Color(109,207,246));
	    g.fillRect(0, 0, R, R);
	}

	g.setColor(new Color(117,76,36));
	g.drawRect(0, 0, R, R);

        
        g.drawString(this.p.getName(), 1, 10); 

       
    }

    //Placelistener
    @Override
    public void placeChanged()
    {
        //It appears I've moved...
        if ((this.px!=p.getX())||(this.py!=p.getY())){
         mp.setSave(true);
	}
        this.setLocation(p.getX(),p.getY());
        repaint();
        
    }

    //MouseListener
    @Override
    public void mouseEntered(MouseEvent me)
    {
       // this.setToolTipText(this.p.getName());                   
    }
    @Override
    public void mouseExited(MouseEvent me)
    {
    }
    @Override
    public void mousePressed(MouseEvent me)
    {
        
	//if((me.getX()>=this.p.getX())&&(me.getY()>=this.p.getY())&&(me.getX()<=this.p.getX()+R)&&(me.getY()<=this.p.getY()+R)){
        x0= me.getX();
	y0= me.getY();
      //  }
        
    }
    @Override
    public void mouseReleased(MouseEvent me)
    {

    }
    @Override
    public void mouseClicked(MouseEvent me)
    {
        mp.deselectRoads(null);
        
        if (mp.getNRMode()==false){
        mp.deselectPlaces(this);
        }
        
	isSelected= !isSelected;
        repaint();
        mp.nrCheck(false);
        
    }

    //MouseMotionListener
    @Override
    public void mouseDragged(MouseEvent me)
    {
     
	int x= me.getX();
	int y= me.getY();
        if (this.isSelected){
        //System.out.println("src x:"+ x0+"src y:"+y0+ "drag x:"+ x+"drag y:"+y + "displacement x:"+ (x-x0) +"displacement y:"+(y-y0));
        for (Place pp : mp.getSelPlaces()){
        //prw=mp.getEditor().getMaximumSize().width-50;
        //prh=mp.getEditor().getMaximumSize().height-160;
        //int cx = x-x0+pp.getX();
        //int cy = y-y0+pp.getY();
        
        //if ((cx<prw)&&(cy<prh)&&(cx>0)&&(cy>0)){    
           pp.moveBy(x-x0,y-y0);
        //}
           
        }
      }
       else {             
            prw=mp.getEditor().getMaximumSize().width-50;
            prh=mp.getEditor().getMaximumSize().height-160;
            int cx = x-x0+p.getX();
            int cy = y-y0+p.getY();

            if ((cx<prw)&&(cy<prh)&&(cx>0)&&(cy>0)){    
               p.moveBy(x-x0,y-y0);
            }
         }
    }

    @Override
    public void mouseMoved(MouseEvent me)
    {
     this.setToolTipText(this.p.getName());
    }

}
