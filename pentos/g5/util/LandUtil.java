package pentos.g5.util;

import java.util.*;

import pentos.sim.Land;
import pentos.sim.Cell;

public class LandUtil {

    public enum Direction {INWARDS, OUTWARDS};

    public Land land;
    public int lastLoopLevel;

    public LandUtil(Land l) {
        land = l;
    }

    public Pair getDiag(BuildingUtil bu, Direction dir) {

        Pair[] buildingHull = bu.Hull();

        int numLoops = land.side;
        Looper looper;
        looper = new Looper(0, numLoops-1, 1);

        int loop;
        while(looper.hasNext()) {
            loop = looper.next();
            lastLoopLevel = loop;

            int i = loop;
            int j = 0;
            for (; j <= loop; j++) {
                i = loop - j;
                int actualI;
                int actualJ;
                if (dir == Direction.OUTWARDS) {
                    // finding cell that factory would be placed on
                    actualI = numLoops - i - buildingHull[1].i;
                    actualJ = numLoops - j - buildingHull[1].j;
                } else {
                    actualI = i;
                    actualJ = j;
                }
                if (land.buildable(bu.building, new Cell(actualI,actualJ))) {
                    return new Pair(actualI, actualJ);
                }
            }
        }
        return new Pair(-1, -1);
    }

    public Pair getCup(BuildingUtil bu, Direction dir) {

        Pair[] buildingHull = bu.Hull();

        int numLoops = (land.side+1) / 2;
        int maxI = land.side - buildingHull[1].i;
        int maxJ = land.side - buildingHull[1].j;

        Looper looper;
        if( LandUtil.Direction.OUTWARDS == dir ) {
            looper = new Looper(numLoops-1, 0, -1);
        } else {
            looper = new Looper(0, numLoops-1, 1);
        }

        // for(int loop=0; loop < numLoops ; ++loop ) {
        int loop;
        while(looper.hasNext()) {
            loop = looper.next();
            lastLoopLevel = loop;

            // DEBUG System.err.println("Trying to build at level: "+loop);
            int i = loop;
            int j = 0;
            for(; j< maxJ - loop; ++j) {
                // System.out.println(new Pair(i,j));
                if(land.buildable( bu.building, new Cell(i,j))) {
                    return new Pair(i, j);
                }
            }   // Traverse all in the top row
            for(; i< maxI - loop; ++i) {
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
