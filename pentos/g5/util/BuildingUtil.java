package pentos.g5.util;

import java.util.*;

import pentos.sim.Building;
import pentos.sim.Cell;

public class BuildingUtil {

    public Building building;

    public BuildingUtil(Building b) {
        building = b;
    }

    public static void toString1(Building building, char[][] buf, char occupied){
        toString1(building, buf, occupied, new Pair(0,0));
    }

    public static void toString1(Building building, char[][] buf, char occupied, Pair offset){

        for (Cell p : building) {
            buf[p.i + offset.i][p.j + offset.j] = occupied;
        }

        return;
    }

    public static String toString(Building building){
        int mini = 0;
        int maxi = 0;
        int minj = 0;
        int maxj = 0;
        char printChar = (building.type == Building.Type.RESIDENCE) ? 'R' : (building.type == Building.Type.FACTORY) ? 'F' : 'b';
        for (Cell p : building) {
            if (p.i < mini)
                mini = p.i;
            if (p.i > maxi)
                maxi = p.i;
            if (p.j < minj)
                minj = p.j;
            if (p.j > maxj)
                maxj = p.j;
        }

        char[][] buf = new char [maxi+1][maxj+1];
        for(int i = 0; i <= maxi; ++i) {
            for(int j = 0; j <= maxj; ++j) {
                buf[i][j] = '.';
            }
        }

        for (Cell p : building) {
            buf[p.i][p.j] = printChar;
        }

        String s1 = "";
        for(int i = 0; i <= maxi; ++i) {
            s1+= new String(buf[i]) + "\n";
        }
        // DEBUG System.err.println("size:(" + mini +","+ maxi +"),("+ minj +","+ maxj +")");
        return s1;
    }

    public String toString() {
        return BuildingUtil.toString( building );
    }

    public static Pair[] Hull(Building building) {

        Iterator<Cell> it = building.iterator();
        Pair start = new Pair(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Pair end = new Pair(Integer.MIN_VALUE, Integer.MIN_VALUE);
        Pair[] hull = {start, end};

        while( it.hasNext() ) {
            Cell c = it.next();
            if(c.i < start.i) {
                start.i = c.i;
            } else if(c.i > end.i) {
                end.i = c.i;
            }
            if(c.j < start.j) {
                start.j = c.j;
            } else if(c.j > end.j) {
                end.j = c.j;
            }
        }
        return hull;
    }

    public Pair[] Hull() {
        return BuildingUtil.Hull( building );
    }

}