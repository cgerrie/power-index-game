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
	public static long chooose(int n, int m) {
		return factorial(n)/(factorial(m)*factorial(n-m));
	}
}
