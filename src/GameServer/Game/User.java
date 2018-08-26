package GameServer.Game;


import DataBaseIO.DataBaseIO;
import DataBaseIO.Elements.PlayerDatabase;
import DataBaseIO.Exceptions.UserNotFoundException;
import GameLib.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class User implements Runnable, Constants {

    private boolean run;
    private Thread thread;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Game parent;
    private int id;
    private DataBaseIO dataBase;
    private PlayerDatabase player;
    private IGameServer gameServer;

    public User(Socket socket, DataBaseIO dataBase, IGameServer gameServer) {
        this.id = INVALID_ID;
        this.dataBase = dataBase;
        this.socket = socket;
        this.gameServer = gameServer;
        thread = new Thread(this);
        try {
            out = new ObjectOutputStream(this.socket.getOutputStream());
            in = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void start() {
        run = true;
        thread.start();
        update();
    }

    @Override
    public void run() {
        while (run) {
            ServerRequest request = null;
            request = readRequest();
            if (parent != null || (request != null && request.getRequestKey() == ServerRequestKey.LOGIN)) {
                if (request != null) {
                    selectMethod(request);
                } else {
                    if (parent.isOver(this)) {
                        break;
                    } else {
                        //TODO -> save game
                    }
                }
            }
        }
        stop();
    }


    private void selectMethod(ServerRequest request) {
        System.out.println("Recevido: <" + request + ">");
        switch (request.getRequestKey()) {
            case LOGIN:
                gameLogin(request);
                return;
            case STARTGAME:
                parent.startGame(this);
                return;
            case PLACETOKEN:
                parent.placeToken(this, ((Object[]) request.getParams())[0], ((Object[]) request.getParams())[1]);
                return;
            case RETURNTOKEN:
                parent.returnToken(this, ((Object[]) request.getParams())[0], ((Object[]) request.getParams())[1]);
                return;
            default:
        }
    }

    private void gameLogin(ServerRequest request) {
        String username = (String) request.getParams();
        try {
            player = dataBase.getPlayer(username);
            if (!player.isLogado()) {
                sendObject(new ClientRequest(ClientRequestKey.ENDGAME));
                stop();
                return;
            } else {
                gameServer.loginComplet(this);
                if (parent != null && id != INVALID_ID && player != null) {
                    parent.setPlayerName(id, String.format("%s(%s)", player.getName(), player.getUser()));
                    update();
                }
            }
        } catch (UserNotFoundException e) {
            player = null;
        }
    }


    void sendObject(ClientRequest request) {
        try {
            System.out.println("Enviado: <" + request + ">");
            out.writeObject(request);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (!run)
            return;
        run = false;
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ServerRequest readRequest() {

        while (true)
            try {
                Object obj;
                do {
                    obj = in.readObject();
                } while (!(obj instanceof ServerRequest));
                return (ServerRequest) obj;
            } catch (SocketTimeoutException ex) {
                System.out.println("Timeout - readObj");
            } catch (SocketException e) {
                System.out.println("Conexao com " + socket.getInetAddress() + ":" + socket.getPort() + " perdida.");
                run = false;
                break;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                break;
            }
        return null;
    }


    public void update() {
        System.out.println("Update fase 1");
        if (parent == null)
            return;
        System.out.println("Update fase 2");
        sendObject(new ClientRequest(ClientRequestKey.SETPLAYER_0, parent.getPlayer0(this)));
        sendObject(new ClientRequest(ClientRequestKey.SETPLAYER_1, parent.getPlayer1(this)));
        sendObject(new ClientRequest(ClientRequestKey.SETCURRENTPLAYER, parent.getCurrentPlayer(this)));
        sendObject(new ClientRequest(ClientRequestKey.ISOVER, parent.isOver(this)));
        sendObject(new ClientRequest(ClientRequestKey.SETSTATE, parent.getState(this)));
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 3; ++j) {
                Object token = parent.getToken(this, i, j);
                sendObject(new ClientRequest(ClientRequestKey.SETTOKEN, new Object[]{i, j, token}));
            }
        sendObject(new ClientRequest(ClientRequestKey.HASWON, parent.hasWon(this)));
        sendObject(new ClientRequest(ClientRequestKey.READY));
        sendObject(new ClientRequest(ClientRequestKey.UPDATE));
    }

    public void endGame() {
        sendObject(new ClientRequest(ClientRequestKey.ENDGAME));
    }

    public void setParentAndId(Game parent, int id) {
        this.parent = parent;
        this.id = id;
        if (parent != null && id != INVALID_ID && player != null) {
            parent.setPlayerName(id, String.format("%s(%s)", player.getName(), player.getUser()));
            update();
        }
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return player.getUser();
    }
}
