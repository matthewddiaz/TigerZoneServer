package com.tigerzone.fall2016.scoring;

import com.tigerzone.fall2016.adapters.PlayerOutAdapter;
import com.tigerzone.fall2016.animals.Animal;
import com.tigerzone.fall2016.animals.Boar;
import com.tigerzone.fall2016.animals.Deer;
import com.tigerzone.fall2016.animals.Tiger;
import com.tigerzone.fall2016.area.*;
import com.tigerzone.fall2016.gamesystem.Player;
import com.tigerzone.fall2016.ports.CMDPromptPort;
import com.tigerzone.fall2016.ports.IOPort;
import com.tigerzone.fall2016.ports.TextFilePort;
import com.tigerzone.fall2016.tileplacement.FreeSpaceBoard;
import com.tigerzone.fall2016.tileplacement.tile.BoardTile;
import com.tigerzone.fall2016.tileplacement.tile.PlayableTile;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by matthewdiaz on 11/7/16.
 */
public class ScorerTest {

    private Player player1;
    private Player player2;
    private HashMap<Player,Integer> playerScores;
    private Scorer scorer;
    private  List<Player> players;

    private List<DenArea> denAreas;        // }
    private List<JungleArea> jungleAreas;  // |
    private List<LakeArea> lakeAreas;      // |---Needed for AreaManager
    private List<TrailArea> trailAreas;    // |
    private FreeSpaceBoard freeSpaceBoard; // }
    private AreaManager areaManager;
    private PlayerOutAdapter outAdapter;

    @org.junit.Before
    public void setUp() throws Exception {
        // Populate player scores, with player1 having the higher score
        player1 = new Player("Clay");
        player2 = new Player("Matt");
        players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        playerScores = new HashMap();
        playerScores.put(player1, 0);
        playerScores.put(player2, 0);

        // Create AreaManager
        freeSpaceBoard = new FreeSpaceBoard();

        TextFilePort textFilePort = new TextFilePort();
        LinkedList<PlayableTile> tileStack = textFilePort.createTiles();

        outAdapter = new CMDPromptPort(0, player1.getPlayerId(), player2.getPlayerId(), tileStack);
        // Create scorer
        scorer = new Scorer(players, outAdapter);
        areaManager = new AreaManager(scorer);
    }

    @org.junit.Test
    public void scoreDen() throws Exception {
        // Create a DenArea of size 3 whose owner is Clay
        DenArea denArea = new DenArea();
        Set<BoardTile> boardTiles = new HashSet<>();
        BoardTile boardTile = new BoardTile(new PlayableTile("JJJJ-"));
        BoardTile boardTile2 = new BoardTile(new PlayableTile("JJTJX"));
        BoardTile boardTile3 = new BoardTile(new PlayableTile("TLTT-"));
        boardTiles.add(boardTile);
        boardTiles.add(boardTile2);
        boardTiles.add(boardTile3);
        denArea.addBoardTile(boardTiles);
        Tiger tiger = new Tiger(player1);
        denArea.placePredator(tiger);
        scorer.score(denArea);

        // Clay should have 3 points
        assertTrue(scorer.getScore(player1) == 3);
    }

    @org.junit.Test
    public void scoreLake() throws Exception {
         //Create a LakeArea of size 3 with 2 unique animals, whose owner is player Clay a
        LakeArea lakeArea = new LakeArea();
        Set<BoardTile> boardTiles = new HashSet<>();
        BoardTile boardTile = new BoardTile(new PlayableTile("JJJJ-"));
        BoardTile boardTile2 = new BoardTile(new PlayableTile("JJTJX"));
        BoardTile boardTile3 = new BoardTile(new PlayableTile("TLTT-"));
        boardTiles.add(boardTile);
        boardTiles.add(boardTile2);
        boardTiles.add(boardTile3);
        lakeArea.addBoardTile(boardTiles);
        Animal boar = new Boar();
        Animal deer = new Deer();
        Tiger tiger = new Tiger(player1);

        lakeArea.addAnimal(boar);
        lakeArea.addAnimal(deer);
        lakeArea.placePredator(tiger);
        scorer.score(lakeArea);
        System.out.print(scorer.getScore(player1));
         //Clay should have 3*2*(1+2) = 18 points
        assertTrue(scorer.getScore(player1) == 18);
    }

    @org.junit.Test
    public void scoreTrail() throws Exception {
        // Create a TrailArea of size 5 with 1 unique animal, whose owner is player Clay
        TrailArea trailArea = new TrailArea();
        Set<BoardTile> boardTiles = new HashSet<>();
        BoardTile boardTile = new BoardTile(new PlayableTile("JJJJ-"));
        BoardTile boardTile2 = new BoardTile(new PlayableTile("JJTJX"));
        BoardTile boardTile3 = new BoardTile(new PlayableTile("TLTT-"));
        BoardTile boardTile4 = new BoardTile(new PlayableTile("JLLJX"));
        BoardTile boardTile5 = new BoardTile(new PlayableTile("TXLTT-"));
        boardTiles.add(boardTile);
        boardTiles.add(boardTile2);
        boardTiles.add(boardTile3);
        boardTiles.add(boardTile4);
        boardTiles.add(boardTile5);

        trailArea.addBoardTile(boardTiles);
        Animal boar = new Boar();
        trailArea.addAnimal(boar);
        Tiger tiger = new Tiger(player1);
        trailArea.placePredator(tiger);
        scorer.score(trailArea);

        // player1 should have 5+1 = 6 points and player2 should have 0 points
        System.out.print(scorer.getScore(player1));
        assertTrue(scorer.getScore(player1) == 6);
    }

    @org.junit.Test
    public void endGameScoring() throws Exception {
        // Create 2 dens, 2 lakes, 2 trails and 1 jungle
        // For each area in which 2 were created, 1 should be incomplete
        // The incomplete den should be of size 2 (including the den)
        // The incomplete lake should be of size 1
        // The incomplete trail should be of size 3
        // The jungle should be adjacent to the complete Den and the complete Lake
        // No area should have any prey
        // player1 should own each area

        // player1 should have 2 (dens) + 1 (lakes) + 3 (trails) + 8 (jungles) = 14 points
        // player2 should have 0 points
//        assertTrue(scorer.getScore(1) == 14);
//        assertTrue(scorer.getScore(2) == 0);
    }

    @org.junit.Test
    public void testAnnounceWinner() throws Exception {
        // player1 score = 32
        // player2 score = 15
        Map<Player, Integer> players = new HashMap<>();

        players.put(player1, 32);
        players.put(player2, 15);
        scorer.setPlayerScores(players);

        // player1 should be in the winners set, but player2 should not
        Set<String> winners = scorer.announceWinners();
        assertTrue(winners.contains(player1.getPlayerId()));
        assertFalse(winners.contains(player2.getPlayerId()));

        winners = scorer.announceWinners();
        assertTrue(winners.contains(player1.getPlayerId()));
    }

    @Test
    public void testAnnounceWinners() throws Exception {

    }

    @Test
    public void testGetScore() throws Exception {

    }
}