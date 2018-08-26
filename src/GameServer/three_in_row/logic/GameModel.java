package GameServer.three_in_row.logic;

import GameServer.three_in_row.logic.states.AwaitBeginning;
import GameServer.three_in_row.logic.states.IStates;

import java.io.Serializable;

/**
 *
 * @author Jose Marinho
 */

public class GameModel  implements Serializable
{
    
    private GameData gameData;
    private IStates state;
    
    public GameModel()
    {
        gameData = new GameData();
        setState(new AwaitBeginning(gameData));
    }

    public IStates getState()
    {
        return state;
    }
    
    private void setState(IStates state)
    {
        this.state = state;
    }        
    
     // Methods retrieve data from the game
    
    public InternalPlayer getCurrentPlayer()
    {
        return gameData.getCurrentPlayer();
    }
    
    public InternalPlayer getPlayer0()
    {
        return gameData.getPlayer0();
    }

    public InternalPlayer getPlayer1()
    {
        return gameData.getPlayer1();
    }

    public InternalToken getToken(int line, int column)
    {
        return gameData.getToken(line, column);
    }
    
    public boolean isOver() 
    {
        return gameData.isOver();
    }
    
    public boolean hasWon(InternalPlayer internalPlayer)
    {
        return gameData.hasWon(internalPlayer);
    }
    
    // Methods that are intended to be used by the user interfaces and that are delegated in the current state of the finite state machine 


    public void setPlayerName(int num, String name) 
    {
        setState(getState().setName(num, name));
    }

    public void startGame(int playerNum)
    {
        setState(getState().startGame(playerNum));
    }

    public void placeToken(int line, int column)
    {
        setState(getState().placeToken(line, column));
    }

    public void returnToken(int line, int column)
    {
        setState(getState().returnToken(line, column));
    }
}
