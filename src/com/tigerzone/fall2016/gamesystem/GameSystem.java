package com.tigerzone.fall2016.gamesystem;

import com.tigerzone.fall2016.adapters.PlayerInAdapter;
import com.tigerzone.fall2016.adapters.PlayerOutAdapter;
import com.tigerzone.fall2016.area.AreaManager;
import com.tigerzone.fall2016.ports.TextFilePort;
import com.tigerzone.fall2016.scoring.Scorer;
import com.tigerzone.fall2016.tileplacement.FreeSpaceBoard;
import com.tigerzone.fall2016.tileplacement.tile.AreaTile;
import com.tigerzone.fall2016.tileplacement.tile.Tile;

import javafx.geometry.Point2D;

import java.util.*;

public class GameSystem implements PlayerInAdapter
{
    // Game State
    private TileStack ts;
    private AreaTile origintile;
    private Player player1;
    private Player player2;
    private FreeSpaceBoard fsb;
    private Scorer scorer;
    private Turn currentTurn;
    // private AreaManager am;

    // Communication
    private PlayerOutAdapter outAdapter;
    boolean gameInProgress;
    boolean waitingForInput;
    private boolean timeout;
    private int timeoutLength; // time in milliseconds until forfeit

    public GameSystem(int seed) {
        initializeGame(seed);
        gameInProgress = true;
        waitingForInput = true;
        timeout = false;
        timeoutLength = 1000; // in milliseconds
    }

    /**
     * Called by an adapter at the system's boundary to provide the current player's turn
     *
     * @param t  Turn object holding the player whose turn it is, their tile placement, and predator placement
     */
    public void acceptTurn(Turn t)
    {
        waitingForInput = false;
        this.currentTurn = t;
    }

    /**
     * Creates the objects necessary to play a game, shuffles the tile stack,
     * and waits 15 seconds before starting the game
     *
     * @param seed  used to generate a unique Tile order for a game
     */
    public void initializeGame(int seed)
    {
        player1 = new Player(1);
        player2 = new Player(2);

        fsb = new FreeSpaceBoard();

        ts = new TileStack(seed, new TextFilePort());
        origintile = ts.pop();
        ts.shuffle(); //Shuffle

        // pass the entire contents of the TileStack to the outAdapter
        // wait 15 seconds before soliciting the first move
        //TODO: Add getCompleteTileList() to TileStack
        outAdapter.getTilesInOrder(new ArrayList<AreaTile>());
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        startGame();
                    }
                }, 15000
        );
    }

    //TODO: Refactor functionality into private methods

    /**
     * The main game loop. One iteration of the game loop encapsulates one turn.
     *
     */
    public void startGame()
    {
        // Game Loop
        while (gameInProgress) {
            // get turn
            new Timer().schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            if (waitingForInput) {
                                forfeit();
                                waitingForInput = false;
                                timeout = true;
                            }
                        }
                    }, timeoutLength
            );

            while (waitingForInput) {
                // do nothing
            }

            //TODO: Refactor...can't put break statement inside of the TimerTask
            if (timeout) {
                break;
            }

            // check if tile is unplayable
            AreaTile currentTile = currentTurn.getTile();
            if ( fsb.needToRemove(currentTile) ) {
                // prompt player to:
                //   1. pass
                //   2. pick up one of their previously placed Tigers and return it to the supply
                //   3. put another tiger from their supply on top of a tiger they previously placed
            }

            // place tile
            Point2D position = currentTurn.getPosition();
            fsb.placeTile(position, currentTile);
            //TODO: Add forfeit check

            // update areas

            // notify outAdapter with results

            AreaTile nextTile = ts.pop();
            // If there are no tiles remaining, end the game
            if (nextTile == null) {
                gameInProgress = false;
            }
        }
        Set<Integer> winners = scorer.announceWinners();
        outAdapter.notifyEndGame(winners);
        gameInProgress = false;
    }

    //========== Helper Methods ===========//

    // Notifies the outAdapter that the player whose not currently taking their turn is the winner
    // (The player whose turn it currently is forfeits)
    private void forfeit() {
        int currentPlayerID = currentTurn.getPlayerID();
        int player1ID = player1.getPlayerId();
        int player2ID = player2.getPlayerId();

        int winningPlayerID = (currentPlayerID == player1ID) ? player2ID : player1ID;
        Set<Integer> winners = new HashSet<>();
        winners.add(winningPlayerID);

        // Change this to a separate method notifying forfeit?
        outAdapter.notifyEndGame(winners);
    }

}