package com.tigerzone.fall2016.tileplacement.tile;

import com.tigerzone.fall2016.tileplacement.terrain.Terrain;

import java.util.List;

/**
 * Created by lenovo on 11/13/2016.
 */
public class BoardTile {

    Terrain[] zones = new Terrain[9];

    public BoardTile(Terrain[] zones) {
        this.zones = zones;
    }

    public BoardTile(PlayableTile playableTile) {
        populateZones(playableTile);
    }

    public Terrain getZone(int zone) {
        int zoneTranslation = zone-1;
        return zones[zone];
    }

    public void setZone(int zone, Terrain terrain) {
        int zoneTranslation = zone-1;
        zones[zoneTranslation] = terrain;
    }

    private void populateZones(PlayableTile playableTile) {
        setZone(2, playableTile.getNorthFace());
        setZone(6, playableTile.getEastFace());
        setZone(8, playableTile.getSouthFace());
        setZone(4, playableTile.getWestFace());
    }

    public Terrain getNorthFace(){
        return zones[1];
    }

    public Terrain getEastFace(){
        return zones[5];
    }

    public Terrain getSouthFace(){
        return zones[7];
    }

    public Terrain getWestFace(){
        return zones[3];
    }

    //zones 2,6,4,8

}
