package pentos.g5.util;

import java.util.Set;
import java.util.HashSet;

import pentos.sim.Building;
import pentos.sim.Cell;

public class Pentominos implements pentos.sim.Sequencer {

    public static int [][][] indices = {
        {{0,0},{0,1},{0,2},{0,3},{0,4}},

        {{0,1},{0,2},{1,0},{1,1},{2,1}},
        {{0,0},{0,1},{1,1},{1,2},{2,1}},

        {{0,0},{0,1},{1,0},{2,0},{3,0}},
        {{0,0},{0,1},{1,1},{2,1},{3,1}},

        {{0,0},{0,1},{1,0},{1,1},{2,1}},
        {{0,0},{0,1},{1,0},{1,1},{2,0}},

        {{0,1},{1,1},{2,0},{2,1},{3,0}},
        {{0,0},{1,0},{2,0},{2,1},{3,1}},

        {{0,0},{0,1},{0,2},{1,1},{2,1}},

        {{0,0},{1,0},{1,1},{1,2},{0,2}},

        {{0,0},{0,1},{0,2},{1,2},{2,2}},

        {{0,0},{0,1},{1,1},{1,2},{2,2}},

        {{0,1},{1,0},{1,1},{1,2},{2,1}},

        {{0,1},{1,0},{1,1},{2,1},{3,1}},
        {{0,0},{1,0},{1,1},{2,0},{3,0}},

        {{0,0},{0,1},{1,1},{2,1},{2,2}},
        {{0,1},{0,2},{1,1},{2,1},{2,0}}
    };

    public int id = 0;
    public static int length = indices.length;

    public static Set <Building> buildings;

    public void init() {
    }

    public Building next() {
        Set<Cell> cells = new HashSet<Cell>();
        for(int i=0; i<5; ++i){
            System.out.print("("+indices[id][i][0]+","+indices[id][i][1]+") ");
            cells.add(new Cell(indices[id][i][0],indices[id][i][1]));
        }
        System.out.println();
        id = (id+1)%length;
        return new Building(cells.toArray(new Cell[cells.size()]), Building.Type.RESIDENCE);
    }

    public static void main(String[] args) {
        Pentominos p = new Pentominos();
        p.init();
        Building b = null;
        BuildingUtil bu;
        for( int i=0; i<p.length; ++i){
            b = p.next();
            bu = new BuildingUtil(b);
            System.out.println(bu.toString());
        }
        return;
    }

}
