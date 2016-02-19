
public class MapFormatException extends Exception
{
    private int lineNr;
    
    public MapFormatException(int lineNr, String msg)
    {
	super(msg);
	this.lineNr= lineNr;
    }
    
    
    public String toString()
    {
	return lineNr+":0:"+super.toString();
    }
}
