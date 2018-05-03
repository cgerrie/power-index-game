
public enum Side {
	STRONG, WEAK;
	public String toString() {
		switch(this) {
		case STRONG: return "STRONG";
		case WEAK: return "WEAK";
		default: return "Why?";
		}
	}
}
