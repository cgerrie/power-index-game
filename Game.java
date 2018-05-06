/* Charlie Gerrie 2018
 * 
 * This class contains the logic of the power index game
 */
public class Game {
	public static void IterateGraph(Graph currentGraph) {
		// TODO parallelize
		// calculate weights
		int with, against;
		for(Vertex v : currentGraph.vertices) {
			with = 1; // since we're counting how many agree in v's closed neighborhood
			against = 0;
			for(Vertex w : v.neighbors) {
				if(w.side == v.side)
					with++;
				else
					against++;
			}
			// WEAK vertices will have 0 power if half or more members of their closed neighborhood disagree with them,
			// whereas STRONG ones will only have 0 power if strictly more members do.
			if((v.side == Side.STRONG && with >= against) ||
			   (v.side == Side.WEAK && with > against))
				v.inversePower = with;
			else
				v.inversePower = 0;
		}
		// calculate nextSide
		Vertex maxInfluenceNeighbor;
		for(Vertex v : currentGraph.vertices) {
			// find the neighbor with the highest non-zero power
			maxInfluenceNeighbor = v.inversePower!=0?v:null;
			for(Vertex w : v.neighbors) {
				if(w.inversePower != 0) {
					if(maxInfluenceNeighbor == null)
						maxInfluenceNeighbor = w;
					// if two neighbors have the same power, the one with the same side as v will win
					else if((w.inversePower == maxInfluenceNeighbor.inversePower && w.side == v.side) ||
					        (w.inversePower < maxInfluenceNeighbor.inversePower))
						maxInfluenceNeighbor = w;
				}
			}
			// set nextSide
			// if the vertex is not being influenced, it keeps its current side
			if(maxInfluenceNeighbor == null || maxInfluenceNeighbor.inversePower == 0)
				v.nextSide = v.side;
			// otherwise it takes the side of the most influential neighbor
			else
				v.nextSide = maxInfluenceNeighbor.side;
		}
		// set each vertex's side to nextSide
		for(Vertex v : currentGraph.vertices)
			v.side = v.nextSide;
	}
}
