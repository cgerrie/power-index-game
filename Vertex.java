import java.util.HashSet;
import java.util.Set;

public class Vertex {
	public int inversePower;
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
}
