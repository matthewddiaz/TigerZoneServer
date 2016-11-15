package com.tigerzone.fall2016.tileplacement;

import com.sun.javafx.geom.Point2D;
import com.tigerzone.fall2016.area.AreaManager;
import com.tigerzone.fall2016.tileplacement.tile.BoardTile;
import com.tigerzone.fall2016.tileplacement.tile.PlayableTile;

import java.util.HashMap;

/**
 * Created by lenovo on 11/12/2016.
 */
public class Board {
    private AreaManager areaManager;

    private HashMap<Point2D, BoardTile> board = new HashMap<>();

    public void placeTile(Point2D position, BoardTile tile) {
        if (board.get(position) == null) {
            board.put(position, tile);

            getleftAdjacentTile(position);
            getRightAdjacentTile(position);
            getAboveAdjacentTile(position);
            getBelowAdjacentTile(position);

        } else {
            System.out.println("there is already a tile there"); // TODO: 11/12/2016 alert GM that an illegal move has been made?
        }
    }

    //at some point perhaps we just go through each tile and check adjacent

    public BoardTile getTile(Point2D boardPosition) {
        return board.get(boardPosition);
    }

    // TODO: 11/12/2016 move these methods to another class? See if matt can help generate tests here

    public BoardTile getRightAdjacentTile(Point2D boardPosition) {
        Point2D adjacentTilePosition = new Point2D(boardPosition.x + (float)1.0, boardPosition.y);
        //return board.get(adjacentTilePosition); // TODO: 11/12/2016 what if no tile at this position?
        return getTile(adjacentTilePosition);
    }

    public BoardTile getleftAdjacentTile(Point2D boardPosition) {
        Point2D adjacentTilePosition = new Point2D(boardPosition.x - (float)1.0, boardPosition.y);
        //return board.get(adjacentTilePosition);
        return getTile(adjacentTilePosition);
    }

    public BoardTile getAboveAdjacentTile(Point2D boardPosition) {
        Point2D adjacentTilePosition = new Point2D(boardPosition.x, boardPosition.y + (float)1.0);
        //return board.get(adjacentTilePosition);
        return getTile(adjacentTilePosition);
    }

    public BoardTile getBelowAdjacentTile(Point2D boardPosition) {
        Point2D adjacentTilePosition = new Point2D(boardPosition.x, boardPosition.y - (float)1.0);
        //return board.get(adjacentTilePosition);
        return getTile(adjacentTilePosition);

    }


}
