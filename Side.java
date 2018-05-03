
public enum Side {
	STRONG, WEAK;
	public String toString() {
		switch(this) {
		case STRONG: return "STRONG";
		case WEAK: return "WEAK";
		default: return "Why?";
		}
	}
	public double getColor() {
		switch(this) {
		case STRONG: return 1;
		case WEAK: return 0.5;
		default: return 0;
		}
	}
}
