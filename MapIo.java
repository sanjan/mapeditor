
import java.io.Reader;
import java.io.IOException;
import java.io.Writer;

public interface MapIo 
{
    //This class handles reading and writing map representations as 
    //described in the practical specification

    //Read the description of a map from the 
    //Reader r, and transfers it to Map, m.
    public void read (Reader r, Map m) 
      throws IOException, MapFormatException;
    
    
    //Write a representation of the Map, m, to the Writer w.
    public void write(Writer w, Map m)
      throws IOException;
}
