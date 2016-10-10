package pentos.sim;

import pentos.sim.Cell;
import pentos.sim.Building;
import pentos.sim.Land;
import java.util.HashSet;

/* Utility class that extends Land in order to access its protected methods */
public class LandBuilder extends Land {

    public LandBuilder(int side) {
        super(side);
    }

    public void copy(Land land) {
        this.land = new Cell[land.land.length][];
        int i = 0;
        int j = 0;
        for (Cell[] cells : land.land) {
            this.land[i] = new Cell[cells.length];
            for (Cell curCell : cells) {
                this.land[i][j] = new Cell(i, j);
                if (curCell.isRoad()) {
                    this.land[i][j].buildRoad();
                } else if (curCell.isWater()) {
                    this.land[i][j].buildWater();
                } else if (curCell.isPark()) {
                    this.land[i][j].buildPark();
                } else if (curCell.isFactory()) {
                    this.land[i][j].buildFactory();
                } else if (!curCell.isEmpty()){
                    this.land[i][j].buildResidence();
                }
                j++;
            }
            j = 0;
            i++;
        }
        // for (int i = 0; i < land.land.length; i++) {
        //     this.land[i] = Arrays.copyOf(land.land[i], land.land[i].length);
        // }
        this.road_network = new HashSet<Cell>();
        this.road_network.addAll(land.road_network);
    }

    // functions for simulator to build stuff
    public void buildWater(Cell q) {
	    super.buildWater(q);
    }
    public void buildRoad(Cell q) {
	    road_network.add(new Cell(q.i+1,q.j+1)); // re-index to allow borders
	    super.buildRoad(q);
    }
    public void buildPark(Cell q) {
	    super.buildPark(q);
    }
    public boolean validateRoads() {
	    return Cell.isConnected(road_network,side+2);
    }

    public int build(Building building, Cell q)  {
        int score = super.build(building, q);
        return score;
    }

    public String toString() {
        String type = "";
        String grid_str = "";
        for (int i = 0; i < this.side; i++) {
            for (int j = 0; j < this.side; j++) {
                if (land[i][j].isEmpty()) {
                    type = "E";
                } else if (land[i][j].isRoad()) {
                    type = "S";
                } else if (land[i][j].isWater()) {
                    type = "W";
                } else if (land[i][j].isPark()) {
                    type = "P";
                } else if (land[i][j].isFactory()) {
                    type = "F";
                } else {
                    type = "R";
                }
                grid_str += type + " ";
                if (j == this.side - 1) {
                    grid_str += "\n";
                }
            }
        }
        return grid_str;
    }
}
