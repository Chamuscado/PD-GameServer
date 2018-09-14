
package GameServer.Logic;

import GameServer.Logic.states.AwaitBeginning;
import GameServer.Logic.states.AwaitPlacement;
import GameServer.Logic.states.AwaitReturn;
import GameServer.Logic.states.IStates;
import GameLib.States;

import java.io.*;
import java.util.Observable;

import static GameLib.Constants.INVALID_ID;

public class ObservableGame extends Observable {
    private GameModel gameModel;
    private String fileName;

    public ObservableGame(String fileName) {
        //gameModel = new GameModel();
        this.fileName = fileName + ".game";
        gameModel = read(this.fileName);
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
        int won = gameModel.hasWon();
        if (won != INVALID_ID)
            delete(fileName);
        return won;
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
        save(gameModel, fileName);
    }

    public void returnToken(int line, int column) {
        gameModel.returnToken(line, column);

        setChanged();
        notifyObservers();
        save(gameModel, fileName);
    }

    private static void save(GameModel gameModel, String filename) {
        ObjectOutputStream oout = null;

        try {
            try {
                File file = new File(filename);
                oout = new ObjectOutputStream(new FileOutputStream(file));
                oout.writeObject(gameModel);

            } finally {
                if (oout != null)
                    oout.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static GameModel read(String filename) {
        ObjectInputStream oin = null;
        try {

            try {
                File file = new File(filename);
                oin = new ObjectInputStream(new FileInputStream(file));
                return (GameModel) oin.readObject();

            } finally {
                if (oin != null)
                    oin.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            return new GameModel();
        }
    }

    private static void delete(String filename) {
        File file = new File(filename);
        file.delete();
    }
}
