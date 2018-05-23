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
}
