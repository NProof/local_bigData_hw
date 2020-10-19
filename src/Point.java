import java.util.ArrayList;
import java.*;

public class Point {
	public double[] dhours;

	public Point(ArrayList<Integer> arr) {
		dhours = new double [24];
		for(int i=0; i<24; ++i) {
			dhours[i] = arr.get(i);
		}
	}
	
	@Override
    public String toString() {
		String ret = "[ " + dhours[0];
		for (int i=1; i<dhours.length; ++i) {
			ret = ret.concat(" , " + dhours[i]);
		}
        return ret; 
    } 
}