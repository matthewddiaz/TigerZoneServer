package com.tigerzone.fall2016server.tournament;

import com.tigerzone.fall2016.tileplacement.tile.PlayableTile;
import com.tigerzone.fall2016server.server.Logger;
import com.tigerzone.fall2016server.tournament.tournamentplayer.TournamentPlayer;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lenovo on 11/17/2016.
 */
public class Round {
    List<TournamentPlayer> players;
    LinkedList<PlayableTile> tiles;
    private Challenge challenge;
    private int roundID;
    private int numOfMatches;
    private int numOfMatchesComplete = 0;
    private int currentRound = 0;
    private int numOfRounds;
    private RoundRobin roundRobin;

    List<Match> matches;


    public Round(List<Match> matches) {
        this.matches = matches;
        this.numOfMatches = matches.size();
    }


    public Round(Challenge challenge, List<Match> matches) {
        this.challenge = challenge;
        this.matches = matches;
        this.numOfMatches = matches.size();
        getChallengeInfo();
    }

    public Round(Challenge challenge, int roundID) {
        this.challenge = challenge;
        this.roundID = roundID;
        getChallengeInfo();
        matches = RoundRobin.listMatches(players, this.roundID, tiles);
        this.numOfMatches = matches.size();
        for (Match match: matches) {
            match.setRound(this);
        }
    }

    public void playRound() {
        sendMessageToPlayers();
        for (Match match : matches) {
            match.start();
            //match.playMatch();
        }
        //notifyComplete();
    }

    public void getChallengeInfo() {
        this.players = challenge.getPlayers();
        this.numOfRounds = challenge.getNumOfRounds();
        this.tiles = challenge.getTiles();
    }

    private void sendMessageToPlayers() {
        for (TournamentPlayer tournamentPlayer : players) {
            tournamentPlayer.sendMessageToPlayer("BEGIN ROUND " + roundID + " OF " + numOfRounds);
            Logger.beginRound(getChallenge().getTournamentID(),getChallenge().getChallengeID(),roundID,numOfRounds);
            System.out.println("BEGIN ROUND " + roundID + " OF " + numOfRounds);
        }
    }

    public void notifyComplete() {
        numOfMatchesComplete++;
        if(numOfMatchesComplete == numOfMatches) {
            for (TournamentPlayer tournamentPlayer : players) {
                tournamentPlayer.sendMessageToPlayer("END OF ROUND " + roundID + " OF " + numOfRounds);
            }
            Logger.endRound(getChallenge().getTournamentID(), getChallenge().getChallengeID(),roundID,numOfRounds);
            challenge.roundComplete();
        }
    }

    public void setPlayers(List<TournamentPlayer> players) {
        this.players = players;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public void setRoundID(int roundID) {
        this.roundID = roundID;
    }

    public void setNumOfRounds(int numOfRounds) {
        this.numOfRounds = numOfRounds;
    }

    public int getRoundID() {
        return roundID;
    }
}
