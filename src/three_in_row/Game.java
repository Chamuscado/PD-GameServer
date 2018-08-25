package three_in_row;

import GameLib.Player;
import GameLib.Token;
import three_in_row.User;
import three_in_row.logic.InternalPlayer;
import three_in_row.logic.InternalToken;
import three_in_row.logic.ObservableGame;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class Game implements Observer {
    final String NULL = "null";
    private User[] users;
    private boolean run;
    private ObservableGame obsGame;
    static final boolean debugMessages = true;
    private IGameServer servergame;

    public Game(IGameServer server, Socket socketPlayer0, Socket socketPlayer1) {
        servergame = server;
        users = new User[2];
        users[0] = new User(0, socketPlayer0, this);
        users[1] = new User(1, socketPlayer1, this);
        obsGame = new ObservableGame();
    }

    public Game(IGameServer server, Socket socketPlayer) {
        servergame = server;
        users = new User[2];
        users[0] = new User(0, socketPlayer, this);
        users[1] = null;
        obsGame = new ObservableGame();
    }

    public void addUser(Socket socketPlayer1) {
        if (users[1] == null) {
            users[1] = new User(1, socketPlayer1, this);
            users[1].start();
        }
    }

    public int getNumPlayers() {
        int n = 0;
        for (User user : users) {
            if (user != null)
                ++n;
        }
        return n;
    }

    public void start() {
        if (run) return;
        obsGame.addObserver(this);
        run = true;
        if (users[0] != null)
            users[0].start();
        if (users[1] != null)
            users[1].start();
    }


    public void stop() {
        if (run) {
            run = false;
            users[0].close();
            users[1].close();
            servergame.removeGame(this);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("update");
        if (users[0] != null)
            users[0].sendObject("update");
        if (users[1] != null)
            users[1].sendObject("update");
    }

    void startGame(User user) {
        if (getNumPlayers() == 2)
            obsGame.startGame(user.id);
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

    Object hasWon(User user, Object player) {
        System.out.println("(" + player + ")");
        if (player instanceof InternalPlayer)
            return obsGame.hasWon((InternalPlayer) player);
        System.out.println("hasWon: Parametro errado");
        return false;
    }

    void setPlayerName(User user, Object num, Object name) {
        System.out.println("(" + num + "," + name + ")");
        if (num instanceof Integer && name instanceof String) {
            int lnum = (int) num;
            if (lnum == user.id)
                obsGame.setPlayerName(lnum, (String) name);
        } else {
            System.out.println("setPlayerName: Parametros errados");
        }
    }

    void placeToken(User user, Object line, Object column) {
        System.out.println("(" + line + "," + column + ")");
        if (line instanceof Integer && column instanceof Integer) {
            if (obsGame.getCurrentPlayer().id == user.id)
                obsGame.placeToken((int) line, (int) column);
        } else {
            System.out.println("placeToken: Parametros errados");
        }
    }

    void returnToken(User user, Object line, Object column) {
        System.out.println("(" + line + "," + column + ")");
        if (line instanceof Integer && column instanceof Integer)
            if (obsGame.getCurrentPlayer().id == user.id)
                obsGame.returnToken((int) line, (int) column);
            else {
                System.out.println("returnToken: Parametros errados");
            }
    }

    Object getToken(User user, Object line, Object column) {
        System.out.println("(" + line + "," + column + ")");
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
}
