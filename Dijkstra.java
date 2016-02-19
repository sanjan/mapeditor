
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
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



public class Dijkstra
{
  
    private Set<Vertex> vertices = new HashSet<Vertex>(); 

    
    public Dijkstra(Set<Place> p, Set<Road> r){
    
    
         for (Place plc : p){
          Vertex v = new Vertex(plc);
          this.vertices.add(v);
        }
         
        for (Vertex v : this.vertices){
        
            for (Road rd : r){
            if (rd.firstPlace().getName().equals(v.toString())){
            v.addEdge(new Edge(this.getVertex(rd.secondPlace()),rd.length()));
            }
            else if (rd.secondPlace().getName().equals(v.toString())){
            v.addEdge(new Edge(this.getVertex(rd.firstPlace()),rd.length()));
            }
            }
        
        }
    
    
    }
    
    public int computePaths(Place src, Place target)
    {
        if ((src!=null)&&(target!=null)){
        Vertex source = this.getVertex(src);
        
        
        source.minDistance = 0;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
        vertexQueue.add(source);

        while (!vertexQueue.isEmpty()) {
            Vertex u = vertexQueue.poll();
                // Visit each edge exiting u
                for (Edge e : u.adjacencies){
                    
                    Vertex v = e.target;
                    int weight = e.weight;
                    int distanceThroughU = u.minDistance + weight;
                    if (distanceThroughU < v.minDistance) {
                        vertexQueue.remove(v);
                        v.minDistance = distanceThroughU ;
                        v.previous = u;
                        vertexQueue.add(v);
                    }
                }
        }
    
        Vertex t = this.getVertex(target);
        return t.minDistance;
        }
        return Integer.MAX_VALUE;
    }

    public  List<Place> getShortestPathTo(Place target)
    {
        List<Place> path = new ArrayList<Place>();
        
        for (Vertex vertex = this.getVertex(target)  ; vertex != null; vertex = vertex.previous)
            path.add(vertex.place);

        Collections.reverse(path);
        return path;
    }
    
    private Vertex getVertex(Place p){
    
    for (Vertex v : this.vertices){
        if (v.place.equals(p)){
            return v;
        }
    }
    return null;
    }

}

class Vertex implements Comparable<Vertex>
{
    public final Place place;
    public ArrayList<Edge> adjacencies = new ArrayList<Edge>();
    public int minDistance = Integer.MAX_VALUE;
    public Vertex previous;
    public Vertex(Place argName) { place = argName; }
    public String toString() { return place.getName(); }
    public int compareTo(Vertex other)
    {
        return Double.compare(minDistance, other.minDistance);
    }
    public void addEdge(Edge e){
    adjacencies.add(e);
    }

}

class Edge
{
    public final Vertex target;
    public final int weight;
    public Edge(Vertex argTarget, int argWeight){ 
        target = argTarget; 
        weight = argWeight; 
    }
}