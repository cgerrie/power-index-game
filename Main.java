/* Charlie Gerrie 2018
 * 
 * This class contains an application for visually viewing simulations of the power index game.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;

public class Main {
	// standard graphics variables
	public boolean isRunning = false; // used to control the main loop
	public long lastFrame = getTime();
	public long lastFPS = getTime();
	public static long startTime;
	public int fps = 0;
	// graph being simulated
	Graph graph;
	List<Integer> hashes;
	// graph visualization parameters
	double zoom = 50;
	int timeSinceLastFrame = 0;
	int frameTime = 50;
	boolean shouldIterate;
	// main method
	public static void main(String[] args) {
		Main main = new Main();
		main.start();
	}
	// main loop
	public void start() {
		startTime = System.nanoTime();
		init(800,600);
		isRunning = true;
		while(isRunning) {
			// refresh screen
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			// call each-frame methods
			input();
			draw();
			move(getDelta());
			// update screen
			updateFPS();
			Display.update();
			Display.sync(60);
		}
		try {
			File outfile = new File("data2");
			PrintWriter outWriter = new PrintWriter(outfile);
			for(int i : hashes) {
				outWriter.println(i);
			}
			outWriter.flush();
			outWriter.close();
			System.out.println("done writing to file");
		} catch(FileNotFoundException e) {
			System.err.println("file not found");
		}
	}
	// initialization; gets run once at beginning of start() main loop
	public void init(int xres, int yres) {
		// create window
		try {
			Display.setDisplayMode(new DisplayMode(xres,yres));
			Display.create();
		} catch(LWJGLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		// initialize openGL environment
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
		// initialize graph
		graph = new Graph(new Graph.GridGraph((int)(600/zoom),(int)(600/zoom),false,false), new Graph.RandomSides());
		hashes = new LinkedList<>();
		shouldIterate = false;
	}
	// this method is for handling input, and is called each frame
	public void input() {
		// exit
		if(Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
			isRunning = false;
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
			shouldIterate = true;
		else
			shouldIterate = false;
	}
	// this method is for drawing the visualizaiton, and is called each frame
	public void draw() {
		// draw the vertices of the graph
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
	// this method is for animating, and is called each frame along with the time in ms since last frame
	public void move(int delta) {
		// iterate graph if enough time has elapsed
		if((timeSinceLastFrame+=delta)>frameTime && shouldIterate) {
			timeSinceLastFrame = 0;
			//System.out.println("move");
			hashes.add(graph.hashCode());
			Game.IterateGraph(graph);
		}
	}
	// these methods are used for timing
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
			//System.out.println("FPS: "+ fps);
			fps = 0;
			lastFPS+=1000;
		}
		fps++;
	}
}