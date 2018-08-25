package three_in_row.logic.states;

import three_in_row.logic.GameData;
import three_in_row.logic.InternalPlayer;
import three_in_row.logic.InternalToken;

import static GameLib.Constants.DIM;

public class AwaitReturn extends StateAdapter
{
    
    public AwaitReturn(GameData g)
    {
        super(g);
    }

    @Override
    public IStates returnToken(int line, int column)
    { 
        InternalPlayer p = getGame().getCurrentPlayer();
        
        
        if (line < 0 || line >= DIM || column < 0 || column >= DIM) {
            return this;
        }
        
        InternalToken InternalToken = getGame().getToken(line, column);
        
        if (InternalToken == null || InternalToken.getInternalPlayer() != p) {
            return this;
        }
        
        p.getAvailableInternalTokens().add(InternalToken);
        getGame().removeToken(line, column);
        return new AwaitPlacement(getGame(), line, column);
    }
    
    @Override
    public IStates quit()
    { 
        getGame().getNotCurrentPlayer().setHasWon(true);
        return new AwaitBeginning(getGame());
    }
    
}
