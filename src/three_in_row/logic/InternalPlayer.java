package three_in_row.logic;

import GameLib.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InternalPlayer implements Serializable, Constants
{
    static final long serialVersionUID = 1; //or 1L
    private String name;
    private List<InternalToken> availableInternalTokens = new ArrayList<>();
    private boolean hasWon;
    public final int id;

    public InternalPlayer(String name, int id)
    {
        this.name = name;
        hasWon = false;
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public boolean getHasWon()
    {
        return hasWon;
    }

    public void setHasWon(boolean hasWon)
    {
        this.hasWon = hasWon;
    }

    public void getNewTokens()
    {
        availableInternalTokens.clear();

        for(int i=0; i<NUM_TOKENS_TURN; i++){
            availableInternalTokens.add(new InternalToken(this));
        }

        hasWon = false;
    }

    public List<InternalToken> getAvailableInternalTokens()
    {
        return availableInternalTokens;
    }

    public int getNumAvailableTokens()
    {
        System.out.println("getNumAvailableTokens -> "+ availableInternalTokens.size());
        return availableInternalTokens.size();
    }

    @Override
    public String toString()
    {
        return "InternalPlayer " + name + "  Available tokens: " + availableInternalTokens.size() +  (hasWon?"\nHAS WON!":"");
    }

}
