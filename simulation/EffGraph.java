package simulation;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import symmetries.SymmetryCounter;
import symmetries.SymmetryGetter;

/* Charlie Gerrie 2018
 * 
 * This is a more efficient, thought less flexible, simulator for power-index games on a grid.
 */

public class EffGraph implements Simulatable {
	public Side[][] sides, next;
	public int[][] computeStates, powers;
	private static final double NONRANDOM_TRANSITION_CHANCE = 1;
	private Function<Integer,Double> transitionFunc = null,
	                                 randomFunc = null;
	private int t;
	public EffGraph() {}
	public EffGraph(Side[][] sides, int[][] computeStates) {
		this.sides = sides;
		this.computeStates = computeStates;
		powers = new int[sides.length][];
		next = new Side[sides.length][];
		for(int i=0;i<sides.length;i++) {
			powers[i] = new int[sides[i].length];
			next[i] = new Side[sides[i].length];
		}
		t = 0;
	}
	public EffGraph(Side[][] sides, int[][] computeStates,
	                Function<Integer, Double> transitionFunc,
	                Function<Integer, Double> randomFunc) {
		this(sides, computeStates);
		this.transitionFunc = transitionFunc;
		this.randomFunc = randomFunc;
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
				if((computeStates[i][j]&1)!=0) {
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
				if((computeStates[i][j]&2)!=0) {
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
		computePowerTorus();
		// compute next
		int maxPower;
		Side changeTo;
		Side[] neighbors;
		int[] neighborsPower;
		for(int i=0;i<sides.length;i++) {
			for(int j=0;j<sides[i].length;j++) {
				if((computeStates[i][j]&2)!=0) {
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
				if(computeStates[i][j]>=2) {
					if((transitionFunc == null && Math.random()<NONRANDOM_TRANSITION_CHANCE) ||
					   (transitionFunc != null && Math.random()<transitionFunc.apply(t)))
						sides[i][j] = next[i][j];
					else if(randomFunc != null && Math.random()<randomFunc.apply(t))
						sides[i][j] = Math.random()<0.5?Side.STRONG:Side.WEAK;
				}
			}
		}
		
		t++;
	}
	public void computePowerTorus() {
		Side[] neighbors;
		int forCount;
		for(int i=0;i<sides.length;i++) {
			for(int j=0;j<sides[i].length;j++) {
				if((computeStates[i][j]&1)!=0) {
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
	public int[] count2ShapesSymSpecified() {
		int[] ret = new int[6];
		for(int i=0;i<sides.length;i++)
			for(int j=0;j<sides[i].length;j++)
				if((computeStates[i][j]&8)!=0)
					ret[getShapeSym(i,j)]++;
		return ret;
	}
	public int getShape(int i, int j) {
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
	public int[] countSidesSym() {
		int[] ret = new int[2];
		for(int i=0;i<sides.length;i++)
			for(int j=0;j<sides[i].length;j++)
				ret[getSideSym(i,j)]++;
		return ret;
	}
	public int getSide(int i, int j) {
		return (sides[i][j]==Side.STRONG?2:0)+
		       (sides[Util.mod(i+1, sides.length)][j]==Side.STRONG?1:0);
	}
	public int getSideSym(int i, int j) {
		int side = getSide(i,j);
		if(side==0 || side==3)
			return 0;
		if(side==1 || side==2)
			return 1;
		return -1;
	}
	public HashMap<SymmetryCounter.Arrangement, Util.IntPointer>
	         countShapes(int xsize, int ysize, List<SymmetryCounter.Arrangement> shapes) {
		HashMap<SymmetryCounter.Arrangement, Util.IntPointer> map = new HashMap<>();
		if(xsize <= 0 || ysize <= 0)
			return map;
		for(SymmetryCounter.Arrangement a : shapes)
			map.put(a, new Util.IntPointer(0));
		Side[][] shape = new Side[xsize][ysize];
		SymmetryCounter.Arrangement currArr = new SymmetryCounter.Arrangement(shape, xsize, ysize);
		for(int i=0;i<sides.length;i++) {
			for(int j=0;j<sides[i].length;j++) {
				getShape(i,j,xsize,ysize,shapes,currArr);
				/*for(int x=0;x<xsize;x++)
					for(int y=0;y<ysize;y++)
						shape[x][y] = sides[Util.mod(i+x, sides.length)][Util.mod(j+y, sides[i].length)];
				currArr.cells = shape;*/
				map.get(currArr).x++;
			}
		}
		return map;
	}
	public void getShape(int i, int j, int xsize, int ysize, List<SymmetryCounter.Arrangement> shapes, SymmetryCounter.Arrangement currArr) {
		Side[][] shape = new Side[xsize][ysize];
		for(int x=0;x<xsize;x++) {
			for(int y=0;y<ysize;y++) {
				shape[x][y] =
					sides[Util.mod(i+x, sides.length)]
					     [Util.mod(j+y, sides[x].length)];
			}
		}
		
		currArr.cells = shape;
	}
	public HashMap<Integer, Util.IntPointer> countInvPowers() {
		HashMap<Integer, Util.IntPointer> map = new HashMap<>();
		map.put(0, new Util.IntPointer(0));
		map.put(3, new Util.IntPointer(0));
		map.put(4, new Util.IntPointer(0));
		map.put(5, new Util.IntPointer(0));
		for(int i=0;i<sides.length;i++)
			for(int j=0;j<sides[i].length;j++)
				map.get(powers[i][j]).x++;
		return map;
	}
	public Integer[] getPowerNeighbors() {
		Integer[] pairs = new Integer[20];
		for(int i=0;i<20;i++)
			pairs[i] = 0;
		int p1 = 0, p2 = 0, pair = -1;
		for(int i=0;i<sides.length;i++) {
			for(int j=0;j<sides[i].length;j++) {
				// convert inversePowers to numerals
				switch(powers[i][j]) {
				case 0: p1=0;
				        break;
				case 3: p1=1;
				        break;
				case 4: p1=2;
				        break;
				case 5: p1=3;
				        break;
				}
				switch(powers[Util.mod(i+1, sides.length)][j]) {
				case 0: p2=0;
				        break;
				case 3: p2=1;
				        break;
				case 4: p2=2;
				        break;
				case 5: p2=3;
				        break;
				}
				// make p2 always the greater one
				if(p2<p1) {
					int temp = p1;
					p1 = p2;
					p2 = temp;
				}
				// set pair
				switch(p1) {
				case 0: pair=0;
				        break;
				case 1: pair=4;
				        break;
				case 2: pair=7;
				        break;
				case 3: pair=9;
				        break;
				}
				pair += p2-p1;
				// add 10 to identifier if adjacent sides are different
				if(!sides[i][j].equals(sides[Util.mod(i+1, sides.length)][j]))
					pair += 10;
				// increment counter
				pairs[pair]++;
			}
		}
		return pairs;
	}
	public Integer[][] getShapeNeighborhoods() {
		// array of ret[centre shape][neighbortype] counts
		Integer[][] ret = new Integer[16][16];
		int centreShape;
		for(int i=0;i<sides.length;i++) {
			for(int j=0;j<sides[i].length;j++) {
				// get centre shape
				centreShape = getShape(i, j);
				// for 12 2-squares in 2-neighborhood of centre
				for(int di=-2;di<=2;di++)
					for(int dj=-2;dj<=2;dj++)
						if(Math.abs(di)+Math.abs(dj)==2 || Math.abs(di)+Math.abs(dj)==1)
							ret[centreShape][getShape(i+di, j+dj)]++;
					// ret[centre shape][neighbor shape]++
			}
		}
		return ret;
	}
	public Integer[][] getShapeNeighborhoodsSym() {
		// array of ret[centre shape][neighbortype] counts
		Integer[][] ret = new Integer[6][6];
		for(int i=0;i<6;i++)
			for(int j=0;j<6;j++)
				ret[i][j] = 0;
		int centreShape;
		for(int i=0;i<sides.length;i++) {
			for(int j=0;j<sides[i].length;j++) {
				// get centre shape
				centreShape = getShapeSym(i, j);
				// for 12 2-squares in 2-neighborhood of centre
				for(int di=-2;di<=2;di++)
					for(int dj=-2;dj<=2;dj++)
						if(Math.abs(di)+Math.abs(dj)==2 || Math.abs(di)+Math.abs(dj)==1)
							ret[centreShape][getShapeSym(Util.mod(i+di,sides.length), Util.mod(j+dj, sides[j].length))]++;
					// ret[centre shape][neighbor shape]++
			}
		}
		return ret;
	}
	public void set2Shape(int x, int y, int shape) {
		sides[x][y]=((1 & (shape>>3))!=0)?Side.STRONG:Side.WEAK;
		sides[Util.mod(x+1, sides.length)][y]=((1 & (shape>>2))!=0)?Side.STRONG:Side.WEAK;
		sides[Util.mod(x+1, sides.length)][Util.mod(y+1, sides[x].length)]=((1 & (shape>>1))!=0)?Side.STRONG:Side.WEAK;
		sides[x][Util.mod(y+1, sides[x].length)]=((1 & shape)!=0)?Side.STRONG:Side.WEAK;
	}
	public void setNShape(int n, int x, int y, int shape) {
		int toShift = n*n-1;
		for(int i=0;i<n;i++)
			for(int j=0;j<n;j++)
				sides[Util.mod(x+i, sides.length)][Util.mod(y+j, sides[x].length)] =
						(1 & (shape >> (toShift--)))!=0?Side.STRONG:Side.WEAK;
	}
	public int getNShape(int n, int x, int y) {
		int toShift = n*n-1;
		int ret = 0;
		for(int i=0;i<n;i++)
			for(int j=0;j<n;j++, toShift--)
				ret += sides[Util.mod(x+i, sides.length)][Util.mod(y+j, sides[x].length)]==Side.STRONG?
						1 << toShift:
						0;
		
		return ret;
	}
	public String toString() {
		String ret = "";
		for(int i=0;i<sides.length;i++)
			for(int j=0;j<sides[i].length;j++)
				ret += sides[i][j]==Side.STRONG?"0":"1";
		return ret;
	}
}
