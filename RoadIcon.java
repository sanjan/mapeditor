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
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Stroke;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.Line2D;


public class RoadIcon extends JComponent implements RoadListener
{
    private static final int R=16;
    private Road road;
    private int x1,y1,x2,y2;
    private boolean isSelected;
    private boolean isChosen;
    private Line2D.Double line = new Line2D.Double();

  

    public RoadIcon(Road r)
    {
	this.road= r;
        this.getDimensions();
	this.setVisible(true);
        
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
    
    public void setChosen(boolean b)
    {
	if( isChosen==b ){
	    return;
	}

	isChosen= b;
	repaint();
    }
    
    public boolean isChosen()
    {
	return isChosen;
    }
    


    @Override
    protected void paintComponent(Graphics gg)
    {
        
        
        super.paintComponent(gg);
	Graphics2D g= (Graphics2D)gg;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
       
        //g.setStroke(new BasicStroke(5));
        //g.drawRect(re2.x, re2.y, re2.width, re2.height);
        //g.setColor(Color.GREEN);
        //g.fillRect(re.x, re.y, re.width, re.height);
        // g.setColor(Color.GREEN);
        //g.drawRect(x1, y1, x2, y2);
        //g.setColor(Color.MAGENTA);
        //g.fillOval(x1 -5, y1-5, 10, 10);
	Font f = new Font("default", Font.BOLD, 11);
        
        if( isSelected ){
	g.setColor(new Color(0,191,243));
        Stroke stroke = new BasicStroke(2);
        g.setStroke(stroke);
	}
        else if (isChosen){
	g.setColor(new Color(57,181,74));
        Stroke stroke = new BasicStroke(2);
        g.setStroke(stroke);
	}
        else{
	g.setColor(new Color(115,99,87));
        f = new Font("default", Font.PLAIN, 11);
        }
        
        line.setLine(x1, y1, x2, y2);
        g.draw(line);

        int centerX =x1 + ((x2-x1)/2);
        int centerY =y1 + ((y2-y1)/2);

        double deg = Math.toDegrees(Math.atan2(centerY - y2, centerX - x2)+ Math.PI);
   
        if ((deg>90)&&(deg<270)){
            deg += 180;
        }
        
        double angle = Math.toRadians(deg);

        String roadtext = road.roadName() + " (" + road.length() + ")";
        
        FontMetrics fm = g.getFontMetrics(f);
        int sw =  fm.stringWidth(roadtext);
    
        g.setFont(f);
        g.rotate(angle, centerX, centerY);
        g.drawString(roadtext, centerX - (sw/2), centerY - 10); 
        g.rotate(-angle, centerX, centerY);
      
        //g.dispose();
    
     }


    //Roadlistener
    @Override
    public void roadChanged()
    {
       
        if (road.isChosen()){
        this.setChosen(true);
        }
        this.getDimensions();
	repaint();
    }
    
    private void getDimensions(){
        
            int ox=road.firstPlace().getX()+R;
            int oy=road.firstPlace().getY()+R;
            int ex=road.secondPlace().getX()+R;
            int ey=road.secondPlace().getY()+R;
            
            double rw=Math.abs(ex-ox)/R;
            double rh=Math.abs(ey-oy)/R; 
            double s = Math.max(rw, rh);
    
            x1= (int)(ox+ (ex-ox)/s);
            y1= (int)(oy +(ey-oy)/s);
            x2=(int)(ex- (ex-ox)/s); //dest
            y2=(int)(ey- (ey-oy)/s); //dest
            
            /*if ((x2>=x1)&&(y2>=y1)){

            re.setRect(x1, y1, x2-x1, y2-y1);
            }
            else if((x2>=x1)&&(y1>=y2)) {

            re.setRect(x1, y2, x2-x1, y1-y2);
            }
            else if((x1>x2)&&(y1>y2)) {

            re.setRect(x2, y2, x1-x2, y1-y2);
            }
            else if((x1>x2)&&(y2>y1)) {

            re.setRect(x2, y1, x1-x2, y2-y1);
            }*/
            
            this.setBounds(0,0,Toolkit.getDefaultToolkit().getScreenSize().width,Toolkit.getDefaultToolkit().getScreenSize().height);
            //System.out.println(this.road.roadName() + ": Bounds, size (x:" + me.getWidth() + ",y:" + me.getHeight() +")");
            
    }
   
   public Line2D getLine(){
   return line;
   }
   
   public String name(){return this.road.roadName();}
  
    
}
