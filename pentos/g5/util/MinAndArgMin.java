package pentos.g5.util;

import java.util.List;
import java.util.ArrayList;

public class MinAndArgMin<ArgType> {
    public float min = Float.MAX_VALUE;
    public int len = 0;
    public int idxMin = -1;
    public int frequency = 0;
    public ArgType argMin = null;

    public List<ArgType> argsMin = new ArrayList<ArgType>();

    public void consider(float val, ArgType arg) {
        if( val < min ) {
            min = val;
            idxMin = len;
            argMin = arg;
            frequency = 1;

            argsMin.clear();
        } else if (val==min) {
            frequency+=1;
            argsMin.add(arg);
        }
        ++len;
    }

    public String toString() {
        return "(min:"+min+", idxMin:"+idxMin+", frequency:"+frequency+")";
    }

}