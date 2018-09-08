package GameServer.Logic;

import GameLib.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameData implements Serializable, Constants {

    private List<InternalPlayer> internalPlayers;

    private int numCurrentPlayer;
    private InternalToken[][] grid;
    //private IStates state;

    public GameData() {
        internalPlayers = new ArrayList<>();
        // setState(new AwaitBeginning(this));
        numCurrentPlayer = -1;
        internalPlayers.add(new InternalPlayer("A", 0));
        internalPlayers.add(new InternalPlayer("B", 1));
        grid = new InternalToken[DIM][DIM];
    }

    public boolean initialize(int playerNum) {

        grid = new InternalToken[DIM][DIM];
        internalPlayers.get(0).getNewTokens();
        internalPlayers.get(1).getNewTokens();

        numCurrentPlayer = playerNum;

        return true;
    }

    public InternalPlayer getCurrentPlayer() {
        if (numCurrentPlayer >= 0 && numCurrentPlayer < internalPlayers.size()) {
            return internalPlayers.get(numCurrentPlayer);
        }

        return null;
    }

    public InternalPlayer getNotCurrentPlayer() {
        if (numCurrentPlayer == 0) {
            return internalPlayers.get(1);
        } else if (numCurrentPlayer == 1) {
            return internalPlayers.get(0);
        }

        return null;
    }

    public InternalPlayer getPlayer0() {
        return internalPlayers.get(0);
    }

    public InternalPlayer getPlayer1() {
        return internalPlayers.get(1);
    }

    public InternalToken getToken(int line, int column) {
        if (line < 0 || line >= DIM || column < 0 || column >= DIM) {
            return null;
        }

        if (grid == null) {
            return null;
        }

        return grid[line][column];
    }

    public void setNextPlayerTurn() {
        numCurrentPlayer = (numCurrentPlayer == 0 ? 1 : 0);
    }

    public boolean setPlayerName(int num, String name) {
        try {
            internalPlayers.get(num).setName(name);
            return true;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public boolean placeToken(InternalToken internalToken, int line, int column) {
        if (line < 0 || line >= DIM || column < 0 || column >= DIM) {
            return false;
        }

        if (grid[line][column] != null) {
            return false;
        }

        grid[line][column] = internalToken;
        return true;
    }

    public boolean removeToken(int line, int column) {
        if (line < 0 || line >= DIM || column < 0 || column >= DIM) {
            return false;
        }

        if (grid[line][column] == null) {
            return false;
        }

        grid[line][column] = null;
        return true;
    }

    public boolean isOver() {
        return hasWon() != INVALID_ID;

    }

    public int hasWon() {
        for (InternalPlayer player : internalPlayers) {
            if (isAlignedH(player) || isAlignedD(player) || isAlignedV(player))
                return player.id;
        }
        return INVALID_ID;
    }

    private boolean isAlignedH(InternalPlayer internalPlayer) {
        for (int i = 0; i < DIM; i++) {
            if (isAlignedH(internalPlayer, i)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAlignedH(InternalPlayer internalPlayer, int line) {
        for (InternalToken internalToken : grid[line]) {
            if (internalToken == null || internalToken.getInternalPlayer() != internalPlayer) {
                return false;
            }
        }
        return true;
    }

    private boolean isAlignedV(InternalPlayer internalPlayer) {
        for (int i = 0; i < DIM; i++) {
            if (isAlignedV(internalPlayer, i)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAlignedV(InternalPlayer internalPlayer, int column) {
        for (int i = 0; i < grid[column].length; i++) {
            InternalToken internalToken = grid[i][column];
            if (internalToken == null || internalToken.getInternalPlayer() != internalPlayer) {
                return false;
            }
        }
        return true;
    }

    private boolean isAlignedD(InternalPlayer internalPlayer) {
        return isAlignedMainD(internalPlayer) || isAlignedSecondaryD(internalPlayer);
    }

    private boolean isAlignedMainD(InternalPlayer internalPlayer) {
        for (int i = 0; i < DIM; i++) {
            if (grid[i][i] == null || grid[i][i].getInternalPlayer() != internalPlayer) {
                return false;
            }
        }
        return true;
    }

    private boolean isAlignedSecondaryD(InternalPlayer internalPlayer) {
        for (int i = 0; i < DIM; i++) {
            if (grid[i][DIM - 1 - i] == null || grid[i][DIM - 1 - i].getInternalPlayer() != internalPlayer) {
                return false;
            }
        }
        return true;
    }
}
