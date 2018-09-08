package GameServer;

import DataBaseIO.DataBaseIO;
import DataBaseIO.Elements.PairDatabase;
import DataBaseIO.Exceptions.PairAtualNotFoundException;
import DataBaseIO.Exceptions.UserNotFoundException;
import GameLib.Constants;
import GameServer.Game.Game;
import GameServer.Game.IGameServer;
import GameServer.Game.User;
import HeartBeats.HeartBeatsGame;
import HeartBeats.IHeartBeatsGameParent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameServer implements IGameServer, Constants, IHeartBeatsGameParent {

    private final static String dataBaseUser = "GameServer";
    private final static String dataBasePass = "pd-1718";
    private static String dataBaseName = "PD_DataBase";


    static public void main(String[] args) {

        GameServer server = new GameServer();
        HeartBeatsGame heartBeatsGame = HeartBeatsGame.startHeartBeatsGame(server, "127.0.0.1");


        //   heartBeatsGame.DEBUG = true;
        /*
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        server.run();
        heartBeatsGame.stop();

    }

    private DataBaseIO dataBase;
    private String dataBaseIP;
    private int dataBasePort;
    private ServerSocket socket;
    private int port;
    private boolean run;
    private boolean DEBUG;
    private final List<Game> games;
    private final List<Game> gameNotStarted;


    public GameServer(int port) {
        games = new ArrayList<>();
        gameNotStarted = new ArrayList<>();
        this.DEBUG = true;
        this.port = port;
        this.run = true;
        try {
            socket = new ServerSocket(port);
        } catch (IOException ex) {
            System.out.println("Erro de IO" + ex);
        }


    }

    public GameServer() {
        this(PORT);
    }

    public int getPort() {
        return port;
    }

    public void run() {
        dataBase = new DataBaseIO(dataBaseName, dataBaseUser, dataBasePass, dataBaseIP);
        //DataBaseIO.DEBUG = true;
        int i = 0;
        System.out.println("A tentar conectar à base de dados...");
        while (!dataBase.connect()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                continue;
            }
            dataBase.setIp(dataBaseIP);
            dataBase.setPort(dataBasePort);
            System.out.println("A tentar conectar à base de dados... tentativa nº " + (++i) + " <" + dataBase.getIp() + ":" + dataBase.getPort() + ">");

        }
        run = true;
        Game game = null;
        if (DEBUG)
            System.out.println("Inicio:");
        while (run) {

            try {
                Socket cli = socket.accept();
                if (DEBUG)
                    System.out.println("Novo Cliente aceite, ip:" + cli.getInetAddress() + ":" + cli.getPort());
                new User(cli, this.dataBase, this).start();
/*
                if (game == null || game.getNumPlayers() >= 2) {

                    game = new Game(dataBase, this, user);
                    game.start();
                    synchronized (games) {
                        games.add(game);
                    }
                } else
                    game.addUser(user);
                    */
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (DEBUG)
            System.out.println("Fim:");
        dataBase.close();
    }

    public void removeGame(Game game) {
        synchronized (games) {
            games.remove(game);
        }
    }

    @Override
    public void loginCompleted(User user) {
        if (DEBUG)
            System.out.println("login of " + user.getUsername() + "complet");
        PairDatabase pair = null;
        try {
            pair = dataBase.getPairAtual(user.getUsername());
        } catch (UserNotFoundException | PairAtualNotFoundException e) {
            user.stop();
            return;
        }
        boolean found = false;
        synchronized (gameNotStarted) {
            for (int i = 0; i < gameNotStarted.size(); ++i) {
                try {
                    if (gameNotStarted.get(i).getUser(0).getUsername().compareTo(pair.getPlayerDatabases(0).getUser()) == 0
                            || gameNotStarted.get(i).getUser(0).getUsername().compareTo(pair.getPlayerDatabases(1).getUser()) == 0) {
                        Game game = gameNotStarted.get(i);
                        gameNotStarted.remove(i);
                        game.addUser(user);
                        synchronized (games) {
                            games.add(game);
                        }
                        found = true;
                        break;
                    }
                } catch (UserNotFoundException e) {
                    return;
                }
            }
        }
        if (!found) {
            Game game = new Game(dataBase, this, user);
            synchronized (gameNotStarted) {
                gameNotStarted.add(game);
            }
        }
    }

    public void setDataBaseIP(String dataBaseIP) {
        this.dataBaseIP = dataBaseIP;
    }

    @Override
    public void setDataBasePort(int port) {
        this.dataBasePort = port;
    }
}