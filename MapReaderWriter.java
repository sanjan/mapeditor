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

import java.io.Reader;
import java.io.IOException;
import java.io.Writer;
import java.io.BufferedReader;
import java.util.Set;

public class MapReaderWriter implements MapIo {
    //This class handles reading and writing map representations as 
    //described in the practical specification

    //Read the description of a map from the 
    //Reader r, and transfers it to Map, m.
    
    public MapReaderWriter(){}
    
    public void read (Reader r, Map m) throws IOException, MapFormatException{
            if (r==null){
             throw new IOException("Reader cannot be null");
            }
            
            if(m==null) {
            throw new IOException("Map cannot be null");
            }

            //try {
            BufferedReader b = new BufferedReader(r);
            String line = b.readLine();
            
            if(line==null) {
            throw new IOException("Empty file");
            }
            
            int linenr=1;
            while (line!=null){
               line = line.trim();
               
               if (line.length() == 0){
               line = b.readLine();
               linenr++;
               continue;
               }
               
               
               if(line.matches("^#.*$")){
               line = b.readLine();
               linenr++;
               continue;
               }
               
               String[] result=line.split("\\s+");
               
               //process a place
               if ((result[0].equals("place"))&& (result.length==4)){
               
                    String plcname = result[1];

                    if (!plcname.matches("^([A-Za-z])(\\w)*$")){
                     throw new MapFormatException(linenr,"Place name \""+ plcname + "\" is not a valid name");
                    }

                    if (!result[2].matches("^\\d+$")){
                     throw new MapFormatException(linenr,"x-coordinate for \""+ plcname + "\" is not a valid non-negative integer");
                    }

                    if (!result[3].matches("^\\d+$")){
                     throw new MapFormatException(linenr,"y-coordinate for \""+ plcname + "\" is not a valid non-negative integer");
                    } 

                    int x = Integer.parseInt(result[2]);
                    int y = Integer.parseInt(result[3]);
                    try {
                     m.newPlace(plcname, x, y);
                    } catch (IllegalArgumentException e){
                    throw new MapFormatException(linenr, "Cannot create place: " + plcname + ", " + e.getMessage());
                    }
                }
               
                //process a road
                else if ((result[0].equals("road"))&& (result.length==5)){
                    String plcfr = result[1];
                    String plcto = result[4];
                    String road = result[2];
                    
                    if (!road.matches("^([A-Za-z])([A-Za-z0-9])*$|^-$")){
                     throw new MapFormatException(linenr,"Road name \""+ road + "\" is not valid");
                    }

                    if (!result[3].matches("^\\d+$")){
                     throw new MapFormatException(linenr,"road length for road \""+ road +"\" is not valid");
                    }

                    Place from = m.findPlace(plcfr);
                    Place to = m.findPlace(plcto);

                    if ((from!=null)&&(to!=null)){
                        try{
                        if(road.equals("-"))
                            road="";
                        
                        m.newRoad(from, to, road,Integer.parseInt(result[3]));
                        
                        } 
                        catch(IllegalArgumentException e){
                        throw new MapFormatException(linenr,"Cannot create Road: " + road + ", " + e.getMessage());
                        }
                    }
                    else {
                    throw new MapFormatException(linenr,"One of the places for road \""+ road +"\" is not in the map");
                    }

                }
               
               //parse start place
               else if ((result[0].equals("start")) && (result.length==2)){
                    String plcname = result[1];

                    if (!plcname.matches("^([A-Za-z])(\\w)*$")){
                     throw new MapFormatException(linenr,"Place name \""+ plcname + "\" is not valid");
                    }
                   
                   Place p = m.findPlace(plcname);
                   if (p!=null){
                   m.setStartPlace(p);
                   
                   }
                   else{
                   throw new MapFormatException(linenr,"Start place \""+ plcname + "\" is not in the map");
                   }
               }
               
               //parse end place
               else if ((result[0].equals("end"))&& (result.length==2)){
                   String plcname = result[1];

                   if (!plcname.matches("^([A-Za-z])(\\w)*$")){
                    throw new MapFormatException(linenr,"Place name \""+ plcname + "\" is not valid");
                   }
                   
                   Place p = m.findPlace(plcname);
                   if (p!=null){
                   m.setEndPlace(p);
                   }
                   else{
                   throw new MapFormatException(linenr,"End place \""+ plcname + "\" is not in the map");
                   }
               }
               
               else{
                   throw new MapFormatException(linenr,"Unable to parse line: " + line);
               }
               
           line = b.readLine();
           linenr++;
           }
       
    }
    
    
    //Write a representation of the Map, m, to the Writer w.
    public void write(Writer w, Map m) throws IOException{
    
    
        String map="";
         
        Set<Place> existplc = m.getPlaces();
        
        for (Place p : existplc){
          map=map+"place "+p.getName() + " " + p.getX() + " " + p.getY() +"\n";
        }
        
        map=map+"\n";
        
        Set<Road> existroad = m.getRoads();
        
        for (Road r : existroad){
          String rname = r.roadName();
          if (rname.equals(""))
              rname="-";
          
          map=map+"road "+r.firstPlace().getName() + " " + rname + " " + r.length() + " " + r.secondPlace().getName()+"\n";
        }
        
        if (m.getStartPlace()!=null){
        map=map+"start "+ m.getStartPlace().getName() + "\n";
        }
        
        if (m.getEndPlace()!=null){
        map=map+"end " + m.getEndPlace().getName() + "\n";
        }

        try{
        w.write(map);
        }
        catch (IOException e){
        throw new IOException(e.getMessage());
        }
        
    }
}
