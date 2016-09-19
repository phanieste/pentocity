package pentos.g5.util;

public class Pair {
    public int i, j;
    public Pair() {
        this(0,0);
    }
    public Pair(Pair p) {
        this(p.i, p.j);
    }
    public Pair(int x, int y) {
        i = x;
        j = y;
    }
    public void add(Pair p) {
        i += p.i;
        j += p.i;
    }
    public void mult(int s) {
        i *= s;
        j *= s;
    }
    public void subtract(Pair p) {
        i -= p.i;
        j -= p.i;        
    }

    public boolean equals(Object o) {
        if (!(o instanceof Pair))
            return false;
        if (o == this)
            return true;
        return i == ((Pair) o).i && j == ((Pair) o).j;
    }

    public int hashCode() {
        return i*100+j;
    }

    public String toString() {
        return "("+i+","+j+")";
    }
}
