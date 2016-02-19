
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/*
*
* Author: Grero, Kondagamage Sanjan Chamara
* 
* Student ID: A1204014
*
* Assignment: Map Database
*
* Subject: Event Driven Computing 
*
* Class: Singapore - Trimester 2, 2013
*
*/



public class PlaceImpl implements Place
{
    private Map containingMap;
    private String name;
    private int xpos, ypos;
    private Place previous=null;
    private ArrayList<PlaceListener> listeners;
    
   public PlaceImpl(String n, int x, int y){
        this.name=n;
        this.xpos=x;
        this.ypos=y;
        this.listeners = new ArrayList<PlaceListener>();
    }
    //Add the PlaceListener pl to this place. 
    //Note: A place can have multiple listeners
    @Override
    public void addListener(PlaceListener pl){
    this.listeners.add(pl);
    }


    //Delete the PlaceListener pl from this place.
    @Override
    public void deleteListener(PlaceListener pl){
    this.listeners.remove(pl);
    }


    //Return a set containing all roads that reach this place
    @Override
    public Set<Road> toRoads(){
    
        Set<Road> existingroads = this.containingMap.getRoads();
        Set<Road> toroads = new HashSet<Road>();
         for (Road r : existingroads){
            if ((r.secondPlace().getName().equals(this.name))||(r.firstPlace().getName().equals(this.name))){
            toroads.add(r);
            } 
         }
         
         return toroads;
    
    }


    //Return the road from this place to dest, if it exists
    //Returns null, if it does not
    @Override
    public Road roadTo(Place dest){
    
        Set<Road> existingroads = this.containingMap.getRoads();
        Road roadto = null;
         for (Road r : existingroads){
        
            if ((r.firstPlace().getName().equals(this.name))&&(r.secondPlace().getName().equals(dest.getName()))){
            roadto =r;
            }
            else if((r.secondPlace().getName().equals(this.name))&&(r.firstPlace().getName().equals(dest.getName()))){
            roadto = r;
            }
           
        }
         
         return roadto;
    
    }
    

    //Move the position of this place 
    //by (dx,dy) from its current position
    @Override
    public void moveBy(int dx, int dy){
        this.xpos=this.xpos+dx;
        this.ypos=this.ypos+dy;
        this.placeChanged();
    }
    

    //Return the name of this place 
    @Override
    public String getName(){return this.name;}
    

    //Return the X position of this place
    @Override
    public int getX(){return this.xpos;}
    

    //Return the Y position of this place
    @Override
    public int getY(){return this.ypos;}


    //Return true if this place is the starting place for a trip
    @Override
    public boolean isStartPlace(){
        return this.equals(this.containingMap.getStartPlace());
    }


    //Return true if this place is the ending place for a trip
    @Override
    public boolean isEndPlace(){
        return this.equals(this.containingMap.getEndPlace());
    }


    //Return a string containing information about this place 
    //in the form (without the quotes, of course!) :
    //"placeName(xPos,yPos)"  
    @Override
    public String toString(){return this.name+"("+this.xpos+","+this.ypos+")";}
    
    public void setContaining(Map m){
        this.containingMap=m;
    }
    
    public void setPrevious(Place p){
    this.previous=p;
    }
    
    public Place getPrevious(){
    return this.previous;
    }
    
    private void placeChanged()
    {
	for( PlaceListener pl: listeners ){
	    pl.placeChanged();
	}
        
        for (Road r : this.toRoads()){
        RoadImpl rr = (RoadImpl)r;
        rr.roadChanged();
       }
    }
    
}
