package GameServer.three_in_row.logic.states;

import GameServer.three_in_row.logic.InternalPlayer;
import GameServer.three_in_row.logic.GameData;
import GameServer.three_in_row.logic.InternalToken;

public class AwaitPlacement extends StateAdapter
{

    private int prevLine, prevColumn;

    public AwaitPlacement(GameData g)
    {
        super(g);
        prevLine = prevColumn = -1;
    }

    public AwaitPlacement(GameData g, int prevLine, int prevColumn)
    {
        super(g);
        this.prevLine = prevLine;
        this.prevColumn = prevColumn;
    }

    public boolean isPrevLocation(int line, int column) 
    {
        return line == prevLine && column == prevColumn;
    }

    @Override
    public IStates placeToken(int line, int column)
    {
        if(isPrevLocation(line, column)){
            return this;
        }
        
        if (line < 0 || line >= DIM || column < 0 || column >= DIM) {
            return this;
        }
        
        InternalPlayer p = getGame().getCurrentPlayer();
        InternalToken internalToken = p.getAvailableInternalTokens().get(0);

        if (getGame().placeToken(internalToken, line, column)) {
            
            p.getAvailableInternalTokens().remove(0);
            
            if (getGame().hasWon(p)) {
                p.setHasWon(true);
                return new AwaitBeginning(getGame());
            }
            
            getGame().setNextPlayerTurn();
                        
            if(getGame().getCurrentPlayer().getNumAvailableTokens() > 0){
                return new AwaitPlacement(getGame());
            }else{
                return new AwaitReturn(getGame());
            }
            
        } 
        
        return this;
    }

    @Override
    public IStates quit()
    {
        getGame().getNotCurrentPlayer().setHasWon(true);
        return new AwaitBeginning(getGame());
    }
    
}
