package GameServer.Logic.states;

import GameLib.Constants;

import java.io.Serializable;

/*
 * List of expected events when taking into account all the states.
 */
public interface IStates extends Serializable, Constants {

    IStates setName(int num, String name);

    IStates startGame(int playerNum);

    IStates placeToken(int line, int column);

    IStates returnToken(int line, int column);

}
