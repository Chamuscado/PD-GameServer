import HeartBeats.HeartBeatsGame;

public class GameServer {
    static public void main(String[] args) {
        HeartBeatsGame heartBeatsGame = HeartBeatsGame.startHeartBeatsGame("localhost", "RemoteT");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        heartBeatsGame.stop();
    }
}
