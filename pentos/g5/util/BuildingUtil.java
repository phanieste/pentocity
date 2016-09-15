package pentos.g5.util;

import java.util.*;

import pentos.sim.Building;
import pentos.sim.Cell;

public class BuildingUtil {

    public Building building;

    public BuildingUtil(Building b) {
        building = b;
    }

    public Pair[] Hull() {
        Iterator<Cell> it = building.iterator();
        Pair start = new Pair(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Pair end = new Pair(Integer.MIN_VALUE, Integer.MIN_VALUE);
        Pair[] hull = {start, end};

        while( it.hasNext() ) {
            Cell c = it.next();
            if(c.i < start.i) {
                start.i = c.i;
            } else if(c.i > end.i) {
                end.i = c.i;
            }
            if(c.j < start.j) {
                start.j = c.j;
            } else if(c.j > end.j) {
                end.j = c.j;
            }
        }
        return hull;
    }

}