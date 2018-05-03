
public class Util {
	public static int mod(int n, int m) {
		n = n%m;
		if(n<0)
			n += m;
		return n;
	}
}
