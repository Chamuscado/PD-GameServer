
package GameServer.Logic;

import GameServer.Logic.states.AwaitBeginning;
import GameServer.Logic.states.AwaitPlacement;
import GameServer.Logic.states.AwaitReturn;
import GameServer.Logic.states.IStates;
import GameLib.States;

import java.util.Observable;

public class ObservableGame extends Observable {
    private GameModel gameModel;

    public ObservableGame() {
        gameModel = new GameModel();
    }

    public Byte getState() {
        IStates state = gameModel.getState();
        Byte st = -1;
        if (state instanceof AwaitBeginning)
            st = States.AwaitBeginning;
        else if (state instanceof AwaitPlacement)
            st = States.AwaitPlacement;
        else if (state instanceof AwaitReturn)
            st = States.AwaitReturn;
        return st;
    }

    // Methods retrieve data from the game

    public InternalPlayer getCurrentPlayer() {
        return gameModel.getCurrentPlayer();
    }

    public InternalPlayer getPlayer0() {
        return gameModel.getPlayer0();
    }

    public InternalPlayer getPlayer1() {
        return gameModel.getPlayer1();
    }

    public InternalToken getToken(int line, int column) {
        return gameModel.getToken(line, column);
    }

    public boolean isOver() {
        return gameModel.isOver();
    }

    public int hasWon() {
        return gameModel.hasWon();
    }

    public void setPlayerName(int num, String name) {

        gameModel.setPlayerName(num, name);
        setChanged();
        notifyObservers();
    }

    public void startGame(int playerNum) {
        gameModel.startGame(playerNum);

        setChanged();
        notifyObservers();
    }

    public void placeToken(int line, int column) {
        gameModel.placeToken(line, column);

        setChanged();
        notifyObservers();
    }

    public void returnToken(int line, int column) {
        gameModel.returnToken(line, column);


        setChanged();
        notifyObservers();
    }
}
