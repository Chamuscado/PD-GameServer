package GameServer.Logic.states;


import GameServer.Logic.GameData;

public class StateAdapter implements IStates {
    private GameData game;

    public StateAdapter(GameData g)
    {
        this.game =g;
    }

    public GameData getGame()
    {
        return game;
    }

    public void setGame(GameData game)
    {
        this.game = game;
    }

    @Override
    public IStates setName(int num, String name){ return this;}

    @Override
    public IStates startGame(int playerNum){ return this;}

    @Override
    public IStates placeToken(int linha, int coluna){ return this;}

    @Override
    public IStates returnToken(int linha, int coluna){ return this;}
}
