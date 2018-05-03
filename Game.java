
public class Game {
	public static void IterateGraph(Graph currentGraph) {
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
			if((v.side == Side.STRONG && with >= against) ||
			   (v.side == Side.WEAK && with > against))
				v.inversePower = with;
			else
				v.inversePower = 0;
		}
		// calculate nextSide
		Vertex maxInfluenceNeighbor;
		for(Vertex v : currentGraph.vertices) {
			maxInfluenceNeighbor = null;
			for(Vertex w : v.neighbors) {
				if(w.inversePower != 0) {
					if(maxInfluenceNeighbor == null)
						maxInfluenceNeighbor = w;
					else if((w.inversePower == maxInfluenceNeighbor.inversePower && w.side == v.side) ||
					        (w.inversePower < maxInfluenceNeighbor.inversePower))
						maxInfluenceNeighbor = w;
				}
			}
			if(maxInfluenceNeighbor == null || maxInfluenceNeighbor.inversePower == 0)
				v.nextSide = v.side;
			else
				v.nextSide = maxInfluenceNeighbor.side;
		}
		// set each vertex's side to nextSide
		for(Vertex v : currentGraph.vertices)
			v.side = v.nextSide;
	}
}
