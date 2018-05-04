/* Charlie Gerrie 2018
 * 
 * This class represents the sides of the power index game. See the Game class for more on their differences.
 */
public enum Side {
	STRONG, WEAK;
	public String toString() {
		switch(this) {
		case STRONG: return "STRONG";
		case WEAK: return "WEAK";
		default: return "Why?";
		}
	}
	// this is used by the draw method of Main to color the graph different colors for each side
	public double getColor() {
		switch(this) {
		case STRONG: return 1;
		case WEAK: return 0.5;
		default: return 0;
		}
	}
}
