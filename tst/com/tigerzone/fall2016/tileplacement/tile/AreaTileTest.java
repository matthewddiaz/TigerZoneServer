package com.tigerzone.fall2016.tileplacement.tile;

import com.tigerzone.fall2016.tileplacement.terrain.LakeTerrain;
import com.tigerzone.fall2016.tileplacement.terrain.JungleTerrain;
import javafx.geometry.Point2D;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Aidan on 11/7/2016.
 */
public class AreaTileTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void rotateCW() throws Exception {
        AreaTile areaTile = new AreaTile(
                new Edge(new LakeTerrain(), new LakeTerrain(), new LakeTerrain()),
                new Edge(new LakeTerrain(), new LakeTerrain(), new LakeTerrain()),
                new Edge(new JungleTerrain(), new JungleTerrain(), new JungleTerrain()),
                new Edge(new JungleTerrain(), new JungleTerrain(), new JungleTerrain()),
                new JungleTerrain());
        areaTile.rotateCW();
    }

    @Test
    public void rotateCCW() throws Exception {

    }

    @Test
    public void testGetOwnerID() throws Exception {

    }
}