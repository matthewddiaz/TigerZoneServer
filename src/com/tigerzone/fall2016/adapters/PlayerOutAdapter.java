package com.tigerzone.fall2016.adapters;
import com.tigerzone.fall2016.gamesystem.Turn;
import com.tigerzone.fall2016.tileplacement.tile.AreaTile;

import java.util.List;
import java.util.Set;

public interface PlayerOutAdapter
{
    public void passTurn(Turn lastturn, AreaTile areatile);
    public void getTilesInOrder(List<AreaTile> allAreaTiles);
    public void notifyBeginGame(AreaTile areatile);
    public void notifyEndGame(Set<Integer> winners);
}