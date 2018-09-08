package GameServer.Game;

public interface IGameServer {
    void removeGame(Game game);

    void loginCompleted(User user);
}
