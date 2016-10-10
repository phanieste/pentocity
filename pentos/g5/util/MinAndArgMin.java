package pentos.g5.util;

public class MinAndArgMin<ArgType> {
    public float min = Float.MAX_VALUE;
    public int len = 0;
    public int idxMin = -1;
    public int frequency = 0;
    public ArgType argMin = null;

    public void consider(float val, ArgType arg) {
        if( val < min ) {
            min = val;
            idxMin = len;
            argMin = arg;
            frequency = 1;
        } else if (val==min) {
            frequency+=1;
        }
        ++len;
    }

    public String toString() {
        return "(min:"+min+", idxMin:"+idxMin+", frequency:"+frequency+")";
    }

}