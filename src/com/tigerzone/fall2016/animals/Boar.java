package com.tigerzone.fall2016.animals;

import com.tigerzone.fall2016.area.Area;

/**
 * Created by matthewdiaz on 11/11/16.
 */
public class Boar extends Prey {
    @Override
    public void addToArea(Area area){ area.addAnimal(this);}

    @Override
    public boolean isDeer(Deer deer) {
        return false;
    }

    @Override
    public boolean isBoar(Boar boar) {
        return true;
    }

    @Override
    public boolean isBuffalo(Buffalo buffalo) {
        return false;
    }
}
