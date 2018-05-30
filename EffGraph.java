/* Charlie Gerrie 2018
 * 
 * This is a more efficient, thought less flexible, simulator for power-index games on a grid.
 */

public class EffGraph implements Simulatable {
	Side[][] sides, next;
	int[][] computeStates, powers;	
	public EffGraph(Side[][] sides, int[][] computeStates) {
		this.sides = sides;
		this.computeStates = computeStates;
		powers = new int[sides.length][];
		next = new Side[sides.length][];
		for(int i=0;i<sides.length;i++) {
			powers[i] = new int[sides[i].length];
			next[i] = new Side[sides[i].length];
		}
	}
	public Side gridGet(int x, int y) {
		return sides[x][y];
	}
	public void step() {
		// compute power
		Side[] neighbors;
		int forCount;
		for(int i=0;i<sides.length;i++) {
			for(int j=0;j<sides[i].length;j++) {
				if(computeStates[i][j]>=1) {
					forCount = 0;
					neighbors = closedNeighborhood(i,j);
					for(int k=0;k<5;k++)
						if(sides[i][j].equals(neighbors[k]))
							forCount++;
					if(forCount>=3)
						powers[i][j] = forCount;
					else
						powers[i][j] = 0;
				}
			}
		}
		// compute next
		int maxPower;
		Side changeTo;
		int[] neighborsPower;
		for(int i=0;i<sides.length;i++) {
			for(int j=0;j<sides[i].length;j++) {
				if(computeStates[i][j]>=2) {
					maxPower = 0;
					changeTo = null;
					neighborsPower = closedNeighborhoodPower(i,j);
					neighbors = closedNeighborhood(i,j);
					for(int k=0;k<5;k++) {
						if(maxPower == 0 ||
						   (neighborsPower[k] != 0 && neighborsPower[k]<maxPower) ||
						   (neighborsPower[k]==maxPower &&
						    neighbors[k].equals(sides[i][j]))) {
							
							maxPower = neighborsPower[k];
							changeTo = neighbors[k];
						}
					}
					next[i][j] = changeTo;
				}
			}
		}
		// set next
		for(int i=0;i<sides.length;i++) {
			for(int j=0;j<sides[i].length;j++) {
				if(computeStates[i][j]>=2)
					sides[i][j] = next[i][j];
			}
		}
	}
	public void stepTorus() {
		// compute power
		Side[] neighbors;
		int forCount;
		for(int i=0;i<sides.length;i++) {
			for(int j=0;j<sides[i].length;j++) {
				if(computeStates[i][j]>=1) {
					forCount = 0;
					neighbors = closedNeighborhoodTorus(i,j);
					for(int k=0;k<5;k++)
						if(sides[i][j].equals(neighbors[k]))
							forCount++;
					if(forCount>=3)
						powers[i][j] = forCount;
					else
						powers[i][j] = 0;
				}
			}
		}
		// compute next
		int maxPower;
		Side changeTo;
		int[] neighborsPower;
		for(int i=0;i<sides.length;i++) {
			for(int j=0;j<sides[i].length;j++) {
				if(computeStates[i][j]>=2) {
					maxPower = 0;
					changeTo = null;
					neighborsPower = closedNeighborhoodPowerTorus(i,j);
					neighbors = closedNeighborhoodTorus(i,j);
					for(int k=0;k<5;k++) {
						if(maxPower == 0 ||
						   (neighborsPower[k] != 0 && neighborsPower[k]<maxPower) ||
						   (neighborsPower[k]==maxPower &&
						    neighbors[k].equals(sides[i][j]))) {
							
							maxPower = neighborsPower[k];
							changeTo = neighbors[k];
						}
					}
					next[i][j] = changeTo;
				}
			}
		}
		// set next
		for(int i=0;i<sides.length;i++) {
			for(int j=0;j<sides[i].length;j++) {
				if(computeStates[i][j]>=2)
					sides[i][j] = next[i][j];
			}
		}
	}
	private Side[] closedNeighborhood(int x, int y) {
		Side[] list = new Side[5];
		list[0] = sides[x+1][y];
		list[1] = sides[x][y+1];
		list[2] = sides[x-1][y];
		list[3] = sides[x][y-1];
		list[4] = sides[x][y];
		return list;
	}
	private int[] closedNeighborhoodPower(int x, int y) {
		int[] list = new int[5];
		list[0] = powers[x+1][y];
		list[1] = powers[x][y+1];
		list[2] = powers[x-1][y];
		list[3] = powers[x][y-1];
		list[4] = powers[x][y];
		return list;
	}
	private Side[] closedNeighborhoodTorus(int x, int y) {
		Side[] list = new Side[5];
		list[0] = sides[Util.mod(x+1, sides.length)][y];
		list[1] = sides[x][Util.mod(y+1, sides[x].length)];
		list[2] = sides[Util.mod(x-1, sides.length)][y];
		list[3] = sides[x][Util.mod(y-1, sides[x].length)];
		list[4] = sides[x][y];
		return list;
	}
	private int[] closedNeighborhoodPowerTorus(int x, int y) {
		int[] list = new int[5];
		list[0] = powers[Util.mod(x+1, sides.length)][y];
		list[1] = powers[x][Util.mod(y+1, sides[x].length)];
		list[2] = powers[Util.mod(x-1, sides.length)][y];
		list[3] = powers[x][Util.mod(y-1, sides[x].length)];
		list[4] = powers[x][y];
		return list;
	}
	public int[] count2shapes() {
		int[] ret = new int[16];
		for(int i=0;i<sides.length;i++)
			for(int j=0;j<sides[i].length;j++)
				ret[getShape(i,j)]++;
		return ret;
	}
	public int[] count2shapesSym() {
		int[] ret = new int[6];
		for(int i=0;i<sides.length;i++)
			for(int j=0;j<sides[i].length;j++)
				ret[getShapeSym(i,j)]++;
		return ret;
	}
	private int getShape(int i, int j) {
		return (sides[i][j]==Side.STRONG?8:0)+
		       (sides[Util.mod(i+1, sides.length)][j]==Side.STRONG?4:0)+
		       (sides[Util.mod(i+1, sides.length)][Util.mod(j+1, sides[i].length)]==Side.STRONG?2:0)+
		       (sides[i][Util.mod(j+1, sides[i].length)]==Side.STRONG?1:0);
	}
	public int getShapeSym(int i, int j) {
		int shape = getShape(i,j);
		if(shape==0) // white/weak square
			return 0;
		if(shape==15) // black/strong square
			return 1;
		if(shape==3 || shape==6 || shape==9 || shape==12) // side
			return 2;
		if(shape==5 || shape==10) // diag
			return 3;
		if(shape==7 || shape==11 || shape==13 || shape==14) // white/weak corner
			return 4;
		if(shape==1 || shape==2 || shape==4 || shape==8) // black/strong corner
			return 5;
		return -1;
	}
}
