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
	double zoom = 25;
	int timeSinceLastFrame = 0;
	int frameTime = 50;
	boolean shouldIterate,
	        buttonHeld,
	        isColourful,
	        powerNeedsRecalculating,
	        F_held;
	// main method
	public static void main(String[] args) {
		Main main = new Main();
		main.start();
	}
	// main loop
	public void start() {
		startTime = System.nanoTime();
		init(600,600);
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
		Side[] set = new Side[(int)(600/zoom)*(int)(600/zoom)];
		for(int i=0;i<set.length;i++)
			//set[i] = i%2==0?Side.WEAK:Side.STRONG;
			//set[i] = i<set.length/2?(i/((int)(600/zoom)))%4==0||(i/((int)(600/zoom)))%4==1?Side.WEAK:Side.STRONG:Side.STRONG;
			set[i] = (i/((int)(600/zoom)))%4==0||(i/((int)(600/zoom)))%4==1?Side.WEAK:Side.STRONG;
		graph = new Graph(new Graph.GridGraph((int)(600/zoom),(int)(600/zoom),true,true), new Graph.SpecificSides(set));
		
		//graph = new Graph(new Graph.GridGraph((int)(600/zoom),(int)(600/zoom),true,true), new Graph.RandomSides(0.5));
		Game.CalculateWeights(graph);   
		hashes = new LinkedList<>();
		shouldIterate = false;
		buttonHeld = false;
		isColourful = false;
		powerNeedsRecalculating = false;
		F_held = false;
	}
	// this method is for handling input, and is called each frame
	public void input() {
		// exit
		if(Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
			isRunning = false;
		shouldIterate = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
		isColourful = Keyboard.isKeyDown(Keyboard.KEY_C);
		if(Mouse.isButtonDown(0)) {
			if(!buttonHeld) {
				buttonHeld = true;
				int x = Mouse.getX(),
				    y = Mouse.getY();
				for(Vertex v : graph.vertices)
					if(v.pos.x == x/(int)zoom && v.pos.y == y/(int)zoom)
						v.side = Side.flip(v.side);
				powerNeedsRecalculating = true;
			}
		}
		else
			buttonHeld = false;
		// get sizes
		if(Keyboard.isKeyDown(Keyboard.KEY_F) && !F_held) {
			int[][] plane1 = new int[(int)(600/zoom)][(int)(600/zoom)],
			        plane2 = new int[(int)(600/zoom)][(int)(600/zoom)];
			for(int i=0;i<(int)(600/zoom);i++) {
				for(int j=0;j<(int)(600/zoom);j++) {
					//System.out.println(graph.vertices.get(i*(int)(600/zoom)+j).side);
					plane1[i][j] = graph.vertices.get(i*(int)(600/zoom)+j).side==Side.WEAK?0:-1;
					plane2[i][j] = graph.vertices.get(i*(int)(600/zoom)+j).side==Side.STRONG?0:-1;
				}
			}
			System.out.print("Weaks:");
			int prev = 0;
			List<Integer> list = Util.floodFill(plane1);
			list.sort((x,y) -> y.compareTo(x));
			for(Integer i : list)
				System.out.printf((i!=prev?"\n":"\t")+"%d",prev=i);
			list = Util.floodFill(plane2);
			list.sort((x,y) -> y.compareTo(x));
			System.out.print("\nStrongs:");
			prev = 0;
			for(Integer i : list)
				System.out.printf((i!=prev?"\n":"\t")+"%d",prev=i);
		}
		F_held = Keyboard.isKeyDown(Keyboard.KEY_F);
	}
	// this method is for drawing the visualizaiton, and is called each frame
	public void draw() {
		// draw the vertices of the graph
		for(Vertex v : graph.vertices) {
			// TODO coloring based on power, proportion, regional weighting, etc
			if(isColourful) {
				if(powerNeedsRecalculating) {
					Game.CalculateWeights(graph);
					powerNeedsRecalculating = false;
				}
				double[] colour = v.getColor();
				GL11.glColor3d(colour[0], colour[1], colour[2]);
			}
			else {
				double colour = v.side.getColor();
				GL11.glColor3d(colour, colour, colour);
			}
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