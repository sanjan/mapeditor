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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;

public class MapPanel extends JPanel implements MouseListener, 
        MouseMotionListener, MapListener {
    
    private MapEditor containingEditor;
    private Map containingMap;
    private HashMap<Place,PlaceIcon> places;
    private HashMap<Road,RoadIcon> roads;
    private Rectangle rect;
    private Rectangle2D rire;
    private String nrname = "";
    private String nrlength ="";
    private boolean dragmode=false;
    private boolean nrmode = false;
    private int dx,x1,x0,dy,y1,y0;

     
    private boolean savable = false;
    
    public MapPanel(Map m, MapEditor e){
        //Disable default layout manager
	super(null);
        this.containingMap=m;
        this.containingEditor=e;
        
	places= new HashMap<Place,PlaceIcon>();
        roads = new HashMap<Road,RoadIcon>();
        rect= new Rectangle(0,0,0,0);
        rire=(Rectangle2D)rect;
	this.addMouseListener(this);
	this.addMouseMotionListener(this);
        this.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
        this.setVisible(true);
        this.setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width-32,Toolkit.getDefaultToolkit().getScreenSize().height-150));
        repaint();
    }
    
    //Called whenever the number of places in the map has changed
    @Override
    public void placesChanged(){
                    
        Set<Place> actualPlaces= containingMap.getPlaces();
        
	//Add new places
	for( Place p: actualPlaces ){
	   if( !places.containsKey(p) ){
		
		PlaceIcon pi= new PlaceIcon(p,this);
		p.addListener(pi);
		this.places.put(p,pi);
		this.add(pi);
	   }
	}
        
         Iterator iter = places.keySet().iterator();

         while(iter.hasNext()) {
            Place p = (Place)iter.next();
            if (!actualPlaces.contains(p)){
               PlaceIcon pi = places.get(p);
                pi.setVisible(false);
                p.deleteListener(pi);
                iter.remove();
            }
          }

        repaint();
        
                
        if(this.containingMap.getPlaces().size()>1){
        this.containingEditor.enableNewRoad(true);
        }
        else{
        this.containingEditor.enableNewRoad(false);
        }
        
        if(this.containingMap.getPlaces().isEmpty()){
        this.containingEditor.enableEditMenuItems(false);
        }
        else{
        this.containingEditor.enableEditMenuItems(true);
        }
    }

    //Called whenever the number of roads in the map has changed
    @Override
    public void roadsChanged(){
       
        Set<Road> actualRoads = containingMap.getRoads();
        
         Iterator iter = roads.keySet().iterator();

         while(iter.hasNext()) {
            Road r = (Road)iter.next();
            if (!actualRoads.contains(r)){
            RoadIcon ri = roads.get(r);
            ri.setVisible(false);
            r.deleteListener(ri);
            iter.remove();
            }
          }
         
        for (Road r : actualRoads){
            if( !roads.containsKey(r) ){
		
		RoadIcon ri= new RoadIcon(r);
                if(r.isChosen()){
                 ri.setChosen(true);
                }
		r.addListener(ri);
		roads.put(r,ri);
		this.add(ri);
	    }
            else {
               RoadIcon ri = roads.get(r);
               if(r.isChosen()){
                    ri.setChosen(true);
               }               
               else{
                   ri.setChosen(false);
               }
            }        
        }
        
        iter = roads.keySet().iterator();
        while(iter.hasNext()) {
        roads.get((Road)iter.next()).roadChanged();
        }
        
        
        repaint();

    }

    //Called whenever something about the map has changed
    //(other than places and roads)
    @Override
    public void otherChanged(){
        
     int distance = this.containingMap.getTripDistance();
     containingEditor.setTriptext("");

    if ((!this.containingMap.getRoads().isEmpty())&&
             (this.containingMap.getStartPlace()!=null)&&(this.containingMap.getEndPlace()!=null)){
                
                if (distance < 0){
                containingEditor.setTriptext("No Route");
                containingEditor.setTiptext("");
                }
                else if (distance > 0){
                    containingEditor.setTriptext("Trip Distance: "+ distance);

                    containingEditor.setTiptext("Follow the path highlighted in Green to reach " 
                            + this.containingMap.getEndPlace().getName() 
                            + ", from " + this.containingMap.getStartPlace().getName());
                   }
                else {
                containingEditor.setTriptext("Trip Distance: "+ distance + "");
                containingEditor.setTiptext("");
                }
     }
     else if ((!this.containingMap.getRoads().isEmpty())&&
             ((this.containingMap.getStartPlace()!=null)||(this.containingMap.getEndPlace()!=null))){
         containingEditor.setTriptext("No Route");
        containingEditor.setTiptext("set start & end places to find shortest path between the two");
     }
     else{
     containingEditor.setTiptext("");
     }

    this.roadsChanged();
    this.deselect();
    
    }
    
    @Override
    protected void paintComponent(Graphics gg){
	super.paintComponent(gg);
	Graphics2D g= (Graphics2D)gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	g.setColor(Color.MAGENTA);
        g.drawRect(rect.x,rect.y,rect.width,rect.height);
        
        if (dragmode){
        g.setColor(Color.ORANGE);
        Stroke stroke = new BasicStroke(2);
        g.setStroke(stroke);
        g.drawLine(dx, dy, x1, y1);
        g.setStroke(new BasicStroke(1));
       
        }
        
    }
        
    //MouseListener
    @Override
    public void mouseEntered(MouseEvent me){
    }
    @Override
    public void mouseExited(MouseEvent me){
    }
    
    @Override
    public void mousePressed(MouseEvent me){
	x0= me.getX();
	y0= me.getY();
	rect= new Rectangle(x0,y0,0,0);
        rire = (Rectangle2D)(new Rectangle((x0-3),(y0-3),6,6));
    }
    
    @Override
    public void mouseReleased(MouseEvent me){
	rect= new Rectangle(0,0,0,0);
        
	repaint();
    }

    @Override
    public void mouseClicked(MouseEvent me){
        this.nrCheck(true);
    }

    //MouseMotionListener
    @Override
    public void mouseDragged(MouseEvent me){
	if (!nrmode){
        
        int x= me.getX();
	int y= me.getY();

	int rx= Math.min(x0,x);
	int ry= Math.min(y0,y);
	int rw= Math.abs(x-x0);
	int rh= Math.abs(y-y0);
	rect= new Rectangle(rx,ry,rw,rh);

	//See if we have encircled anyone...
	for( Component c: this.getComponents() ){
     
	    try{
        
                if (c.getClass().getName().equals("PlaceIcon")) {
                    
                        PlaceIcon pi= (PlaceIcon)c;
                        boolean isHit= rect.intersects(pi.getBounds());
                        pi.setSelected(isHit);
                }
                else if (c.getClass().getName().equals("RoadIcon")) {
                    
                        Rectangle2D re2d = (Rectangle2D)rect;
                        RoadIcon ri = (RoadIcon)c;
                        if(re2d.intersectsLine(ri.getLine())){
                        ri.setSelected(true);
                        }
                        else {
                        ri.setSelected(false);
                        }
                }
                 
                
            }
            catch(Exception e){
              //  System.out.println(e);
            }
	}
        
        repaint();
        
        }
        
    }

    @Override
    public void mouseMoved(MouseEvent me){
        if (dragmode){
         x1 = me.getX();
         y1 = me.getY();
         repaint();
        }
    }
    
    //return selected
    public Set<Place> getSelPlaces(){
    
        Set<Place> myplaces =  new HashSet<Place>();
        Iterator iter = places.keySet().iterator();

        while(iter.hasNext()) {
            Place key = (Place)iter.next();
            PlaceIcon val = (PlaceIcon)places.get(key);

            if (val.isSelected()){
            myplaces.add(key);
            }
        }
        return myplaces;
    }
    
   public Set<Road> getSelRoads(){
    
        Set<Road> myroads =  new HashSet<Road>();
        Iterator iter = roads.keySet().iterator();

        while(iter.hasNext()) {
            Road key = (Road)iter.next();
            RoadIcon val = (RoadIcon)roads.get(key);

            if (val.isSelected()){
            myroads.add(key);
            }
        }
        return myroads;
    }
    public Set<RoadIcon> getRoadIcons(){
    
        Set<RoadIcon> myri =  new HashSet<RoadIcon>();
        Iterator iter = roads.keySet().iterator();

        while(iter.hasNext()) {
            Road key = (Road)iter.next();
            
            myri.add(roads.get(key));
            
        }
        return myri;
    }
    
    
    
    public void setNRMode(String r, String l){
        containingEditor.setTiptext("Click on a place icon to select origin");
        this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        this.nrmode=true;
        this.nrname=r;
        this.nrlength=l;
        this.deselect();
        repaint();
    }
    
    private void createNR(){

    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));   
    Set<Place> newPlaces = this.getSelPlaces();
    Place[] parray = newPlaces.toArray(new Place[0]);
    
        try{

        if (!nrlength.matches("^\\d+$"))
        throw new IllegalArgumentException("Invalid road length value: "+ nrlength);
                            
        containingMap.newRoad(parray[0], parray[1], nrname, Integer.parseInt(nrlength));
        
        this.otherChanged();
        this.setSave(true);

        }
        catch(Exception ex){
        int mc = JOptionPane.ERROR_MESSAGE;
        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", mc);
        }
        
        deselect();

	this.nrlength="";
        this.nrname="";
        this.nrmode=false; 
        this.dragmode=false;
        this.dx=0;
        this.dy=0;
        
    }
    
    public void nrCheck(boolean b){

        if (this.nrmode){
            
        if(this.getSelPlaces().isEmpty()){
        this.dragmode=false;
        this.dx=0;
        this.dy=0;
        containingEditor.setTiptext("Click on a place icon to select origin");
        }
        
        else if(this.getSelPlaces().size()==1){
        this.dragmode=true;
        this.dx=this.getMousePosition().x;
        this.dy=this.getMousePosition().y;
        this.x1=this.dx;
        this.y1=this.dy;
        containingEditor.setTiptext("Click on a place icon to select destination");
        }
        
        else if (this.getSelPlaces().size()==2){
            this.createNR();
        }
        
        }
        //not nr mode
        else{

            boolean outside=b;
            if (b==false){
            rire = (Rectangle2D)(new Rectangle((0),(0),0,0));
            }

            for( Component c: this.getComponents() ){
            if (c.getClass().getName().equals("RoadIcon")) {       
                RoadIcon ri = (RoadIcon)c;
                if (ri.getLine().intersects(rire)){
                    this.deselectRoads(ri);
                    this.deselectPlaces(null);
                    ri.setSelected(!ri.isSelected());
                    outside=false;
                }
            }

            }
            if (outside){
            this.deselect();
            }
        }
        
        repaint();
    
    }
    
    public void setSave(boolean b){this.savable=b;}
    public boolean getSave(){return this.savable;}
    
    public void deselect(){
            for( Component c: this.getComponents() ){
            try {
                if (c.getClass().getName().equals("PlaceIcon")) {          
                        PlaceIcon pi= (PlaceIcon)c;
                        pi.setSelected(false);
                }
                else if (c.getClass().getName().equals("RoadIcon")){
                        RoadIcon ri = (RoadIcon)c;
                        ri.setSelected(false);
                }
            }
            catch(Exception e){
                //System.out.println(e);}
            }
        }
    }
    
        public void deselectPlaces(PlaceIcon pime){
            for( Component c: this.getComponents() ){
            try {
                if (c.getClass().getName().equals("PlaceIcon")) {          
                        PlaceIcon pi= (PlaceIcon)c;
                        if (!pi.equals(pime))
                        pi.setSelected(false);
                }                
            }
            catch(Exception e){
                //System.out.println(e);}
            }
        }
    }
        
     public void deselectRoads(RoadIcon rime){
            for( Component c: this.getComponents() ){
            try {
                
                if (c.getClass().getName().equals("RoadIcon")){
                        RoadIcon ri = (RoadIcon)c;
                        if(!ri.equals(rime)){
                            ri.setSelected(false);
                        }
                }
            }
            catch(Exception e){
                //System.out.println(e);}
            }
        }

    }
    
    public MapEditor getEditor(){
    return this.containingEditor;
    }
    
    public boolean getNRMode(){
    return this.nrmode;
    }
}
