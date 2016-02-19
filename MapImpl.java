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

import java.util.*;

public class MapImpl implements Map
{
    
    private Set<Place> myplaces =  new HashSet<Place>();
    private Set<Road> myroads =  new HashSet<Road>();
    private PlaceImpl startPl = null;
    private PlaceImpl endPl = null;
    private ArrayList<MapListener> listeners;
    
    
    public MapImpl(){
    this.listeners = new ArrayList<MapListener>();
    
    }
    //Add the MapListener ml to this map.
    //Note: A map can have multiple listeners
    @Override
    public void addListener(MapListener ml){
    this.listeners.add(ml);
    }


    //Delete the MapListener ml from this map.
    @Override
    public void deleteListener(MapListener ml){
    this.listeners.remove(ml);
    }


    //Create a new Place and add it to this map
    //Return the new place
    //Throws IllegalArgumentException if:
    //  the name is not valid or is the same as that
    //  of an existing place
    //Note: A valid placeName begins with a letter, and is 
    //followed by optional letters, digits, or underscore characters
    @Override
    public Place newPlace(String placeName, int xPos, int yPos) throws IllegalArgumentException {
        
        if (placeName == null) {
            throw new IllegalArgumentException("Place name cannot be null!");
        }
        if (placeName.equals("")) {
            throw new IllegalArgumentException("Place name cannot be empty!");
        }
        
        if (!placeName.matches("^([A-Za-z])(\\w)*$")){
            throw new IllegalArgumentException("Invalid place name: "+ placeName);
        }
        
        if ((xPos<0)||(yPos<0)) {
            throw new IllegalArgumentException("Place: " + placeName + ", place coordinates cannot be negative");
        }
        
        Set<Place> existingplaces = this.getPlaces();
        
        for (Place p : existingplaces){
        
            if (placeName.equals(p.getName())){
            throw new IllegalArgumentException("Place with name \"" + placeName + "\" already exists in the map!");
            }
           
        }
        
        PlaceImpl newplc = new PlaceImpl(placeName, xPos, yPos);
        newplc.setContaining(this);
        this.myplaces.add((Place)newplc);
        this.placesChanged();
        return (Place)newplc;
    }


    //Remove a place from the map
    //If the place does not exist, returns without error
    @Override
    public void deletePlace(Place s){
    
        if (this.myplaces.contains(s)){
            
            Set<Road> toroads = s.toRoads();
            for (Road r : toroads){
              this.deleteRoad(r);
            }
            if (s.isStartPlace()){
            this.setStartPlace(null);
            }
            else if (s.isEndPlace()){
            this.setEndPlace(null);
            }
            
            this.myplaces.remove(s);
            this.placesChanged();
        
        }
    
    }


    //Find and return the Place with the given name
    //If no place exists with given name, return NULL
    @Override
    public Place findPlace(String placeName){
    
        Set<Place> existplc = this.getPlaces();
        
        for (Place p : existplc){
            if (placeName.equals(p.getName())){
            return p;
            }
        }
        
        return null;
    
    }


    //Return a set containing all the places in this map
    @Override
    public Set<Place> getPlaces(){return this.myplaces;}
    

    //Create a new Road and add it to this map
    //Returns the new road.
    //Throws IllegalArgumentException if:
    //  the firstPlace or secondPlace does not exist or
    //  the roadName is invalid or
    //  the length is negative
    //Note: A valid roadName is either the empty string, or starts
    //with a letter and is followed by optional letters and digits
    @Override
    public Road newRoad(Place from, Place to, String roadName, int length) throws IllegalArgumentException{
        if (!roadName.matches("^([A-Za-z])([A-Za-z0-9])*$|^$")){
            throw new IllegalArgumentException("Invalid road name: " + roadName);
        }        
        if (!this.myplaces.contains(from)){
        throw new IllegalArgumentException("From place for road: "+ roadName + " does not exist in the map!");
        }
        if (!this.myplaces.contains(to)){
        throw new IllegalArgumentException("To place for road: "+ roadName + " does not exist in the map");
        }
        if (length < 0){
        throw new IllegalArgumentException("Error in Road: "+ roadName +", Road length cannot be negative");
        }

        for (Road rr : this.getRoads()){
            if (((rr.firstPlace()==from)||rr.secondPlace()==from)&&((rr.firstPlace()==to)||rr.secondPlace()==to)){
            //if (rr.roadName().equals(roadName))
                throw new IllegalArgumentException("A road between " + from.getName() + " and " + to.getName() +" already exists in the map! Road Name: " + rr.roadName());
            }
        }
        
        if (roadName.equals("-")){
        roadName = "";
        }
        
        Road newroad = new RoadImpl(from, to, roadName, length);
        this.myroads.add(newroad);
        this.roadsChanged();
        return newroad;
        
    }


