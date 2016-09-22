package pentos.g5.util;

import java.util.Set;
import java.util.HashSet;

import pentos.sim.Land;
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

    public int id = -1;
    public static int length = indices.length;

    public static Set <Building> buildings;

    public void init() {

    }

    public Building current() {
        Set<Cell> cells = new HashSet<Cell>();
        for(int i=0; i<5; ++i){
            // System.out.print("("+indices[id][i][0]+","+indices[id][i][1]+") ");
            cells.add(new Cell(indices[id][i][0],indices[id][i][1]));
        }
        // System.out.println();
        return new Building(cells.toArray(new Cell[cells.size()]), Building.Type.RESIDENCE);        
    }

    public Building next() {
        id = (id+1)%length;
        return current();
    }

    public static void packTogether(Building b1, Building b2) {
        // init a new land of size 10x10
        Land l = null;
        Pair[] hull1 = null;
        Pair[] hull2 = null;
        Pair[] hull = null;
        int hullSize = -1;
        int roadSide = -1;
        char buf[][] = new char [10][10];

        for( Building r1 : b1.rotations() ) {
            l = new Land(10);

            l.build( r1, new Cell(0,0) );

            hull1 = BuildingUtil.Hull(r1);
            hull2 = null;
            hull = null;
            hullSize = -1;
            for( Building r2 : b2.rotations() ) {
                StringUtil.init(buf, ' ');
                Pair offset = null;
                for( int i = 0; i < 10; ++i) {
                    System.out.print(i+",");
                    offset = new Pair(0,i);
                    if( l.buildable(r2, new Cell(0,i)) ) {
                        hull2 = BuildingUtil.Hull(r2);
                        break;
                    }
                }
                System.out.println( ":"+offset.i+","+offset.j );
                if(hull2 != null){
                    hull2[0].add(offset);
                    hull2[1].add(offset);
                    hull = Pair.hull( hull1, hull2 );
                    hullSize = (1+hull[1].i) * (1+hull[1].j);
                    roadSide = hull[1].j - hull[0].j;
                    // System.out.println( BuildingUtil.toString(r1) + BuildingUtil.toString(r2));
                    BuildingUtil.toString1(r2, buf, '2', offset);
                    BuildingUtil.toString1(r1, buf, '1');
                    System.out.println( "|"+StringUtil.toString(buf, "|\n|")+"|" );
                    System.out.println(hull1[1].toString() + hull2[1].toString() + hull[1].toString() + ", " + hullSize + ", " + roadSide) ;
                }
            }
        }
    }

    public static void main(String[] args) {
        Pentominos p = new Pentominos();
        p.init();
        int b_id = -1;
        Building b = null;
        BuildingUtil bu;
        for( int i=0; i<p.length; ++i){
            b = p.next();
            b_id = p.id;
            bu = new BuildingUtil(b);
            System.out.println("Shape "+b_id+" of "+p.length+"\n"+bu.toString());
        }
        p.next();
        packTogether(p.next(), p.next());
        return;
    }

}
