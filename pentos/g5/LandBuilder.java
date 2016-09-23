package pentos.g5;

import pentos.sim.Cell;
import pentos.sim.Building;
import pentos.sim.Land;

/* Utility class that extends Land in order to access its protected methods */
public class LandBuilder extends Land {
    
    public LandBuilder(int side) {
        super(side);
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
}