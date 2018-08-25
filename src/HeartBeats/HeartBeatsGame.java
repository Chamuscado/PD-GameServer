package HeartBeats;

import java.net.MalformedURLException;
import java.rmi.*;

public class HeartBeatsGame implements Runnable {
    boolean cont = true;
    static Thread thread = null;
    static IHeartBeats beat = null;
    int delay = 3000;
    public boolean DEBUG = false;

    static public HeartBeatsGame startHeartBeatsGame(String registry, String serviceStr) {
        if (thread != null) {
            System.out.println("o Serviço já esta a correr");
        }
        try {
            String registration = "rmi://" + registry + "/" + serviceStr;
            Remote service = Naming.lookup(registration);
            beat = (IHeartBeats) service;
            HeartBeatsGame heartBeatsGame = new HeartBeatsGame();
            thread = new Thread(heartBeatsGame);
            thread.start();
            return heartBeatsGame;
        } catch (ConnectException e) {
            System.out.println("Nenhum servidor encontrado em <" + registry + "> com o nome de seviço <" + serviceStr + ">");
            System.exit(0);
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void run() {
        while (cont) {
            try {
                if (DEBUG)
                    System.out.println(beat.heartBeat());
                Thread.sleep(delay);
            } catch (RemoteException | InterruptedException e) {
                e.printStackTrace();
                break;
            }

        }
    }

    public void stop() {
        cont = false;
        thread = null;
    }
}
