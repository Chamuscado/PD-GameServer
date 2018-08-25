import GameLib.Constants;
import HeartBeats.HeartBeatsGame;
import three_in_row.Game;
import three_in_row.IGameServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameServer implements IGameServer, Constants {
    static public void main(String[] args) {


        HeartBeatsGame heartBeatsGame = HeartBeatsGame.startHeartBeatsGame("127.0.0.1", "RemoteT");
        /*
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        GameServer server = new GameServer();
        server.run();

        heartBeatsGame.stop();
    }

    private ServerSocket socket;
    private int port;
    private boolean run;
    private boolean messages;
    private List<Game> games;


    public GameServer(int port) {
        games = new ArrayList<>();
        this.messages = true;
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
        run = true;
        Game game = null;
        if (messages)
            System.out.println("Inicio:");
        while (run) {

            try {
                Socket cli = socket.accept();
                if (messages)
                    System.out.println("Novo Cliente aceite, ip:" + cli.getInetAddress() + ":" + cli.getPort());
                if (game == null || game.getNumPlayers() >= 2) {
                    game = new Game(this, cli);
                    game.start();
                    synchronized (games) {
                        games.add(game);
                    }
                } else
                    game.addUser(cli);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
        }
        if (messages)
            System.out.println("Fim:");
    }

    public void removeGame(Game game) {
        synchronized (games) {
            games.remove(game);
        }
    }
}