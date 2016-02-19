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

import java.util.ArrayList;

public class RoadImpl implements Road
{
    
    private Place from,to;
    private String roadname;
    private int length;
    private boolean chosen;
    private ArrayList<RoadListener> listeners;
    
    public RoadImpl(Place frpl, Place topl, String rn, int ln){
    
    this.from=frpl;
    this.to=topl;
    this.roadname=rn;
    this.length=ln;
    this.chosen=false;
    this.listeners = new ArrayList<RoadListener>();
    
    }
    
    //Add the RoadListener rl to this place.
    //Note: A road can have multiple listeners
    @Override
    public void addListener(RoadListener rl){
    this.listeners.add(rl);
    }


    //Delete the RoadListener rl from this place.
    @Override
    public void deleteListener(RoadListener rl){
    this.listeners.remove(rl);
    }


    //Return the first place of this road
    //Note: The first place of a road is the place whose name
    //comes EARLIER in the alphabet.
    @Override
    public Place firstPlace(){
    
        String s1 = this.from.getName();
        String s2 = this.to.toString();
        int result = s1.compareTo(s2);
        
        if (result<=0){
        return this.from;
        }
        
        else {
        return this.to;
        }
        
    }
    

    //Return the second place of this road
    //Note: The second place of a road is the place whose name
    //comes LATER in the alphabet.
    @Override
    public Place secondPlace(){
    
        String s1 = this.from.getName();
        String s2 = this.to.toString();
        int result = s1.compareTo(s2);
        
        if (result<=0){
        return this.to;
        }
        
        else {
        return this.from;
        }
        
    }
    

    //Return true if this road is chosen as part of the current trip
    @Override
    public boolean isChosen(){return this.chosen;}
    
    public void setChosen(boolean b){
        this.chosen=b; 
        this.roadChanged();
    }
    


    //Return the name of this road
    @Override
    public String roadName(){return this.roadname;}
    

    //Return the length of this road
    @Override
    public int length(){return this.length;}

    
    //Return a string containing information about this road 
    //in the form (without quotes, of course!):
    //"firstPlace(roadName:length)secondPlace"
    @Override
    public String toString(){return this.firstPlace().getName()+"("+this.roadname+":"+this.length+")"+this.secondPlace().getName();}
    
    public void roadChanged()
    {
	for( RoadListener rl: listeners ){
	    rl.roadChanged();
	}
    }
}
