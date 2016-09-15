package pentos.g5.util;

import java.util.*;

import pentos.sim.Land;
import pentos.sim.Cell;

public class LandUtil {

    public Land land;

    public LandUtil(Land l) {
        land = l;
    }

    public Pair getCup(BuildingUtil bu) {

        Pair[] buildingHull = bu.Hull();

        int numLoops = (land.side+1) / 2;
        int maxI = land.side - buildingHull[1].i;
        int maxJ = land.side - buildingHull[1].j;

        for(int loop=0; loop < numLoops ; ++loop ) {
            int i = loop;
            int j = 0;
            for(; j< maxI - loop; ++j) {
                // System.out.println(new Pair(i,j));
                if(land.buildable( bu.building, new Cell(i,j))) {
                    return new Pair(i, j);
                }
            }   // Traverse all in the top row
            for(; i< maxJ - loop; ++i) {
                // System.out.println(new Pair(i,j));
                if(land.buildable( bu.building, new Cell(i,j))) {
                    return new Pair(i, j);
                }
            }   // Traverse all in the left column
            for(; j>0; --j) {
                // System.out.println(new Pair(i,j));
                if(land.buildable( bu.building, new Cell(i,j))) {
                    return new Pair(i, j);
                }
            }
        }
        return new Pair(-1,-1);
    }

}