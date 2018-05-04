/* Charlie Gerrie 2018
 * 
 * This class represents a vertex on a Graph.
 */

import java.util.HashSet;
import java.util.Set;

public class Vertex {
	public int inversePower; // note that inversePower == 0 actually represents power = 0
	public Side side, nextSide;
	public Set<Vertex> neighbors;
	public Position pos;
	public Vertex() {
		neighbors = new HashSet<>();
	}
	public String toString() {
		String acc = "neighbors:\n";
		for(Vertex v : neighbors)
			acc += "\t"+ v.side + "\n";
		return acc;
	}
	// returns a distinct copy, with the exception that the neighbors aren't copied
	public Vertex copyWithoutNeighbors() {
		Vertex copy = new Vertex();
		copy.inversePower = inversePower;
		copy.side = side==null?null:side;
		copy.nextSide = nextSide==null?null:nextSide;
		copy.pos = pos==null?null:pos.clone();
		
		return copy;
	}
}
