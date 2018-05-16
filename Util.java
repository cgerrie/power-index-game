import java.util.ArrayList;
import java.util.List;

/* Charlie Gerrie 2018
 * 
 * This class contains static utility functions
 */
public class Util {
	public static int mod(int n, int m) {
		n = n%m;
		if(n<0)
			n += m;
		return n;
	}
	public static long factorial(int n) {
		if(n==0)
			return 1;
		else
			return n*factorial(n-1);
	}
	public static long choose(int n, int m) {
		return factorial(n)/(factorial(m)*factorial(n-m));
	}
	public static int countZeroes(String s) {
		int n = 0;
		for(char c : s.toCharArray())
			if(c == '0')
				n++;
		return n;
	}
	// flood fill plane
	// key:
	//    0   - not filled yet
	//   -1   - wall
	//    n>0 - nth group
	public static List<Integer> floodFill(int[][] plane) {
		intPointer count = new intPointer(1);
		List<Integer> sizes = new ArrayList<Integer>();
		int size;
		for(int i=0;i<plane.length;i++) {
			for(int j=0;j<plane[i].length;j++) {
				if((size=floodFill(plane, i, j, count))!=0) {
					sizes.add(size);
					count.x++;
					//System.out.println("found "+count.x);
				}
			}
		}
		/*
		int[] sizesArray = new int[sizes.size()];
		for(int i = 0; i < sizes.size(); i++)
			sizesArray[i] = sizes.get(i);*/
		return sizes;
	}
	public static int floodFill(int[][] plane, int x, int y, intPointer count) {
		// already filled, or wall, or out of bounds
		if(x<0 ||
		   y<0 ||
		   x >= plane.length ||
		   y >= plane[x].length ||
		   plane[x][y] != 0)
			return 0;
		// otherwise
		plane[x][y] = count.x;
		return 1+
		       floodFill(plane, x+1, y, count)+
		       floodFill(plane, x, y+1, count)+
		       floodFill(plane, x-1, y, count)+
		       floodFill(plane, x, y-1, count);
	}
	private static class intPointer {
		int x;
		intPointer(int x) {
			this.x = x;
		}
	}
}
