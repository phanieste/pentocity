package pentos.g5.util;

import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;

import pentos.sim.Land;
import pentos.sim.Building;
import pentos.sim.Cell;

public class Pentominos implements pentos.sim.Sequencer {

    // https://en.wikipedia.org/wiki/Pentomino
    public final static int [][][] indices = {
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

    public static Map<Character,Integer> shapeIds;

    public final static String names = "IFflFpPNnTUVWXYyzZ";

    static {
        shapeIds = new HashMap<Character,Integer>();
        shapeIds.put('I', 0); // {{0,0},{0,1},{0,2},{0,3},{0,4}},

        shapeIds.put('F', 1); // {{0,1},{0,2},{1,0},{1,1},{2,1}},
        shapeIds.put('f', 2); // {{0,0},{0,1},{1,1},{1,2},{2,1}},

        shapeIds.put('l', 3); // {{0,0},{0,1},{1,0},{2,0},{3,0}},
        shapeIds.put('L', 4); // {{0,0},{0,1},{1,1},{2,1},{3,1}},

        shapeIds.put('p', 5); // {{0,0},{0,1},{1,0},{1,1},{2,1}},
        shapeIds.put('P', 6); // {{0,0},{0,1},{1,0},{1,1},{2,0}},

        shapeIds.put('N', 7); // {{0,1},{1,1},{2,0},{2,1},{3,0}},
        shapeIds.put('n', 8); // {{0,0},{1,0},{2,0},{2,1},{3,1}},

        shapeIds.put('T', 9); // {{0,0},{0,1},{0,2},{1,1},{2,1}},

        shapeIds.put('U', 10); // {{0,0},{1,0},{1,1},{1,2},{0,2}},

        shapeIds.put('V', 11); // {{0,0},{0,1},{0,2},{1,2},{2,2}},

        shapeIds.put('W', 12); // {{0,0},{0,1},{1,1},{1,2},{2,2}},

        shapeIds.put('X', 13); // {{0,1},{1,0},{1,1},{1,2},{2,1}},

        shapeIds.put('Y', 14); // {{0,1},{1,0},{1,1},{2,1},{3,1}},
        shapeIds.put('y', 15); // {{0,0},{1,0},{1,1},{2,0},{3,0}},

        shapeIds.put('z', 16); // {{0,0},{0,1},{1,1},{2,1},{2,2}},
        shapeIds.put('Z', 17); // {{0,1},{0,2},{1,1},{2,1},{2,0}}

        // for (char c : names.toCharArray()) {
        //     final int i = shapeIds.get(c);
        //     final String s = BuildingUtil.toString(byName(c));
        //     System.out.printf("%c, %d\n%s\n", c, i, s);
        // }

    }

    public final static int length = indices.length;

    public static Building atId(int id) {
        Set<Cell> cells = new HashSet<Cell>();
        for(int i=0; i<5; ++i){
            // System.out.print("("+indices[id][i][0]+","+indices[id][i][1]+") ");
            cells.add(new Cell(indices[id][i][0],indices[id][i][1]));
        }
        // System.out.println();
        return new Building(cells.toArray(new Cell[cells.size()]), Building.Type.RESIDENCE);
    }

    public static Building byName(char c) {

        assert (names.indexOf(c)!=-1) : "Valid shapes are: "+names+" cannot find: "+c;
        if (names.indexOf(c)==-1) {
            System.out.println("Valid shapes are: "+names+" cannot find: "+c);
            c = 'X';
        }
        return atId(shapeIds.get(c));
    }

    public int id = -1;

    public static Set <Building> buildings;

    public void init() {
    }

    public Building current() {
        return atId(id);
    }

    public Building next() {
        id = (id+1)%length;
        return current();
    }

}
