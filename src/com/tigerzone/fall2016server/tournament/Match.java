package com.tigerzone.fall2016server.tournament;

import com.tigerzone.fall2016.tileplacement.tile.PlayableTile;
import com.tigerzone.fall2016server.server.Logger;
import com.tigerzone.fall2016server.server.protocols.GameToClientMessageFormatter;
import com.tigerzone.fall2016server.tournament.tournamentplayer.PlayerStats;
import com.tigerzone.fall2016server.tournament.tournamentplayer.TournamentPlayer;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.*;

/**
 * Created by lenovo on 11/17/2016.
 */
public class Match extends Thread {
    /**
     * This class encapsulates a TournamentPlayer and a boolean gamePlayerTimedOut.
     * If the player times out when sending a response its gamePlayerTimedOut
     * attribute is set to true.
     * <p>
     * NOTE: This class is only used in playMatch()
     */
    class GamePlayer {
        private TournamentPlayer gamePlayer;
        private boolean gamePlayerTimedOut;

        GamePlayer(TournamentPlayer gamePlayer) {
            this.gamePlayer = gamePlayer;
            this.gamePlayerTimedOut = false;
        }

        public boolean isGamePlayerTimedOut() {
            return this.gamePlayerTimedOut;
        }

        public void setGamePlayerTimedOut() {
            this.gamePlayerTimedOut = true;
        }

        public void resetGamePlayerTimeOutStatus() {
            this.gamePlayerTimedOut = false;
        }

        public TournamentPlayer getGamePlayer() {
            return gamePlayer;
        }

        public void setGamePlayer(TournamentPlayer gamePlayer) {
            this.gamePlayer = gamePlayer;
        }

        public String readGamePlayerResponse() throws IOException {
            return this.gamePlayer.readPlayerMessage();
        }

        public String getGamePlayerUserName() {
            return this.gamePlayer.getUsername();
        }

        public void sendGameMessageToGamePlayer(String gameMessage) {
            this.gamePlayer.sendMessageToPlayer(gameMessage);
        }
    }

    private TournamentPlayer player1;
    private TournamentPlayer player2;
    private LinkedList<PlayableTile> tileStack;
    private Round round;
    private int matchID;
    private Game game1;
    private Game game2;
    private final int setUpTime = 10;
    private HashMap<Game, String> forfeitGameMap = new HashMap<>(); //This is used to keep track of which player forfeited for each game

    public Match(TournamentPlayer player1, TournamentPlayer player2, LinkedList<PlayableTile> tileStack) {
        this.tileStack = tileStack;
        this.player1 = player1;
        this.player2 = player2;
        game1 = new Game(1, player1, player2, tileStack, this);
        game2 = new Game(2, player2, player1, tileStack, this);
    }

    private void swapPlayers(GamePlayer game1player, GamePlayer game2player) {
        TournamentPlayer placeHolder = game1player.getGamePlayer();
        game1player.setGamePlayer(game2player.getGamePlayer());
        game2player.setGamePlayer(placeHolder);
    }

    public void run() {
        startMatch();
        playMatch();
    }

