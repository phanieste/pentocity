package pentos.g5;

import pentos.sim.Cell;
import pentos.sim.Building;
import pentos.sim.Land;
import pentos.sim.Move;

import pentos.g5.util.*;

import java.util.*;

import pentos.g5.util.BuildingUtil;
import pentos.g5.util.Pair;

public class Player implements pentos.sim.Player {

    private Random gen = new Random();
    private Set<Cell> allRoadCells = new HashSet<Cell>();

    public void init() { // function is called once at the beginning before play is called

    }


    private Building lastRequest;
    private Pair[] lastHull;
    private Pair lastBuildLocation;
    private int lastRotation;
    private int lastNumRoadCells;
    private int lastLoopLevel;

    private Move playCore(Building request, Land land) {
        // Build a residence

        BuildingUtil bu = new BuildingUtil( request );
        LandUtil lu = new LandUtil( land );
        Pair[] hull = bu.Hull();

        LandUtil.Direction d = LandUtil.Direction.INWARDS;
        if (request.type == Building.Type.FACTORY) {
            d = LandUtil.Direction.OUTWARDS;
        }

        Pair buildLocation = lu.getCup(bu, d);

        if((buildLocation.i < 0) || (buildLocation.j < 0)) {
            return new Move(false);
        }

        Cell startCell = new Cell(buildLocation.i, buildLocation.j);

        int rotation = 0;

        // DEBUG System.err.println( "Build:" + hull[0] + hull[1]);
        // DEBUG System.err.println( request.toString1() );
        // DEBUG System.err.println( "At:" + buildLocation );

        lastRequest = request;
        lastHull = hull;
        lastBuildLocation = buildLocation;
        lastRotation = rotation;
        lastLoopLevel = lu.lastLoopLevel;

        Set<Cell> shiftedCells = new HashSet<Cell>();
        for (Cell x : request.rotations()[rotation]){
            shiftedCells.add(new Cell(x.i+startCell.i, x.j+startCell.j));
        }            // build a road to connect this building to perimeter
        Set<Cell> roadCells = findShortestRoad(shiftedCells, land);

        Move move = new Move(true, request, startCell, rotation,
            new HashSet<Cell>(), new HashSet<Cell>(), new HashSet<Cell>());
        if( roadCells!=null ) {
            move.road = roadCells;
            allRoadCells.addAll(roadCells);
            lastNumRoadCells = roadCells.size();
        } else {
            return new Move(false);
        }


        // for(Building r : rotations ) {
        //     // System.out.println( "Rotation:\n" +r.toString1() );
        // }

        // Locate the location of first spiral where this home is possible.
        // * cannot build on a reserved piece of road that connects the inside

        // Build Connecting Road to location

        // Try to optimize with parks and ponds
        // * Do we need to put parks and ponds before the residence is built?

        return move;
    }

    private Move playFactory(Building request, Land land) {
        // Build a factory

        // Locate the location of first outward spiral where this Factory is possible.

        // Build Connecting Road to location

        return new Move(false);
    }



    public Move play(Building request, Land land) {

        Move move = playCore(request, land);

        if(move.accept) {
            if( lastNumRoadCells>0 ){
                System.out.println("Road:"+lastNumRoadCells);
                System.out.println("Building:" + lastHull[0] + lastHull[1]);
                System.out.println( lastRequest.toString1() );
                System.out.println( "At:" + lastBuildLocation );
                System.out.println( "Reached:" + lastLoopLevel );
                // try {
                //     System.in.read();
                // } catch (Exception e) {

                // }
                
            }
        } else {
            System.out.println("Rejecting Request");
        }

        return move;

    }

    // build shortest sequence of road cells to connect to a set of cells b
    private Set<Cell> findShortestRoad(Set<Cell> b, Land land) {
        System.out.println("findShortestRoad");
        Set<Cell> output = new HashSet<Cell>();
        boolean[][] checked = new boolean[land.side][land.side];
        Queue<Cell> queue = new LinkedList<Cell>();
        // add border cells that don't have a road currently
        Cell source = new Cell(Integer.MAX_VALUE,Integer.MAX_VALUE); // dummy cell to serve as road connector to perimeter cells
        for (int z=0; z<land.side; z++) {
            if (b.contains(new Cell(0,z)) || b.contains(new Cell(z,0)) || b.contains(new Cell(land.side-1,z)) || b.contains(new Cell(z,land.side-1))) //if already on border don't build any roads
                return output;
            if (land.unoccupied(0,z))
                queue.add(new Cell(0,z,source));
            if (land.unoccupied(z,0))
                queue.add(new Cell(z,0,source));
            if (land.unoccupied(z,land.side-1))
                queue.add(new Cell(z,land.side-1,source));
            if (land.unoccupied(land.side-1,z))
                queue.add(new Cell(land.side-1,z,source));
        }
        // add cells adjacent to current road cells
        for (Cell p : allRoadCells) {
            for (Cell q : p.neighbors()) {
                if (!allRoadCells.contains(q) && land.unoccupied(q) && !b.contains(q)) 
                    queue.add(new Cell(q.i,q.j,p)); // use tail field of cell to keep track of previous road cell during the search
            }
        }
        while (!queue.isEmpty()) {
            Cell p = queue.remove();
            checked[p.i][p.j] = true;
            for (Cell x : p.neighbors()) {
                if (b.contains(x)) { // trace back through search tree to find path
                    Cell tail = p;
                    while (!b.contains(tail) && !allRoadCells.contains(tail) && !tail.equals(source)) {
                        output.add(new Cell(tail.i,tail.j));
                        tail = tail.previous;
                    }
                    if (!output.isEmpty())
                        return output;
                }
                else if (!checked[x.i][x.j] && land.unoccupied(x.i,x.j)) {
                    x.previous = p;
                    queue.add(x);         
                } 

            }
        }
        if (output.isEmpty() && queue.isEmpty())
            return null;
        else
            return output;
    }

    // walk n consecutive cells starting from a building. Used to build a random field or pond. 
    private Set<Cell> randomWalk(Set<Cell> b, Set<Cell> marked, Land land, int n) {
        ArrayList<Cell> adjCells = new ArrayList<Cell>();
        Set<Cell> output = new HashSet<Cell>();
        for (Cell p : b) {
            for (Cell q : p.neighbors()) {
                if (land.isField(q) || land.isPond(q))
                    return new HashSet<Cell>();
                if (!b.contains(q) && !marked.contains(q) && land.unoccupied(q))
                    adjCells.add(q); 
            }
        }
        if (adjCells.isEmpty())
            return new HashSet<Cell>();
        Cell tail = adjCells.get(gen.nextInt(adjCells.size()));
        for (int ii=0; ii<n; ii++) {
            ArrayList<Cell> walk_cells = new ArrayList<Cell>();
            for (Cell p : tail.neighbors()) {
                if (!b.contains(p) && !marked.contains(p) && land.unoccupied(p) && !output.contains(p))
                    walk_cells.add(p);      
            }
            if (walk_cells.isEmpty()) {
                //return output; //if you want to build it anyway
                return new HashSet<Cell>();
            }
            output.add(tail);       
            tail = walk_cells.get(gen.nextInt(walk_cells.size()));
        }
        return output;
    }

}
