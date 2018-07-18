package tests;

import java.util.ArrayList;
import java.util.List;

import simulation.Util;

public class ConstantThirdPowerTest {
	private static int runIterations;
	public static void main(String[] args) {
		int i = 0,
		    xsize = 10,
		    ysize = 10;
		
		for(Boolean[][] config : removeSymmetries(getConfigurations(xsize, ysize), xsize, ysize)) {
			System.out.println("\nconfig "+(i++));
			for(int y=0;y<ysize;y++) {
				for(int x=0;x<xsize;x++)
					System.out.print(config[x][y]?"S":"W");
				System.out.println();
			}
		}
		System.out.println("counting completed with "+runIterations+" iterations");
	}
	public static Boolean[][][] getConfigurations(int xsize, int ysize) {
		List<Boolean[][]> configs = new ArrayList<Boolean[][]>(),
		                  toRemove = new ArrayList<Boolean[][]>();
		configs.add(new Boolean[xsize][ysize]);
		int configI = 0, x, y;
		NeighCount[] neighs = new NeighCount[4];
		for(int i=0;i<4;i++)
			neighs[i] = new NeighCount();
		Boolean[][] curr;
		while(configI < configs.size()) {
			curr = configs.get(configI++);
			for(x = 0, y = 0;
			    y < ysize && x < xsize && curr[x][y] != null;
			    y += (x == xsize-1?1:0), x = (x+1)%xsize);
			if(y == ysize)
				continue;
			// count neighbors
			for(int i=0;i<4;i++)
				neighs[i].reset();
			countNeighbors(curr, x-1, y, xsize, ysize, neighs[0]);
			countNeighbors(curr, x+1, y, xsize, ysize, neighs[1]);
			countNeighbors(curr, x, y-1, xsize, ysize, neighs[2]);
			countNeighbors(curr, x, y+1, xsize, ysize, neighs[3]);
			if(canBeTrue(neighs))
				configs.add(copyAndSet(curr, x, y, true));
			if(canBeFalse(neighs))
				configs.add(copyAndSet(curr, x, y, false));
			toRemove.add(curr);
		}
		check:
		for(Boolean[][] config : configs) {
			for(int i=0;i<config.length;i++) {
				for(int j=0;j<config.length;j++) {
					neighs[0].reset();
					countNeighbors(config, i, j, xsize, ysize, neighs[0]);
					if(neighs[0].trueCount != 2 || neighs[0].falseCount != 2) {
						toRemove.add(config);
						continue check;
					}	
				}
			}
		}
		runIterations = configI;
		configs.removeAll(toRemove);
		Boolean[][][] ret = new Boolean[configs.size()][][];
		configs.toArray(ret);
		return ret;
	}
	private static class NeighCount {
		int trueCount, falseCount, neithCount;
		private void reset() {
			trueCount = 0;
			falseCount = 0;
			neithCount = 0;
		}
	}
	private static void countNeighbors(Boolean[][] config, int x, int y, int xsize, int ysize, NeighCount count) {
		count.reset();
		if(config[Util.mod(x, xsize)][Util.mod(y, ysize)] == null)
			return;
		countSpace(config, Util.mod(x-1, xsize), Util.mod(y, ysize), count);
		countSpace(config, Util.mod(x+1, xsize), Util.mod(y, ysize), count);
		countSpace(config, Util.mod(x, xsize), Util.mod(y-1, ysize), count);
		countSpace(config, Util.mod(x, xsize), Util.mod(y+1, ysize), count);
	}
	private static void countSpace(Boolean[][] config, int x, int y, NeighCount count) {
		if(config[x][y] == null)
			count.neithCount++;
		else if(config[x][y])
			count.trueCount++;
		else
			count.falseCount++;
	}
	private static Boolean[][] copyAndSet(Boolean[][] config, int x, int y, boolean side) {
		Boolean[][] copy = new Boolean[config.length][];
		for(int i=0;i<copy.length;i++)
			copy[i] = new Boolean[config[i].length];
		for(int i=0;i<copy.length;i++)
			for(int j=0;j<copy[i].length;j++)
				copy[i][j] = config[i][j];
		copy[x][y] = side;
		return copy;
	}
	private static boolean canBeTrue(NeighCount[] count) {
		for(int i=0;i<4;i++)
			if(count[i].trueCount >= 2)
				return false;
		return true;
	}
	private static boolean canBeFalse(NeighCount[] count) {
		for(int i=0;i<4;i++)
			if(count[i].falseCount >= 2)
				return false;
		return true;
	}
	private static Boolean[][][] removeSymmetries(Boolean[][][] configs, int xsize, int ysize) {
		boolean fits;
		int x, y, yp, remaining = configs.length;
		next:
		for(int configi=0;configi<configs.length;configi++) {
			for(int configj=0;configj<configi;configj++) {
				if(configs[configj] != null) {
					for(int dx=0;dx<xsize;dx++) {
						for(int dy=0;dy<ysize;dy++) {
							for(int rot=0;rot<(xsize==ysize?4:1);rot++) {
								for(int flip=0;flip<2;flip++) {
									for(int s=0;s<2;s++) {
										// iter
										fits = true;
										for(int i=0;i<xsize;i++) {
											for(int j=0;j<ysize;j++) {
												x = Util.mod(i+dx, xsize);
												y = Util.mod(j+dy, ysize);
												for(int k=0;k<rot;k++) {
													yp = x;
													x = ysize-y-1;
													y = yp;
												}
												if(flip!=0)
													x = xsize-x-1;
												if(s==0?(configs[configi][i][j] != configs[configj][x][y]):
												        (configs[configi][i][j] == configs[configj][x][y]))
													fits = false;
											}
										}
										if(fits) {
											configs[configi] = null;
											remaining--;
											continue next;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		Boolean[][][] ret = new Boolean[remaining][][];
		int j = 0;
		for(int i=0;i<configs.length;i++)
			if(configs[i] != null)
				ret[j++] = configs[i];
		return ret;
	}
}
