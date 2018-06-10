package simulation;
/* Charlie Gerrie 2018
 * 
 * This class represents a graph for the game.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Graph implements Simulatable {
	public List<Vertex> vertices;
	private GraphParameters gparams;
	public Graph() {
		vertices = new ArrayList<>();
	}
	public Graph(GraphParameters graphParams, SideParameters sideParams) {
		/*
		 * Generate graph
		 */
		gparams = graphParams;
		if(graphParams instanceof GridGraph) {
			GridGraph gridParams = (GridGraph)graphParams;
			vertices = new ArrayList<>();
			Vertex[][] verticesArray = new Vertex[gridParams.x][gridParams.y];
			// Initialize objects in array
			for(int i=0;i<gridParams.x;i++) {
				for(int j=0;j<gridParams.y;j++) {
					verticesArray[i][j] = new Vertex();
					verticesArray[i][j].pos = new Position(i,j);
				}
			}
			// Add grid connections
			for(int i=0;i<gridParams.x;i++) {
				for(int j=0;j<gridParams.y;j++) {
					if(i != gridParams.x-1 || gridParams.loopx)
						verticesArray[i][j].neighbors.add(verticesArray[(i+1)%gridParams.x][j]);
					if(j != gridParams.y-1 || gridParams.loopy)
						verticesArray[i][j].neighbors.add(verticesArray[i][(j+1)%gridParams.y]);
					if(i != 0 || gridParams.loopx)
						verticesArray[i][j].neighbors.add(verticesArray[Util.mod(i-1,gridParams.x)][j]);
					if(j != 0 || gridParams.loopy)
						verticesArray[i][j].neighbors.add(verticesArray[i][Util.mod(j-1,gridParams.y)]);
					// strong grid attachments
					// TODO add lines to make strong grid work without requiring looping
					if(gridParams.isStrong && gridParams.loopx && gridParams.loopy) {
						verticesArray[i][j].neighbors.add(verticesArray[Util.mod(i-1, gridParams.x)]
						                                               [Util.mod(j-1, gridParams.y)]);
						verticesArray[i][j].neighbors.add(verticesArray[Util.mod(i+1, gridParams.x)]
						                                               [Util.mod(j-1, gridParams.y)]);
						verticesArray[i][j].neighbors.add(verticesArray[Util.mod(i+1, gridParams.x)]
						                                               [Util.mod(j+1, gridParams.y)]);
						verticesArray[i][j].neighbors.add(verticesArray[Util.mod(i-1, gridParams.x)]
						                                               [Util.mod(j+1, gridParams.y)]);
						// TODO test strong grid
					}
				}
			}
			// if there's a specific side setup
			if(sideParams instanceof SpecificSides) {
				SpecificSides sidesParams = (SpecificSides)sideParams;
				for(int i=0;i<gridParams.x;i++)
					for(int j=0;j<gridParams.y;j++)
						verticesArray[i][j].side = sidesParams.sides[i*gridParams.x+j];
			}
			// Add elements from grid into set
			for(int i=0;i<gridParams.x;i++) {
				for(int j=0;j<gridParams.y;j++) {
					vertices.add(verticesArray[i][j]);
				}
			}
		}
		/*
		 * Assign Sides
		 */
		if(sideParams instanceof RandomSides) {
			Random rand = new Random();
			RandomSides randParams = (RandomSides)sideParams;
			for(Vertex v : vertices)
				v.side = rand.nextFloat()<randParams.strongRate?Side.STRONG:Side.WEAK;
		}
	}
	public int hashCode() {
		int total = 0;
		for(Vertex v : vertices)
			total += v.side==Side.STRONG?1:0;
		return total;
	}
	// returns a distinct copy
	public Graph clone() {
		Graph newGraph = new Graph();
		HashMap<Vertex,Vertex> correspondence = new HashMap<>(); // correspondence between original and copy vertices
		// generate the new vertices, and store their correspondence to their original nodes
		for(Vertex v : vertices)
			correspondence.put(v, v.copyWithoutNeighbors());
		// connect the new vertices in the same way the original vertices were
		Vertex newV;
		for(Vertex v : vertices) {
			newV = correspondence.get(v);
			for(Vertex w : v.neighbors)
				newV.neighbors.add(correspondence.get(w));
		}
		// copy vertices into clone's set
		for(HashMap.Entry<Vertex,Vertex> e : correspondence.entrySet())
			newGraph.vertices.add(e.getValue());
		// return
		return newGraph;
	}
	// Simulatable methods
	public Side gridGet(int x, int y) {
		return vertices.get(y*((GridGraph)gparams).x+x).side;
	}
	public void step() {
		Game.CalculateWeights(this);
		Game.IterateGraph(this);
	}
	/*
	 * Classes for passing around generation parameters	
	 */
	
	// Standard class for parameterizing a way of generating and connecting vertices
	public static abstract class GraphParameters {}
	// Particular instance for generating a 2D grid
	public static class GridGraph extends GraphParameters {
		int x, y;
		boolean loopx, loopy, isStrong;
		public GridGraph(int x, int y, boolean loopx, boolean loopy) {
			this.x = x;
			this.y = y;
			this.loopx = loopx;
			this.loopy = loopy;
			isStrong = false;
		}
		public GridGraph(int x, int y, boolean loopx, boolean loopy, boolean isStrong) {
			this(x,y,loopx,loopy);
			this.isStrong = isStrong;
		}
	}
	// TODO Petersen graph
	// Standard class for parameterizing a way of assigning sides
	public static abstract class SideParameters {}
	// randomly assigns sides
	public static class RandomSides extends SideParameters {
		public double strongRate;
		public RandomSides(double strongRate) {
			this.strongRate = strongRate;
		}
		public RandomSides() {
			strongRate = 0.5;
		}
	}
	// assigns sides according to a specific list
	public static class SpecificSides extends SideParameters {
		public Side[] sides;
		public SpecificSides(Side[] sides) {
			this.sides = sides;
		}
		public static SpecificSides fromBinaryString(String s) {
			Side[] sides = new Side[s.length()];
			for(int i=0;i<s.length();i++) {
				switch(s.charAt(i)) {
				case 'w': sides[i] = Side.WEAK;
				          break;
				case 'W': sides[i] = Side.WEAK;
				          break;
				case '0': sides[i] = Side.WEAK;
				          break;
				case 's': sides[i] = Side.STRONG;
				          break;
				case 'S': sides[i] = Side.STRONG;
				          break;
				case '1': sides[i] = Side.STRONG;
				          break;
				default:  sides[i] = null;
				}
			}
			return new SpecificSides(sides);
		}
	}
	// class to not initialize the sides to anything, so that they can be set later
	public static class DontInitializeSides extends SideParameters {}
}