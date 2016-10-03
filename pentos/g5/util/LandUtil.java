package pentos.g5.util;

import java.util.*;

import pentos.sim.Land;
import pentos.sim.Cell;
import pentos.sim.Building;
import pentos.g5.Player;

public class LandUtil {

    public enum Direction {INWARDS, OUTWARDS};

    public Land land;
    public int lastLoopLevel;

    public Pair returnPair;
    public int returnRotation;

    public LandUtil(Land l) {
        land = l;
    }

    public Pair getDiag(BuildingUtil bu, Direction dir, Set<Pair> rejects) {

        Pair[] buildingHull = bu.Hull();

        int numLoops = land.side - 1;
        Looper looper;
        looper = new Looper(0, numLoops*2, 1);

        int loop;
        while(looper.hasNext()) {
            loop = looper.next();
            lastLoopLevel = loop;

            if (loop <= numLoops) {
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
                    if (actualI < 0 || actualJ < 0)
                        continue;
                    Pair loc = new Pair(actualI, actualJ);
                    if (!rejects.contains(loc) && land.buildable(bu.building, new Cell(actualI,actualJ))) {
                        return loc;
                    }
                }
            }
            else {
                int i = numLoops;
                int j = loop - numLoops;
                for (; j <= numLoops; j++) {
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
                    if (actualI < 0 || actualJ < 0)
                        continue;
                    Pair loc = new Pair(actualI, actualJ);
                    if ((!rejects.contains(loc) && land.buildable(bu.building, new Cell(actualI,actualJ)))) {
                        return loc;
                    }
                }
            }
        }
        return new Pair(-1, -1);
    }

    public boolean searchOptimalPlacement(BuildingUtil bu, Direction dir, Set<Pair> rejects, Player.Strategy strategy) {

        int count = 0;

        Pair[] buildingHull = bu.Hull();
        Pair size = new Pair(land.side, land.side);
        // size.subtract( buildingHull[1] );
        MinAndArgMin<Pair> indexWiseLocations = new MinAndArgMin<Pair>();
        MinAndArgMin<Integer> indexWiseRotations = new MinAndArgMin<Integer>();

        // MinAndArgMin<Pair> otherWiseLocations = new MinAndArgMin<Pair>();
        // MinAndArgMin<Integer> otherWiseRotations = new MinAndArgMin<Integer>();

        // MinAndArgMin<Pair> otherWiseLocations = new MinAndArgMin<Pair>();
        // MinAndArgMin<Integer> otherWiseRotations = new MinAndArgMin<Integer>();

        Building[] rotations = null;
        // Building r = null;

        List<Pair> allPairs;

        if( strategy == Player.Strategy.SPIRAL ) {
            allPairs = Looper2D.getSpiral( size.i, size.j, dir==Direction.OUTWARDS );
        } else {
            allPairs = Looper2D.getCorner( size.i, size.j, dir==Direction.OUTWARDS );
        }

        // for( Pair p : Looper2D.getSpiral( size.i, size.j, dir==Direction.OUTWARDS )) {
        for(Pair p : allPairs) {
            rotations = bu.building.rotations();
            for( int r=0; r < rotations.length; ++r ) {
                if(!rejects.contains(p) && land.buildable(rotations[r], new Cell(p.i, p.j)) ) {
                    indexWiseLocations.consider( count, p);
                    indexWiseRotations.consider( count, r);
                }
            }
        }

        if(indexWiseLocations.idxMin >= 0){
            returnPair = indexWiseLocations.argMin;
            returnRotation = indexWiseRotations.argMin;
            return true;
        }
        return false;
    }

}
