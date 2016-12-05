package pentos.g5.util;

import java.util.Iterator;
import java.lang.Math;
import java.util.*;

public class Looper2D {
    // public enum Type {SPIRAL, CORNERS};

    // public Type type; 
    // public int size; // Ideally this should m, n

    public static List<Pair> getFour( int m, int n, int w, boolean outwards) {
        List<Pair> l = new ArrayList<Pair>();
        int i=m/4;
        int j=0;
        int k=0;
        for(j=0; j<n/2; ++j) {
            for(k=-w; k<w; ++k){
                l.add(new Pair(j, i+k));
            }
        }
        for(; i < 3*m/4; ++i) {
            for(k=-w; k<w; ++k){
                l.add(new Pair(j+k, i));
            }
        }
        for(j=0; j<n; ++j) {
            for(k=-w; k<w; ++k){
                l.add(new Pair(j, i+k));
            }
        }
        return l;
        // if( outwards )
        // List<Pair> shallowCopy = l.subList(0, l.size());
        // return Collections.reverse(shallowCopy);
    }

    /*
     * Note: has not actually been tested with m != n so using with such settings
     * may have unintended consequences...
     */
    public static List<Pair> getCorner( int m, int n, boolean outwards) {
        int numLoops = Math.min(m,n) - 1;
        List<Pair> l = new ArrayList<Pair>();
        Looper looper;
        
        int loop;
        looper = new Looper(0, numLoops*2+1, 1);

        while (looper.hasNext()) {
            loop = looper.next();

            if (loop <= numLoops) {
                int i = loop;
                int j = 0;
                for (; j <= loop; j++) {
                    i = loop - j;
                    if (outwards) {
                        l.add(new Pair(numLoops - i, numLoops - j));
                    } else {
                        l.add(new Pair(i,j));
                    }
                }
            } 
            else {
                int i = numLoops;
                int j = loop - numLoops;
                for (; j<= numLoops; j++) {
                    i = loop - j;
                    if (outwards) {
                        l.add(new Pair(numLoops - i, numLoops - j));
                    } else {
                        l.add(new Pair(i,j));
                    }
                }
            }
        }
        return l;
    }

    public static List<Pair> getSpiral( int m, int n, boolean outwards ) {

        int numLoops = (Math.min(m,n)+1)/2;
        int loop;
        int i;
        int j;
        List<Pair> l = new ArrayList<Pair>();
        Looper looper = null;

        if(outwards) {
            looper = new Looper(numLoops-1, -1, -1);
        } else {
            looper = new Looper(0, numLoops, 1);
        }

        while(looper.hasNext()) {
            // 0, 1, 2, 3, .... , ceil(m/2)-1
            loop = looper.next();

            i = (m/2);
            j = loop;
            for(; i > loop; --i) {
                l.add( new Pair(i,j) );
            }
            assert (i == loop);
            assert (j == loop);

            for(; j < (n-1) - loop; ++j) {
                l.add( new Pair(i,j) );
            }   // Traverse all in the top row

            for(; i < (m-1) - loop; ++i) {
                l.add( new Pair(i,j) );
            }   // Traverse all in the left column

            for(; j > loop; --j) {
                l.add( new Pair(i,j) );
            }
            assert (i == (m-1)-loop);
            assert (j == loop);

            for(; i > (m/2)-1; --i) {
                l.add( new Pair(i,j) );
            }
        }

        return l;
    }


    public static List<Pair> getBlocks( int m, int n, boolean outwards ) {

        int i;
        int j;
        List<Pair> l = new ArrayList<Pair>();
        Looper looperI = null;
        Looper looperJ = null;

        if(outwards) {
            for(i=0; i<m; ++i) {
                for(j=0; j<n; ++j) {
                    l.add( new Pair(i, j) );
                }
            }
        } else {
            for(i=m-1; i>=0; --i) {
                for(j=n-1; j>=0; --j) {
                    l.add( new Pair(i, j) );
                }
            }
        }

        return l;
    }

    public static void main(String[] args) {

        int counter = 0;
        char [][] buf = new char[10][10];
        StringUtil.init(buf, ' ');

        // Looper2D l2d = new Looper2D();
        for( Pair p : Looper2D.getSpiral(5,6,false)) {
            buf[p.i][p.j] = (char)('0'+(counter++)%10);
            // System.out.println( p.toString() );
        }

        System.out.println(StringUtil.toString(buf, "\n"));

        StringUtil.init(buf, ' ');
        counter = 0;

        // Looper2D l2d = new Looper2D();
        for( Pair p : Looper2D.getSpiral(6,5,true)) {
            buf[p.i][p.j] = (char)('0'+(counter++)%10);
            // System.out.println( p.toString() );
        }

        System.out.println(StringUtil.toString(buf, "\n"));

        StringUtil.init(buf, ' ');
        counter = 0;

        // Looper2D l2d = new Looper2D();
        for( Pair p : Looper2D.getCorner(3,6,true)) {
            buf[p.i][p.j] = (char)('0'+(counter++)%10);
            // System.out.println( p.toString() );
        }

        System.out.println(StringUtil.toString(buf, "\n"));

        StringUtil.init(buf, ' ');
        counter = 0;

        // Looper2D l2d = new Looper2D();
        for( Pair p : Looper2D.getCorner(6,6,false)) {
            buf[p.i][p.j] = (char)('0'+(counter++)%10);
            // System.out.println( p.toString() );
        }

        System.out.println(StringUtil.toString(buf, "\n"));
    }
}