    private void startMatch() {
        sendMessageToPlayers();
        try {
            Thread.sleep(setUpTime * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void playMatch() {
        GamePlayer game1player = new GamePlayer(player1);
        GamePlayer game2player = new GamePlayer(player2);

        forfeitGameMap = new HashMap<>();
        int moveNumber = 1;
        game1.initializeIOport();
        game2.initializeIOport();

        while ((!game1.isOver() || !game2.isOver()) && moveNumber < 77) {
            game1player.resetGamePlayerTimeOutStatus();
            String game1PlayerResponse = acquireGamePlayerResponse(game1, moveNumber, game1player);

            game2player.resetGamePlayerTimeOutStatus();
            String game2PlayerResponse = acquireGamePlayerResponse(game2, moveNumber, game2player);

            verifyingGamePlayerResponse(game1, game1player, game1PlayerResponse);
            verifyingGamePlayerResponse(game2, game2player, game2PlayerResponse);

            //swap who is the active player in each game
            swapPlayers(game1player, game2player);
            //Increment move count
            moveNumber++;
        }
        notifyEndGameToPlayers();
        round.notifyComplete();
    }

    /**
     * This method returns the response from a GamePlayer.
     * If the a tournamentPlayer times out the returned response is a timed out message
     * plus the current tournamentPlayer timedOut attribute is set to true.
     * <p>
     * NOTE: A GamePlayer contains a TournamentPlayer & a timeOut boolean for that TournamentPlayer
     *
     * @param game
     * @param moveNumber
     * @param gamePlayer
     */
    private String acquireGamePlayerResponse(Game game, int moveNumber, GamePlayer gamePlayer) {
        String gamePlayerResponse = null;
        if (!game.isOver()) {
            String gamePlayerPrompt = GameToClientMessageFormatter.generateMessageToActivePlayer(game.getGameID(), 1, moveNumber, game.getCurrentTile());
            gamePlayer.sendGameMessageToGamePlayer(gamePlayerPrompt);
            //timeout to start
            try {
                gamePlayerResponse = gamePlayer.readGamePlayerResponse();
            } catch (SocketTimeoutException e) {
                gamePlayerResponse = GameToClientMessageFormatter.generateForfeitMessageToBothPlayers(game.getGameID(),
                        moveNumber, gamePlayer.getGamePlayerUserName(), "FORFEITED: TIMEOUT");
                gamePlayer.setGamePlayerTimedOut();
                System.out.println("Timeout in game " + game.getGameID() + ": " + gamePlayer.getGamePlayerUserName());
                forfeitGameMap.put(game, gamePlayer.getGamePlayerUserName());
            } catch (IOException e) {
                System.out.println("Caught IOException in match besides timeout (Player 2)");
                System.out.println("This is their input " + gamePlayerResponse);
                e.printStackTrace();
            }
        }
        return gamePlayerResponse;
    }

    /**
     * This method first checks if the GamePlayer has timed out for the current Game, if it has it sends the response
     * to the player and ends the current Game.
     * <p>
     * If the GamePlayer has not timed out this method receives the message from the game and sends the
     * game's message to the GamePlayer.
     * <p>
     * verifies a GamePlayer's response
     *
     * @param game
     * @param gamePlayer
     */
    private void verifyingGamePlayerResponse(Game game, GamePlayer gamePlayer, String gamePlayerResponse) {
        if (!game.isOver() && gamePlayerResponse != null) {
            if (gamePlayer.isGamePlayerTimedOut()) {
                sendGameMessage(gamePlayerResponse);
                game.endGame();
            } else {
                game.receiveTurn(gamePlayerResponse);
                String gameResponse = game.getResponse();
                if (gameResponse.contains("FORFEITED")) {
                    forfeitGameMap.put(game, gamePlayer.getGamePlayerUserName());
                }
                sendGameMessage(gameResponse);
            }
        }
    }

    private String tileToSTring(LinkedList<PlayableTile> tileStack) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        Iterator<PlayableTile> iter = tileStack.iterator();
        while (iter.hasNext()) {
            stringBuilder.append(" ");
            stringBuilder.append(iter.next().getTileString());
        }
        stringBuilder.append(" ]");
        return stringBuilder.toString();
    }

    private void sendStartMessage(TournamentPlayer player, String opponentUserName) {
        player.sendMessageToPlayer("YOUR OPPONENT IS PLAYER " + opponentUserName);
        player.sendMessageToPlayer("STARTING TILE IS TLTJ- AT 0 0 0");
        player.sendMessageToPlayer("THE REMAINING 76 TILES ARE " + tileToSTring(tileStack));
        player.sendMessageToPlayer("MATCH BEGINS IN " + setUpTime + " SECONDS");

        System.out.println("YOUR OPPONENT IS PLAYER " + opponentUserName);
        System.out.println("STARTING TILE IS TLTJ- AT 0 0 0");
        System.out.println("THE REMAINING 76 TILES ARE " + tileToSTring(tileStack));
        System.out.println("MATCH BEGINS IN " + setUpTime + " SECONDS");
    }

    private void sendMessageToPlayers() {
        sendStartMessage(player1, player2.getUsername());
        sendStartMessage(player2, player1.getUsername());
    }

    private boolean verifyGamePlayerScoring(Game game, int p1Score, int p2Score) {
        int actualP1Score = game.getPlayer1FinalScore();
        int actualP2Score = game.getPlayer2FinalScore();
        return ((p1Score == actualP1Score) && (p2Score == actualP2Score));
    }

    private String getFinalScoreFromPlayer(TournamentPlayer tournamentPlayer) {
        String playerGameScoreMessage;
        try {
            playerGameScoreMessage = tournamentPlayer.readPlayerMessage();
        } catch (SocketTimeoutException e) {
            playerGameScoreMessage = "FORFEIT: TIMEOUT " + tournamentPlayer.getUsername() + " did not reply in time";
        } catch (IOException e) {
            e.printStackTrace();
            playerGameScoreMessage = "FORFEIT: IOException error from " + tournamentPlayer.getUsername();
        }
        return playerGameScoreMessage;
    }

    private boolean verifyPlayersScoring(Game game, TournamentPlayer player) {
        String playerScoreMessage = getFinalScoreFromPlayer(player);
        //player did not reply with score message
        if(playerScoreMessage.startsWith("FORFEIT")){
            return false;
        }

        //parsing score message from player
        String[] parsedMessage = playerScoreMessage.split(" ");
        if (parsedMessage.length != 9) {
            forfeitGameMap.put(game, player.getUsername());
            return false;
        } else {
            int gameID;
            int playerScore;
            int playerScore2;
            try {
                gameID = Integer.parseInt(parsedMessage[1]);
                playerScore = Integer.parseInt(parsedMessage[5]);
                playerScore2 = Integer.parseInt(parsedMessage[8]);
                if (gameID != game.getGameID()) {
                    forfeitGameMap.put(game, player.getUsername());
                    return false;
                }
            } catch (NumberFormatException e) {
                forfeitGameMap.put(game, player.getUsername());
                return false;
            }

            if (!verifyGamePlayerScoring(game, playerScore, playerScore2)) {
                forfeitGameMap.put(game, player.getUsername());
                return false;
            }
        }
        return true;
    }

    private String scoringValidatingResponseToPlayer(Game game, TournamentPlayer player){
        String finalMessageToPlayer;
        if(!verifyPlayersScoring(game, player)){
            finalMessageToPlayer = "GAME "  + game.getGameID() + " PLAYER " + player.getUsername() + " FORFEITED: DOES NOT KNOW OUTCOME";
        }else if(forfeitGameMap.get(game) == player.getUsername()){
            TournamentPlayer p1 = game.getPlayer1();
            TournamentPlayer p2 = game.getPlayer2();
            String player1score = (forfeitGameMap.get(game) != p1.getUsername() ? "WIN" : "FORFEITED");
            String player2score = (forfeitGameMap.get(game) != p2.getUsername() ? "WIN" : "FORFEITED");

            finalMessageToPlayer = GameToClientMessageFormatter.generateGameOverMessage(game.getGameID(),
                    p1.getUsername(), p2.getUsername(), player1score, player2score);
        }else{
            finalMessageToPlayer = "GAME " + game.getGameID() + " PLAYER " +
                    player1.getUsername() + game.getPlayer1FinalScore() + " PLAYER " + player2.getUsername() + game.getPlayer2FinalScore();
        }
        return finalMessageToPlayer;
    }

    private void notifyEndGameToPlayers() {
        sendEndGameMessage(game1);
        String player1FinalMessageForGame1 = scoringValidatingResponseToPlayer(game1, player1);
        String player2FinalMessageForGame1 = scoringValidatingResponseToPlayer(game1, player2);
        player1.sendMessageToPlayer(player1FinalMessageForGame1);
        player2.sendMessageToPlayer(player2FinalMessageForGame1);

        sendEndGameMessage(game2);
        String player1FinalMessageForGame2 = scoringValidatingResponseToPlayer(game2, player1);
        String player2FinalMessageForGame2 = scoringValidatingResponseToPlayer(game2, player2);
        player1.sendMessageToPlayer(player1FinalMessageForGame2);
        player2.sendMessageToPlayer(player2FinalMessageForGame2);
    }

    private void sendEndGameMessage(Game game) {
        String endGameMessage = "GAME " + game.getGameID() + " OVER SEND OUTCOME";
        TournamentPlayer p1 = game.getPlayer1();
        TournamentPlayer p2 = game.getPlayer2();
        p1.sendMessageToPlayer(endGameMessage);
        p2.sendMessageToPlayer(endGameMessage);
    }

    private void updatePlayerStatistics(Game game, TournamentPlayer p1, TournamentPlayer p2) {
        Match m = game.getMatch();
        Round r = m.getRound();
        Challenge c = r.getChallenge();

        PlayerStats p1stats = p1.getStats();
        PlayerStats p2stats = p2.getStats();
        p1stats.setGamesPlayed(p1stats.getGamesPlayed() + 1);
        p2stats.setGamesPlayed(p2stats.getGamesPlayed() + 1);
        //p1 forfeited
        if (forfeitGameMap.get(game) != null && forfeitGameMap.get(game) == p1.getUsername()) {
            PlayerStats ps = p1.getStats();
            ps.setForfeits(ps.getForfeits() + 1);
            p2stats.setWinsByForfeit(p2stats.getWinsByForfeit() + 1);//Else, ps is player 2, P2 forfeited.
            p1stats.setLosses(p1stats.getLosses() + 1);
        }//p2 forfeited
        else if (forfeitGameMap.get(game) != null && forfeitGameMap.get(game) == p2.getUsername()) {
            PlayerStats ps = p2.getStats();
            ps.setForfeits(ps.getForfeits() + 1);
            p1stats.setWinsByForfeit(p1stats.getWinsByForfeit() + 1);//If our ps is the same as p1stats, it means P1 forfeited.
            p2stats.setLosses(p2stats.getLosses() + 1);
        } else {
            if (game.getPlayer1FinalScore() > game.getPlayer2FinalScore()) {
                p1stats.setWins(p1stats.getWins() + 1);
                p2stats.setLosses(p2stats.getLosses() + 1);
                p2stats.setLargestpointdifference(game.getPlayer2FinalScore(), game.getPlayer1FinalScore());
            } else if (game.getPlayer1FinalScore() == game.getPlayer2FinalScore()) {
                p1stats.setTies(p1stats.getTies() + 1);
                p2stats.setTies(p2stats.getTies() + 1);
            } else {
                p2stats.setWins(p2stats.getWins() + 1);
                p1stats.setLosses(p1stats.getLosses() + 1);
                p1stats.setLargestpointdifference(game.getPlayer1FinalScore(), game.getPlayer2FinalScore());
            }
        }

        p1stats.setTotalPoints(p1stats.getTotalPoints() + game.getPlayer1FinalScore());
        p2stats.setTotalPoints(p2stats.getTotalPoints() + game.getPlayer2FinalScore());

        p1stats.setOpponentTotalPoints(p1stats.getOpponentTotalPoints() + game.getPlayer2FinalScore());
        p2stats.setOpponentTotalPoints(p2stats.getOpponentTotalPoints() + game.getPlayer1FinalScore());

        Logger.endGame(c.getTournamentID(), c.getChallengeID(), r.getRoundID(), m.getMatchID(), game.getGameID(), p1, p2);
    }

    public TournamentPlayer getPlayer1() {
        return player1;
    }

    public TournamentPlayer getPlayer2() {
        return player2;
    }

    private void sendGameMessage(String playerMessage) {
        player1.sendMessageToPlayer(playerMessage);
        player2.sendMessageToPlayer(playerMessage);
    }

    public Round getRound() {
        return round;
    }

    public int getMatchID() {
        return matchID;
    }

    public void setMatchID(int matchID) {
        this.matchID = matchID;
    }

    public void setRound(Round round) {
        this.round = round;
    }

    public Game getGame1() {
        return game1;
    }

    public Game getGame2() {
        return game2;
    }
}
