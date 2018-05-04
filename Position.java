/* Charlie Gerrie 2018
 * 
 * This class is used to store the position of a Vertex for the visualization
 */
public class Position {
	public int x, y;
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public Position clone() {
		return new Position(x,y);
	}
}
