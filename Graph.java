/* Charlie Gerrie 2018
 * 
 * This class represents a graph for the game.
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Graph {
	// set of vertices composing the graph
	Set<Vertex> vertices;
	public Graph() {
		vertices = new HashSet<>();
	}
	public Graph(GraphParameters graphParams, SideParameters sideParams) {
		/*
		 * Generate graph
		 */
		if(graphParams instanceof GridGraph) {
			vertices = new HashSet<>();
			GridGraph gridParams = (GridGraph)graphParams;
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
				}
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
			RandomSides randParams = (RandomSides)sideParams;
			for(Vertex v : vertices)
				v.side = Math.random()<randParams.strongRate?Side.STRONG:Side.WEAK;
		}
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
	
	
	/*
	 * Classes for passing around generation parameters	
	 */
	
	// Standard class for parameterizing a way of generating and connecting vertices
	static abstract class GraphParameters {}
	// Particular instance for generating a 2D grid
	static class GridGraph extends GraphParameters {
		int x, y;
		boolean loopx, loopy;
		public GridGraph(int x, int y, boolean loopx, boolean loopy) {
			this.x = x;
			this.y = y;
			this.loopx = loopx;
			this.loopy = loopy;
		}
	}
	// Standard class for parameterizing a way of assigning sides
	static abstract class SideParameters {}
	static class RandomSides extends SideParameters {
		public double strongRate;
		public RandomSides(double strongRate) {
			this.strongRate = strongRate;
		}
		public RandomSides() {
			strongRate = 0.5;
		}
	}
	// class to not initialize the sides to anything, so that they can be set later
	static class DontInitializeSides extends SideParameters {}
}