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
}