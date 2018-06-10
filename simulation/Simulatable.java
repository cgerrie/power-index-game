package simulation;

public interface Simulatable {
	public Side gridGet(int x, int y);
	public void step();
}
