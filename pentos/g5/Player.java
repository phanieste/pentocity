package pentos.g5;

import pentos.sim.Cell;
import pentos.sim.Building;
import pentos.sim.Land;
import pentos.sim.Move;

import pentos.g5.util.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.Properties;

import pentos.g5.util.BuildingUtil; import pentos.g5.util.Pair;

public class Player implements pentos.sim.Player {

    public enum Strategy {SPIRAL, CORNERS};

    // temporary flag for which strategy to use
    private static Strategy STRATEGY;
    private static final String CONFIG_FILE_NAME = "player.cfg";

    // number of location rejections allowed before request rejected
    private static final int MAX_REJECTS = 500;

    private Random gen = new Random();
    private Set<Cell> allRoadCells = new HashSet<Cell>();
    private Set<Cell> allBonusCells = new HashSet<Cell>();

    public void init() { // function is called once at the beginning before play is called
        if( !getProperties() ) {
            STRATEGY = Strategy.CORNERS;
            setProperties();
        }
    }

    public boolean getProperties() {
        Properties prop = new Properties();
        InputStream input = null;
        boolean returnVal;
        try {
            input = getClass().getResourceAsStream(CONFIG_FILE_NAME);
            // input = new FileInputStream(CONFIG_FILE_NAME);
            prop.load(input);
            STRATEGY = Strategy.valueOf(prop.getProperty("strategy"));
            returnVal = true;
        } catch (IOException e) {
            e.printStackTrace();
            returnVal = false;
        } finally {
            if(input!=null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return returnVal;
    }

    public void setProperties() {
        Properties prop = new Properties();
        OutputStream output = null;
        try {
            output = new FileOutputStream("player.cfg");
            prop.setProperty("strategy", STRATEGY.name());
            prop.store(output, null);
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if(output!=null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Variables to analyse

    private int numRequests = 0;
    private int lastRotation;
    private int lastNumRoadCells;
    private int lastLoopLevel;
    private Building lastRequest;
    private Pair[] lastHull;
    private Pair lastBuildLocation;
    private String lastBonusType; // track if last bonus was park or water

    private Move playCore(Building request, Land land) {
        // Build a residence

        BuildingUtil bu = new BuildingUtil( request );
        LandUtil lu = new LandUtil( land );
        Pair[] hull = bu.Hull();

        LandUtil.Direction d = LandUtil.Direction.INWARDS;
        if (request.type == Building.Type.FACTORY) {
            d = LandUtil.Direction.OUTWARDS;
        }

        Set<Pair> rejectLocations = new HashSet<Pair>();
        Set<Cell> roadCells = null;
        Set<Cell> bonusCells = null;
        Move move = new Move(false);

        while (roadCells == null && rejectLocations.size() < MAX_REJECTS) {
            Pair buildLocation;
            int rotation = 0;

            if( lu.searchOptimalPlacement(bu, d, rejectLocations) ) {
                buildLocation = lu.returnPair;
                rotation = lu.returnRotation;
            } else {
                lastRequest = request;
                return new Move(false);
            }

            Cell startCell = new Cell(buildLocation.i, buildLocation.j);

            lastRequest = request;
            lastHull = hull;
            lastBuildLocation = buildLocation;
            lastRotation = rotation;
            lastLoopLevel = lu.lastLoopLevel;

            Set<Cell> shiftedCells = new HashSet<Cell>();
            for (Cell x : request.rotations()[rotation]){
                shiftedCells.add(new Cell(x.i+startCell.i, x.j+startCell.j));
            }            // build a road to connect this building to perimeter

            move = new Move(true, request, startCell, rotation,
                new HashSet<Cell>(), new HashSet<Cell>(), new HashSet<Cell>());
            if (bonusCells != null && !bonusCells.isEmpty()) {
                if (bonusCells.iterator().next().isWater()) {
                    move.water = bonusCells;
                    lastBonusType = "POND";
                } else {
                    move.park = bonusCells;
                    lastBonusType = "FIELD";
                }
                allBonusCells.addAll(bonusCells);
            }
            
            roadCells = findShortestRoad(shiftedCells, land);
            if( roadCells!=null ) {
                move.road = roadCells;
                allRoadCells.addAll(roadCells);
                lastNumRoadCells = roadCells.size();
            } else {
                if (bonusCells != null)
                    allBonusCells.removeAll(bonusCells);
                rejectLocations.add(buildLocation);
                move = new Move(false);
            }
        }

        return move;
    }

    public Move play(Building request, Land land) {
        numRequests += 1;
        Move move = playCore(request, land);
        if(!move.accept) {
            System.out.println("Request number      : "+numRequests);
            System.out.println("Road cells built    : "+lastNumRoadCells);
            System.out.println("At                  : " + lastBuildLocation );
            System.out.println("Reached             : " + lastLoopLevel );
            System.out.println("Building            : " + lastHull[0] + lastHull[1]);
            System.out.println("Status              : Rejecting Request");
            System.out.println( BuildingUtil.toString(lastRequest) );
        }
        return move;
    }

    // check if cell is on perimeter
    private boolean isOnPerimeter(Cell c, Land land) {
        return (c.i == 0 || c.j == 0 || c.i == land.side-1 || c.j == land.side-1);
    }

    // check if cell is truly unoccupied (also checks any placed bonus cells)
    private boolean isUnoccupied(int i, int j, Land land) {
        return (land.unoccupied(i,j) && !allBonusCells.contains(new Cell(i,j)));
    }

    // find the nearest bonus cell
    private Set<Cell> findNearestBonus(Set<Cell> b, Land land) {
        // System.out.println("findNearestBonus");
        Set<Cell> output = new HashSet<Cell>();
        boolean[][] checked = new boolean[land.side][land.side];
        Queue<Cell> queue = new LinkedList<Cell>();

        for (Cell p : b) {
            if (isOnPerimeter(p,land))
                continue;
            for (Cell q : p.neighbors()) {
                if (allBonusCells.contains(q))
                    return output;
                if (land.unoccupied(q.i,q.j)) {
                    q.previous = p;
                    queue.add(q);
                }
            }
        }

        // find any nearby bonus cells
        while (!queue.isEmpty()) {
            Cell p = queue.remove();
            if (checked[p.i][p.j])
                continue;
            checked[p.i][p.j] = true;
            if (isOnPerimeter(p,land)) {
                continue;
            }
            for (Cell x : p.neighbors()) {
                if (allBonusCells.contains(x)) {
                    Cell.Type type;
                    if (land.isPond(x))
                        type = Cell.Type.WATER;
                    else
                        type = Cell.Type.PARK;
                    Cell tail = p;
                    output.add(new Cell(p.i,p.j,type));
                    while (!b.contains(tail)) {
                        output.add(new Cell(tail.i,tail.j,type));
                        tail = tail.previous;
                    }
                    if (!output.isEmpty())
                        return output;
                }
            }
        }
        if (output.isEmpty() && queue.isEmpty()) {
            return null;
        }
        else
            return output;
    }

    private boolean safeToBuild(int i, int j, Land land, Set<Cell> b, Set<Cell> occupied) {
        Cell c;
        if (i >= 0 && j >= 0 && i < land.side && j < land.side)
           c = new Cell(i,j);
        else
            return false;
        return (land.unoccupied(c) && !b.contains(c) && !occupied.contains(c));
    }

    private Set<Cell> buildBonusGroup(Set<Cell> b, Pair location, Land land, BuildingUtil bu) {
        // System.out.println("buildBonusGroup");
        Set<Cell> output = new HashSet<Cell>();

        Cell.Type type;
        if (lastBonusType == "POND")
            type = Cell.Type.PARK;
        else
            type = Cell.Type.WATER;

        Iterator<Cell> it = bu.building.iterator();
        Pair corner = bu.LowerRightCorner();
        Cell c = new Cell(location.i+corner.i, location.j+corner.j);
        while (output.size() < 4) {
            if (safeToBuild(c.i, c.j+1, land, b, output)) {
                c = new Cell(c.i, c.j+1, type);
                output.add(c);
            } else if (safeToBuild(c.i+1, c.j, land, b, output)) {
                c = new Cell(c.i+1, c.j, type);
                output.add(c);
            } else if (safeToBuild(c.i-1, c.j, land, b, output)) {
                c = new Cell(c.i-1, c.j, type);
                output.add(c);
            } else if (safeToBuild(c.i, c.j-1, land, b, output)) {
                c = new Cell(c.i, c.j-1, type);
                output.add(c);
            } else {
                output.clear();
                if (it.hasNext()) {
                    Cell next = it.next();
                    if (new Pair(next.i,next.j) == corner) {
                        if (it.hasNext()) {
                            next = it.next();
                        } else {
                            return null;
                        }
                    }
                    c = new Cell(location.i+next.i, location.j+next.j);
                } else {
                    return null;
                }
            }
        }

        if (output.size() >= 4) {
            return output;
        } else {
            return null;
        }
    }

    private Set<Cell> findShortestRoadAlt(Set<Cell> b, Land land) {
        System.out.println("findShortestRoad");
        Set<Cell> output = new HashSet<Cell>();
        boolean[][] checked = new boolean[land.side][land.side];
        Queue<Cell> queue = new LinkedList<Cell>();

        for (Cell p : b) {
            if (isOnPerimeter(p,land))
                return output;
            for (Cell q : p.neighbors()) {
                if (allRoadCells.contains(q))
                    return output;
                if (land.unoccupied(q.i,q.j)) {
                    q.previous = p;
                    queue.add(q);
                }
            }
        }

        while (!queue.isEmpty()) {
            Cell p = queue.remove();
            if (checked[p.i][p.j])
                continue;
            checked[p.i][p.j] = true;
            if (isOnPerimeter(p,land)) {
                Cell tail = p;
                output.add(new Cell(p.i,p.j));
                while (!b.contains(tail)) {
                    output.add(new Cell(tail.i,tail.j));
                    tail = tail.previous;
                }
                if (!output.isEmpty())
                    return output;
            }
            else {
                for (Cell x : p.neighbors()) {
                    if (allRoadCells.contains(x)) {
                        Cell tail = p;
                        output.add(new Cell(p.i,p.j));
                        while (!b.contains(tail)) {
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
        }
        if (output.isEmpty() && queue.isEmpty()) {
            return null;
        }
        else
            return output;
    }

    // build shortest sequence of road cells to connect to a set of cells b
    private Set<Cell> findShortestRoad(Set<Cell> b, Land land) {
        // System.out.println("findShortestRoad");
        Set<Cell> output = new HashSet<Cell>();
        boolean[][] checked = new boolean[land.side][land.side];
        Queue<Cell> queue = new LinkedList<Cell>();
        // add border cells that don't have a road currently
        Cell source = new Cell(Integer.MAX_VALUE,Integer.MAX_VALUE); // dummy cell to serve as road connector to perimeter cells
        for (int z=0; z<land.side; z++) {
            if (b.contains(new Cell(0,z)) || b.contains(new Cell(z,0)) || b.contains(new Cell(land.side-1,z)) || b.contains(new Cell(z,land.side-1))) //if already on border don't build any roads
                return output;
            if (isUnoccupied(0,z,land))
                queue.add(new Cell(0,z,source));
            if (isUnoccupied(z,0,land))
                queue.add(new Cell(z,0,source));
            if (isUnoccupied(z,land.side-1,land))
                queue.add(new Cell(z,land.side-1,source));
            if (isUnoccupied(land.side-1,z,land))
                queue.add(new Cell(land.side-1,z,source));
        }
        // add cells adjacent to current road cells
        for (Cell p : allRoadCells) {
            for (Cell q : p.neighbors()) {
                if (b.contains(q)) {
                    return output; // adjacent to a road cell already
                } else if (!allRoadCells.contains(q) && isUnoccupied(q.i,q.j,land)) {
                    queue.add(new Cell(q.i,q.j,p)); // use tail field of cell to keep track of previous road cell during the search
                }
            }
        }
        while (!queue.isEmpty()) {
            Cell p = queue.remove();
            if (checked[p.i][p.j])
                continue;
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
                else if (!checked[x.i][x.j] && isUnoccupied(x.i,x.j,land)) {
                    x.previous = p;
                    queue.add(x);
                } 

            }
        }
        if (output.isEmpty() && queue.isEmpty()) {
            return null;
        }
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