    //Remove a road r from the map
    //If the road does not exist, returns without error
    @Override
    public void deleteRoad(Road r){
    
        if (this.myroads.contains(r)){
         this.myroads.remove(r);
         this.roadsChanged();
        }
    
    }


    //Return a set containing all the roads in this map
    @Override
    public Set<Road> getRoads(){return this.myroads;}
    

    //Set the place p as the starting place
    //If p==null, unsets the starting place
    //Throws IllegalArgumentException if the place p is not in the map
    @Override
    public void setStartPlace(Place p)throws IllegalArgumentException{
    
        if (p==null){
        this.startPl=null;
        this.otherChanged();
        }
        else if (!this.myplaces.contains(p)){
        throw new IllegalArgumentException("Unable to set start place, provided Place is not in the map!");
        }
    //    else if ((this.endPl!=null)&&(this.endPl.getName().equals(p.getName()))){
     //   throw new IllegalArgumentException("Start place is same as the End place!");
      //  }
        else{
        this.startPl=(PlaceImpl)p;
        this.startPl.setContaining(this);
        this.otherChanged();
        }
    }


    //Return the starting place of this map
    @Override
    public Place getStartPlace(){return (Place)this.startPl;}


    //Set the place p as the ending place
    //If p==null, unsets the ending place
    //Throws IllegalArgumentException if the place p is not in the map
    @Override
    public void setEndPlace(Place p) throws IllegalArgumentException{
        if (p==null){
        this.endPl=null;
        this.otherChanged();
        }
        else if (!this.myplaces.contains(p)){
        throw new IllegalArgumentException("Unable to set end place, provided Place is not in the map!");
        }
        else{
        this.endPl=(PlaceImpl)p;
        this.endPl.setContaining(this);
        this.otherChanged();
        }
    }


    //Return the ending place of this map
    @Override
    public Place getEndPlace(){return (Place)this.endPl;}


    //Causes the map to compute the shortest trip between the
    //"start" and "end" places
    //For each road on the shortest route, sets the "isChosen" property
    //to "true".
    //Returns the total distance of the trip.
    //Returns -1, if there is no route from start to end
    @Override
    public int getTripDistance(){
    
        //reset chosen roads
        for (Road rr : this.getRoads()){
            RoadImpl r = (RoadImpl)rr;
            r.setChosen(false);
        }
        
        Dijkstra dk = new Dijkstra(this.getPlaces(),this.getRoads());
        
        int distance = dk.computePaths(this.getStartPlace(),this.getEndPlace());
        
        if (distance == Integer.MAX_VALUE){
        return -1;
        }
        
        List<Place> path = dk.getShortestPathTo(this.getEndPlace());
        //System.out.println("Path: " + path);
        
        for (int i = 0; i < path.size(); i++){
            Place pl = path.get(i);
            
            if (i+1<path.size()){
            RoadImpl rd = (RoadImpl)pl.roadTo(path.get(i+1));
            if (rd != null){
            rd.setChosen(true);
            }
            }
        }

        return distance;
        
    }


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
    @Override
    public String toString(){
    
        String map="";
         
        Set<Place> existplc = this.getPlaces();
        
        for (Place p : existplc){
          map=map+"PLACE "+p.toString()+"\n";
        }
        
        Set<Road> existroad = this.getRoads();
        
        for (Road r : existroad){
          map=map+"ROAD "+r.toString()+"\n";
        }
        
        if (this.getStartPlace()!=null){
        map=map+"START "+ this.getStartPlace().getName() + "\n";
        }
        
        if (this.getEndPlace()!=null){
        map=map+"END " + this.getEndPlace().getName() + "\n";
        }
        
        
        return map;
        
    
    }
    
    private void placesChanged()
    {
	for( MapListener fl: listeners ){
	    fl.placesChanged();
	}
    }
    
    private void roadsChanged()
    {
	for( MapListener fl: listeners ){
	    fl.roadsChanged();
	}
    }
    
    private void otherChanged()
    {
	for( MapListener fl: listeners ){
	    fl.otherChanged();
	}
    }

}
