package GameServer.Logic;

import java.io.Serializable;

public class InternalToken implements Serializable
{
    static final long serialVersionUID = 1; //or 1L
    private InternalPlayer internalPlayer;

    public InternalToken(InternalPlayer internalPlayer)
    {
        this.internalPlayer = internalPlayer;
    }

    public InternalPlayer getInternalPlayer()
    {
        return internalPlayer;
    }

    @Override
    public String toString()
    {
        return "" + internalPlayer.getName().charAt(0);
    }
    
}
