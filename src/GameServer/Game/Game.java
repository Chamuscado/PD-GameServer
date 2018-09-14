package GameServer.Game;

import DataBaseIO.DataBaseIO;
import DataBaseIO.Elements.GameDatabase;
import DataBaseIO.Elements.PairDatabase;
import DataBaseIO.Elements.PlayerDatabase;
import DataBaseIO.Exceptions.*;
import GameServer.Logic.InternalPlayer;
import GameServer.Logic.InternalToken;
import GameServer.Logic.ObservableGame;
import GameLib.*;
import javafx.scene.shape.Path;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class Game implements Observer, Constants {
    private static final String PATH = ".\\Saves\\";
    private User[] users;
    private boolean run;
    private ObservableGame obsGame;
    static final boolean debugMessages = true;
    private IGameServer servergame;
    private DataBaseIO dataBase;

    public Game(DataBaseIO dataBase, IGameServer server, User user0, User user1) {
        this(dataBase, server, user0);
        addUser(user1);
    }

    public Game(DataBaseIO dataBase, IGameServer server, User user) {
        File file = new File(System.getProperty("user.dir") + PATH);
        if (!file.exists())
            file.mkdir();
        if (debugMessages)
            System.out.println("a criar Game...");
        servergame = server;
        run = true;
        users = new User[2];
        this.dataBase = dataBase;
        String filename = "";
        try {
            filename = System.getProperty("user.dir") + PATH + dataBase.getPairAtual(user.getUsername()).getId();
        } catch (UserNotFoundException | PairAtualNotFoundException e) {
            e.printStackTrace();
        }
        obsGame = new ObservableGame(filename);
        obsGame.addObserver(this);
        users[0] = user;
        users[0].setParentAndId(this, 0);
        users[1] = null;
        users[0].sendObject(new ClientRequest(ClientRequestKey.WAIT, null));

    }

    public void addUser(User user) {
        if (debugMessages)
            System.out.println("a adicionar o user " + user.getUsername() + " ao Game...");
        if (users[1] == null) {
            users[1] = user;
            users[1].setParentAndId(this, 1);
            users[0].sendObject(new ClientRequest(ClientRequestKey.ENDWAIT, null));
        }
        try {
            dataBase.createGame(dataBase.getPair(users[0].getUsername(), users[1].getUsername()));

        } catch (UserNotFoundException | PairNotFoundException | CorruptDataBaseException e) {
            endGame();
        } catch (UnfinishedGameException e) {

        }
        startGame(users[0]);
    }

    public int getNumPlayers() {
        int n = 0;
        for (User user : users) {
            if (user != null)
                ++n;
        }
        return n;
    }

    public void stop() {
        if (run) {
            run = false;
            users[0].stop();
            users[1].stop();
            servergame.removeGame(this);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (users[0] != null)
            users[0].update();
        if (users[1] != null)
            users[1].update();
    }

    void startGame(User user) {
        if (getNumPlayers() == 2) {
            obsGame.startGame(user.getId());
            if (users[0] != null)
                users[0].update();
            if (users[1] != null)
                users[1].update();
        }
    }

    Player getPlayer0(User user) {
        return InternalPlayerToPlayer(obsGame.getPlayer0());
    }

    Player getPlayer1(User user) {
        return InternalPlayerToPlayer(obsGame.getPlayer1());
    }

    Player getCurrentPlayer(User user) {
        return InternalPlayerToPlayer(obsGame.getCurrentPlayer());
    }

    boolean isOver(User user) {
        return obsGame.isOver();
    }

    Object getState(User user) {
        return obsGame.getState();
    }

    int hasWon(User user) {
        return obsGame.hasWon();

    }

    void placeToken(User user, Object line, Object column) {
        if (line instanceof Integer && column instanceof Integer) {
            //  if (obsGame.getCurrentPlayer().id == user.getId())
            if (obsGame.getCurrentPlayer().getName().equals(user.getPlayerName()))
                obsGame.placeToken((int) line, (int) column);
        } else {
            System.out.println("placeToken: Parametros errados");
        }
        if (obsGame.isOver())
            endGame();
    }

    void returnToken(User user, Object line, Object column) {
        if (line instanceof Integer && column instanceof Integer)
            //   if (obsGame.getCurrentPlayer().id == user.getId())
            if (obsGame.getCurrentPlayer().getName().equals(user.getPlayerName()))
                obsGame.returnToken((int) line, (int) column);
            else {
                System.out.println("returnToken: Parametros errados");
            }
        if (obsGame.isOver())
            endGame();
    }

    Object getToken(User user, Object line, Object column) {
        if (line instanceof Integer && column instanceof Integer)
            return InternalTokenToToken(obsGame.getToken((int) line, (int) column));
        else {
            System.out.println("getToken: Parametros errados");
            return null;
        }
    }

    private Player InternalPlayerToPlayer(InternalPlayer internalPlayer) {
        if (internalPlayer == null) return null;
        Player player = new Player(internalPlayer.getName(), internalPlayer.id);

        player.availableTokens = new ArrayList<>();

        List<InternalToken> list = internalPlayer.getAvailableInternalTokens();

        for (int i = 0; i < list.size(); ++i)
            player.availableTokens.add(new Token(player));

        return player;
    }

    private Token InternalTokenToToken(InternalToken internalToken) {
        if (internalToken == null) return null;
        return new Token(InternalPlayerToPlayer(internalToken.getInternalPlayer()));
    }

    private void endGame() {
        int winner = obsGame.hasWon();

        for (User user : users) {
            user.endGame();
        }
        if (winner != INVALID_ID) {
            try {
                PlayerDatabase player = dataBase.getPlayer(users[0].getUsername());
                GameDatabase game = dataBase.getPairUnfinishedGame(dataBase.getPair(users[0].getUsername(), users[1].getUsername()));
                dataBase.setGameWinner(game, player);
            } catch (UserNotFoundException | GameIdInvalidException | PairNotFoundException | AnyUnfinishedGameException | CorruptDataBaseException e) {
                e.printStackTrace();
            }
        }
        stop();
    }

    public void setPlayerName(int num, String name) {
        if (debugMessages)
            System.out.println("setPlayerName(" + num + "," + name + ");");
        obsGame.setPlayerName(num, name);
    }

    public User getUser(int i) {
        if (i >= 0 && i < users.length)
            return users[i];
        return null;
    }
}
