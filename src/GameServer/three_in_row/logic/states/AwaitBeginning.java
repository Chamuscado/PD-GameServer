package GameServer.three_in_row.logic.states;

import GameServer.three_in_row.logic.GameData;

public class AwaitBeginning extends StateAdapter
{
    
    public AwaitBeginning(GameData g)
    {
        super(g);
    }
    
    @Override
    public IStates setNumberPlayers(int num)
    { 
        getGame().setNumPlayers(num);
        return this; 
    }
    
    @Override
    public IStates setName(int num, String name)
    { 
        getGame().setPlayerName(num, name);
        return this; 
    }
    
    @Override
    public IStates startGame(int playerNum)
    {
        if( getGame().initialize(playerNum)){
            return new AwaitPlacement(getGame());
        }
        
        return this; 
    }
    
 }
