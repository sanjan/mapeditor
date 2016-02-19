
import java.util.Set;

public interface Map
{
    //Add the MapListener ml to this map.
    //Note: A map can have multiple listeners
    public void addListener(MapListener ml);


    //Delete the MapListener ml from this map.
    public void deleteListener(MapListener ml);


    //Create a new Place and add it to this map
    //Return the new place
    //Throws IllegalArgumentException if:
    //  the name is not valid or is the same as that
    //  of an existing place
    //Note: A valid placeName begins with a letter, and is 
    //followed by optional letters, digits, or underscore characters
    public Place newPlace(String placeName, int xPos, int yPos)
      throws IllegalArgumentException;


    //Remove a place from the map
    //If the place does not exist, returns without error
    public void deletePlace(Place s);


    //Find and return the Place with the given name
    //If no place exists with given name, return NULL
    public Place findPlace(String placeName);


    //Return a set containing all the places in this map
    public Set<Place> getPlaces();
    

    //Create a new Road and add it to this map
    //Returns the new road.
    //Throws IllegalArgumentException if:
    //  the firstPlace or secondPlace does not exist or
    //  the roadName is invalid or
    //  the length is negative
    //Note: A valid roadName is either the empty string, or starts
    //with a letter and is followed by optional letters and digits
    public Road newRoad(Place from, Place to, 
      String roadName, int length) 
      throws IllegalArgumentException;


    //Remove a road r from the map
    //If the road does not exist, returns without error
    public void deleteRoad(Road r);


    //Return a set containing all the roads in this map
    public Set<Road> getRoads();
    

    //Set the place p as the starting place
    //If p==null, unsets the starting place
    //Throws IllegalArgumentException if the place p is not in the map
    public void setStartPlace(Place p)
      throws IllegalArgumentException;


    //Return the starting place of this map
    public Place getStartPlace();


    //Set the place p as the ending place
    //If p==null, unsets the ending place
    //Throws IllegalArgumentException if the place p is not in the map
    public void setEndPlace(Place p)
      throws IllegalArgumentException;


    //Return the ending place of this map
    public Place getEndPlace();


    //Causes the map to compute the shortest trip between the
    //"start" and "end" places
    //For each road on the shortest route, sets the "isChosen" property
    //to "true".
    //Returns the total distance of the trip.
    //Returns -1, if there is no route from start to end
    public int getTripDistance();


    //Return a string describing this map
    //Returns a string that contains (in this order):
    //for each place in the map, a line (terminated by \n)
    //  PLACE followed the toString result for that place
    //for each road in the map, a line (terminated by \n)
    //  ROAD followed the toString result for that road
    //if a starting place has been defined, a line containing
    //  START followed the name of the starting-place (terminated by \n)
    //if an ending place has been defined, a line containing
    //  END followed the name of the ending-place (terminated by \n)
    public String toString();
}
