package three_in_row;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class User implements Runnable {

    private boolean run;
    private Thread thread;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Game parent;
    public final int id;

    public User(int id, Socket socket, Game parent) {
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
                if (Game.debugMessages)
                    System.out.println(str);
                Object objToSend = selectMethod(str);
                if (Game.debugMessages)
                    System.out.println("Sending: <" + objToSend + ">");
                if (objToSend != null)
                    sendObject(objToSend);
                else
                    sendObject(parent.NULL);
            }

        }
        close();
    }


    private Object selectMethod(String str) {
        switch (str) {
            case "startGame":
                parent.startGame(this);
                return null;
            case "getPlayer0":
                return parent.getPlayer0(this);
            case "getPlayer1":
                return parent.getPlayer1(this);
            case "getCurrentPlayer":
                return parent.getCurrentPlayer(this);
            case "isOver":
                return parent.isOver(this);
            case "getState":
                return parent.getState(this);
            case "hasWon":
                return parent.hasWon(this, readObject());
            case "setPlayerName":
                parent.setPlayerName(this, readObject(), readObject());
                return null;
            case "placeToken":
                parent.placeToken(this, readObject(), readObject());
                return null;
            case "returnToken":
                parent.returnToken(this, readObject(), readObject());
                return null;
            case "getToken":
                return parent.getToken(this, readObject(), readObject());
            case "getMyId":
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
