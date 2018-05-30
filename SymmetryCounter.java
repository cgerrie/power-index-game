import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedList;

public class SymmetryCounter {
	static int xres = 5,
	           yres = 5;
	public static void main(String[] args) {
		/*long max = 1 << xres*yres;
		LinkedList<Side[][]>[] arrangements = new LinkedList[xres*yres/2+1];
		for(int i=0;i<arrangements.length;i++)
			arrangements[i] = new LinkedList<Side[][]>();
		
		Side[][] currentGraph;
		int count;
		boolean contained;
		String mesg;
		try {
			File outfile = new File("5x5configurations.txt");
			PrintWriter outWriter = new PrintWriter(outfile);
			for(long state=0;state<max;state++) {
				if(state%(max/100)==0)
					System.err.println((100*state/max)+"% done");
				currentGraph = getGridFromIter(state);
				count = countSides(currentGraph);
				if(count>=(xres*yres+1)/2)
					count = xres*yres-count;
				contained = false;
				for(Side[][] otherGraph : arrangements[count])
					if(areSymmetrical(currentGraph, otherGraph))
						contained = true;
				if(!contained)
					arrangements[count].add(currentGraph);
			}
			for(int i=0;i<arrangements.length;i++) {
				System.out.println("arrangements with "+i+" cells");
				outWriter.println("arrangements with "+i+" cells");
				for(Side[][] graph : arrangements[i]) {
					mesg = presentGrid(graph);
					System.out.println(mesg+"\n");
					outWriter.println(mesg+"\n");
				}
			}
			outWriter.flush();
			outWriter.close();
		} catch(FileNotFoundException e) {
			System.err.println("file not found");
		}*/
	}
	public static boolean areSymmetrical(Side[][] g1, Side[][] g2) {
		boolean fits;
		int x, y, yp;
		for(int dx=0;dx<xres;dx++) {
			for(int dy=0;dy<yres;dy++) {
				for(int rot=0;rot<4;rot++) {
					for(int flip=0;flip<2;flip++) {
						for(int s=0;s<2;s++) {
							// iter
							fits = true;
							for(int i=0;i<xres;i++) {
								for(int j=0;j<yres;j++) {
									x = Util.mod((flip==1?xres-i-1:i)+dx, xres);
									y = Util.mod((flip==1?yres-j-1:j)+dy, yres);
									for(int k=0;k<rot;k++) {
										yp = x;
										x = yres-y-1;
										y = yp;
									}
									if(!g1[i][j].equals(g2[x][y]) ^ s==1)
										fits = false;
								}
							}
							if(fits)
								return true;
						}
					}
				}
			}
		}
		return false;
	}
	public static Side[][] getGridFromIter(long n) {
		Side[][] ret = new Side[xres][yres];
		for(int i=0;i<xres;i++) {
			for(int j=0;j<yres;j++) {
				ret[i][j] = (1&n)!=0?Side.STRONG:Side.WEAK;
				n = n >> 1;
			}
		}
		return ret;
	}
	public static int countSides(Side[][] g) {
		int ret = 0;
		for(int i=0;i<g.length;i++)
			for(int j=0;j<g[i].length;j++)
				if(g[i][j].equals(Side.WEAK))
					ret++;
		return ret;
	}
	public static String presentGrid(Side[][] g) {
		String ret = "";
		for(int y=0;y<yres;y++) {
			for(int x=0;x<xres;x++)
				ret += g[x][y].equals(Side.WEAK)?"W":"S";
			ret = ret+"\n";
		}
		return ret;
	}
}
