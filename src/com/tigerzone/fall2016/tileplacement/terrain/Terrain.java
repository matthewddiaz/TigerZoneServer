package com.tigerzone.fall2016.tileplacement.terrain;

/**
 * Created by Aidan on 11/7/2016.
 */
public abstract class Terrain implements TerrainVisitor {

    public abstract boolean accept(TerrainVisitor terrainVisitor);

    public abstract void accept(SegmentVisitor segmentVisitor);

    public boolean isFree(){
        return false;
    }

}