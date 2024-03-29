package GameServer.Logic.states;


import GameServer.Logic.GameData;
import GameServer.Logic.InternalPlayer;
import GameServer.Logic.InternalToken;

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
}
