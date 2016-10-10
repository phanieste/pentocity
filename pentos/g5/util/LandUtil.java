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

    public boolean searchOptimalPlacement(BuildingUtil bu, Direction dir, Set<Pair> rejects, Player.Strategy strategy) {

        int count = 0;

        Pair[] buildingHull = bu.Hull();
        Pair size = new Pair(land.side, land.side);
        // size.subtract( buildingHull[1] );
        MinAndArgMin<Pair> indexWiseLocations = new MinAndArgMin<Pair>();
        MinAndArgMin<Integer> indexWiseRotations = new MinAndArgMin<Integer>();

        MinAndArgMin<Pair> smoothnessWiseLocations = new MinAndArgMin<Pair>();
        MinAndArgMin<Integer> smoothnessWiseRotations = new MinAndArgMin<Integer>();

        MinAndArgMin<Pair> roadnessWiseLocations = new MinAndArgMin<Pair>();
        MinAndArgMin<Integer> roadnessWiseRotations = new MinAndArgMin<Integer>();

        Building[] rotations = null;
        // Building r = null;

        List<Pair> allPairs;

        if(strategy == Player.Strategy.SPIRAL) {
            int minSide = Math.min(buildingHull[1].i, buildingHull[1].j);
            allPairs = Looper2D.getSpiral( size.i - minSide, size.j - minSide, dir==Direction.OUTWARDS );
        } else if(strategy == Player.Strategy.CORNERS) {
            allPairs = Looper2D.getCorner( size.i, size.j, dir==Direction.OUTWARDS );
        } else {
            allPairs = Looper2D.getBlocks( size.i, size.j, dir==Direction.OUTWARDS );
        }

        // for( Pair p : Looper2D.getSpiral( size.i, size.j, dir==Direction.OUTWARDS )) {
        for(Pair p : allPairs) {
            rotations = bu.building.rotations();
            for( int r=0; r < rotations.length; ++r ) {
                if(!rejects.contains(p) && land.buildable(rotations[r], new Cell(p.i, p.j)) ) {
                    int smoothScore = this.smoothness(rotations[r], p);
                    int roadConnectedness = this.roadness(rotations[r], p);
                    // DEBUG System.out.println("smooth score = " + smoothScore + " for rotation " + r);
                    smoothnessWiseLocations.consider(smoothScore, p);
                    smoothnessWiseRotations.consider(smoothScore, r);
                    roadnessWiseLocations.consider(roadConnectedness, p);
                    roadnessWiseRotations.consider(roadConnectedness, r);
                    indexWiseLocations.consider( count, p);
                    indexWiseRotations.consider( count, r);
                }
            }
        }

        if(smoothnessWiseLocations.idxMin >= 0){
            returnPair = smoothnessWiseLocations.argMin;
            returnRotation = smoothnessWiseRotations.argMin;
            return true;
        }
        return false;
    }

    private int smoothness(Building bu, Pair p) {
        // for (Cell q : bu) {
        //     if (!this.land.unoccupied(p.i + q.i, p.j + q.j))
        //         return -1;
        //     else
        //         q = new Cell(p.i + q.i, p.j + q.j, (Type) bu.getType());
        // }
        Iterator<Cell> iter = bu.iterator();
        Set<Cell> buildingCells = new HashSet<Cell>();
        while (iter.hasNext()) {
            Cell bCell = iter.next();
            buildingCells.add(new Cell(bCell.i + p.i, bCell.j + p.j));
        }

        int score = 0;
        for (int i = 0 ; i < land.side ; i++) {
    	    for (int j = 0 ; j < land.side ; j++) {
    		    Cell curr = new Cell(i, j);

                if (buildingCells.contains(curr)) {
                    Cell[] neighbors = curr.neighbors();
                    for (Cell neighbor : neighbors) {
                        if (!buildingCells.contains(neighbor)) {
                            if (neighbor.isEmpty()) {
                                score++;
                            }
                        }
                    }
                } else if (!this.land.unoccupied(curr)) {
                    Cell[] neighbors = curr.neighbors();
                    for (Cell neighbor : neighbors) {
                        if (neighbor.isEmpty() && !buildingCells.contains(neighbor)) {
                            score++;
                        }
                    }
                }
    	    }
        }
        return score;
    }
    
    private int roadness(Building bu, Pair p) {
        
        Iterator<Cell> iter = bu.iterator();
        Set<Cell> buildingCells = new HashSet<Cell>();
        while (iter.hasNext()) {
            Cell bCell = iter.next();
            buildingCells.add(new Cell(bCell.i + p.i, bCell.j + p.j));
        }

        int score = 0;
        for (int i = 0 ; i < land.side ; i++) {
    	    for (int j = 0 ; j < land.side ; j++) {
    		    Cell curr = new Cell(i, j);

                if (buildingCells.contains(curr)) {
                    Cell[] neighbors = curr.neighbors();
                    for (Cell neighbor : neighbors) {
                        if (!buildingCells.contains(neighbor)) {
                            if (neighbor.isRoad()) {
                                score++;
                            }
                        }
                    }
                }
                // } else if (!this.land.unoccupied(curr)) {
                //     Cell[] neighbors = curr.neighbors();
                //     for (Cell neighbor : neighbors) {
                //         if (neighbor.isEmpty() && !buildingCells.contains(neighbor)) {
                //             score++;
                //         }
                //     }
                // }
    	    }
        }
        return score;
    }

}
