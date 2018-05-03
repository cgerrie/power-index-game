import org.lwjgl.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;

public class Main {
	public boolean isRunning = false;
	public long lastFrame = getTime();
	public long lastFPS = getTime();
	public static long startTime;
	public int fps = 0;
	
	Graph graph;
	
	double zoom = 5;
	int timeSinceLastFrame = 0;
	int frameTime = 100;

	public static void main(String[] args) {
		Main main = new Main();
		main.start();
	}
	public void start() {
		startTime = System.nanoTime();
		init(800,600);
		isRunning = true;
		while(isRunning) {
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

			input();
			draw();
			move(getDelta());

			updateFPS();
			Display.update();
			Display.sync(60);
		}
	}
	public void init(int xres, int yres) {
		try {
			Display.setDisplayMode(new DisplayMode(xres,yres));
			Display.create();
		} catch(LWJGLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glViewport(0,0,xres,yres);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0,xres,0,yres,1,-1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		graph = new Graph(new Graph.GridGraph((int)(800/zoom),(int)(600/zoom),false,false), new Graph.RandomSides());
	}
	public void input() {
		if(Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
			isRunning = false;
	}
	public void draw() {
		for(Vertex v : graph.vertices) {
			double color = v.side.getColor();
			GL11.glColor3d(color, color, color);
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2d(zoom*v.pos.x, zoom*v.pos.y);
				GL11.glVertex2d(zoom*(v.pos.x+1), zoom*v.pos.y);
				GL11.glVertex2d(zoom*(v.pos.x+1), zoom*(v.pos.y+1));
				GL11.glVertex2d(zoom*v.pos.x, zoom*(v.pos.y+1));
			GL11.glEnd();
		}
	}
	public void move(int delta) {
		if((timeSinceLastFrame+=delta)>frameTime) {
			timeSinceLastFrame = 0;
			System.out.println("move");
			Game.IterateGraph(graph);
		}
	}
	public long getTime() {
		return System.nanoTime() / 1000000;
	}
	public int getDelta() {
		long time = getTime();
		int delta = (int) (time - lastFrame);
		lastFrame = time;
		return delta;
	}
	public void updateFPS() {
		if(getTime() - lastFPS > 1000) {
			System.out.println("FPS: "+ fps);
			fps = 0;
			lastFPS+=1000;
		}
		fps++;
	}
}