
import java.util.Set;

public interface Place
{
    //Add the PlaceListener pl to this place. 
    //Note: A place can have multiple listeners
    public void addListener(PlaceListener pl);


    //Delete the PlaceListener pl from this place.
    public void deleteListener(PlaceListener pl);


    //Return a set containing all roads that reach this place
    public Set<Road> toRoads();


    //Return the road from this place to dest, if it exists
    //Returns null, if it does not
    public Road roadTo(Place dest);
    

    //Move the position of this place 
    //by (dx,dy) from its current position
    public void moveBy(int dx, int dy);
    

    //Return the name of this place 
    public String getName();
    

    //Return the X position of this place
    public int getX();
    

    //Return the Y position of this place
    public int getY();


    //Return true if this place is the starting place for a trip
    public boolean isStartPlace();


    //Return true if this place is the ending place for a trip
    public boolean isEndPlace();


    //Return a string containing information about this place 
    //in the form (without the quotes, of course!) :
    //"placeName(xPos,yPos)"  
    public String toString();
}
