
public interface MapListener
{
    //Called whenever the number of places in the map has changed
    public void placesChanged();

    //Called whenever the number of roads in the map has changed
    public void roadsChanged();

    //Called whenever something about the map has changed
    //(other than places and roads)
    public void otherChanged();
}
