package GameServer.three_in_row;

import GameLib.Strings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class User implements Runnable, Strings {

    private boolean run;
    private Thread thread;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Game_old parent;
    public final int id;

    public User(int id, Socket socket, Game_old parent) {
        this.id = id;
        this.parent = parent;
        this.socket = socket;
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
    }

    @Override
    public void run() {
        while (run) {
            Object obj = readObject();
            if (obj == null)
                break;
            if (obj instanceof String) {
                String str = (String) obj;
                if (Game_old.debugMessages)
                    System.out.println(str);
                Object objToSend = selectMethod(str);
                if (Game_old.debugMessages)
                    System.out.println("Sending: <" + objToSend + ">");
                if (objToSend != null)
                    sendObject(objToSend);
                else
                    sendObject(NULL);
            }

        }
        close();
    }


    private Object selectMethod(String str) {
        switch (str) {
            case STARTGAME:
                parent.startGame(this);
                return null;
            case GETPLAYER_0:
                return parent.getPlayer0(this);
            case GETPLAYER_1:
                return parent.getPlayer1(this);
            case GETCURRENTPLAYRT:
                return parent.getCurrentPlayer(this);
            case ISOVER:
                return parent.isOver(this);
            case GETSTATE:
                return parent.getState(this);
            case HASWON:
                return parent.hasWon(this, readObject());
            case SETPLAYERNAME:
                parent.setPlayerName(this, readObject(), readObject());
                return null;
            case PLACETOKEN:
                parent.placeToken(this, readObject(), readObject());
                return null;
            case RETURNTOKEN:
                parent.returnToken(this, readObject(), readObject());
                return null;
            case GETTOKEN:
                return parent.getToken(this, readObject(), readObject());
            case GETMYID:
                return id;


            default:
                return null;
        }
    }


    void sendObject(Object obj) {
        try {
            out.writeObject(obj);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void close() {
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
        parent.stop();
    }

    private Object readObject() {

        try {
            return in.readObject();
        } catch (SocketException ex) {
            System.out.println("Conexao com " + socket.getInetAddress() + ":" + socket.getPort() + " perdida.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


}
