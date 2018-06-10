package mains;
/* Charlie Gerrie 2018
 * 
 * This class contains an application for visually viewing simulations of the power index game.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.lwjgl.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;

import simulation.EffGraph;
import simulation.Side;
import simulation.Util;
import simulation.Vertex;
import symmetries.SymmetryCounter;
import symmetries.SymmetryGetter;

public class EffMain {
	// standard graphics variables
	public boolean isRunning = false; // used to control the main loop
	public long lastFrame = getTime();
	public long lastFPS = getTime();
	public static long startTime;
	public int fps = 0;
	// graph being simulated
	//Graph graph;
	EffGraph graph;
	List<Integer[]> hashes,
	                changeHashes;
	// graph visualization parameters
	double zoom = 4;
	int timeSinceLastFrame = 0;
	int frameTime = 50;
	boolean shouldIterate,
	        buttonHeld,
	        isColourful,
	        isChangeful,
	        powerNeedsRecalculating,
	        F_held,
	        L_held,
	        keepRunning = false,
	        storeShapes = false;
	int XRES = 512,
	    YRES = 512,
	    shapexsize = 2,
	    shapeysize = 2;
	double RAND_START_PROP = 0.5;
	
	Set<SymmetryCounter.Arrangement> shapes;
	String shapeFile = "emptydata",
	       changePropsFile = "changeData";
	SymmetryCounter.Arrangement[][] prevIter, currIter;	
	// main method
	public static void main(String[] args) {
		EffMain main = new EffMain();
		main.start();
	}
	// main loop
	public void start() {
		startTime = System.nanoTime();
		init(XRES,YRES);
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
		// write shape data
		try {
			File outfile = new File(shapeFile);
			PrintWriter outWriter = new PrintWriter(outfile);
			for(Integer[] i : hashes) {
				for(Integer j : i)
					outWriter.print(j+"\t");
				outWriter.println();
			}
			outWriter.println();
			for(SymmetryCounter.Arrangement arr : shapes) {
				for(int i=0;i<arr.cells.length;i++)
					for(int j=0;j<arr.cells[i].length;j++)
						outWriter.print(arr.cells[i][j]==Side.STRONG?"S":"W");
				outWriter.print("\t");
			}
			outWriter.println();
			outWriter.flush();
			outWriter.close();
			System.out.println("done writing to shape file");
		} catch(FileNotFoundException e) {
			System.err.println("shape file not found");
		}
		// write change data
		try {
			File outfile = new File(changePropsFile);
			PrintWriter outWriter = new PrintWriter(outfile);
			for(Integer[] i : changeHashes) {
				for(Integer j : i)
					outWriter.print(j+"\t");
				outWriter.println();
			}
			outWriter.println();
			outWriter.flush();
			outWriter.close();
			System.out.println("done writing to change file");
		} catch(FileNotFoundException e) {
			System.err.println("change file not found");
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
		/*Side[] set = new Side[(int)(xres/zoom)*(int)(yres/zoom)];
		for(int i=0;i<set.length;i++)
			//set[i] = i%2==0?Side.WEAK:Side.STRONG;
			//set[i] = i<set.length/2?(i/((int)(600/zoom)))%4==0||(i/((int)(600/zoom)))%4==1?Side.WEAK:Side.STRONG:Side.STRONG;
			set[i] = (i/((int)(xres/zoom)))%4==0||(i/((int)(xres/zoom)))%4==2?Side.WEAK:Side.STRONG;*/
		//graph = new Graph(new Graph.GridGraph((int)(xres/zoom),(int)(xres/zoom),true,true), new Graph.SpecificSides(set));
		Side[][] set = new Side[(int)(xres/zoom)][(int)(yres/zoom)];
		for(int i=0;i<set.length;i++)
			for(int j=0;j<set[i].length;j++)
				//set[i] = i%2==0?Side.WEAK:Side.STRONG;
				//set[i] = i<set.length/2?(i/((int)(600/zoom)))%4==0||(i/((int)(600/zoom)))%4==1?Side.WEAK:Side.STRONG:Side.STRONG;
				//set[i] = (i/((int)(xres/zoom)))%4==0||(i/((int)(xres/zoom)))%4==2?Side.WEAK:Side.STRONG;
				set[i][j] = Math.random()>RAND_START_PROP?Side.WEAK:Side.STRONG;
		int[][] compstates = new int[(int)(xres/zoom)][(int)(yres/zoom)];
		for(int i=0;i<compstates.length;i++)
			for(int j=0;j<compstates[i].length;j++)
				compstates[i][j] = 11;
		graph = new EffGraph(set,compstates);
		//graph = new Graph(new Graph.GridGraph((int)(xres/zoom),(int)(yres/zoom),true,true), new Graph.RandomSides(0.0));
		//Game.CalculateWeights(graph);   
		hashes = new LinkedList<>();
		changeHashes = new LinkedList<>();
		shouldIterate = false;
		buttonHeld = false;
		isColourful = false;
		powerNeedsRecalculating = false;
		F_held = false;
		L_held = false;
		
		//SymmetryCounter.checkSideSym = false;
		setSymmetryDefaults();
		shapes = SymmetryGetter.getMapping(shapexsize, shapeysize).keySet();
		
		currIter = new SymmetryCounter.Arrangement[xres][yres];
		for(int i=0;i<currIter.length;i++)
			for(int j=0;j<currIter[i].length;j++)
				graph.getShape(i, j, shapexsize, shapeysize, shapes, currIter[i][j]=new SymmetryCounter.Arrangement(null, shapexsize, shapeysize));
	}
	// this method is for handling input, and is called each frame
	public void input() {
		// exit
		if(Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
			isRunning = false;
		shouldIterate = Keyboard.isKeyDown(Keyboard.KEY_SPACE) || keepRunning;
		isColourful = Keyboard.isKeyDown(Keyboard.KEY_C);
		isChangeful = Keyboard.isKeyDown(Keyboard.KEY_H);
		if(Mouse.isButtonDown(0)) {
			if(!buttonHeld) {
				buttonHeld = true;
				int x = Mouse.getX(),
				    y = Mouse.getY();
				/*for(Vertex v : graph.vertices)
					if(v.pos.x == x/(int)zoom && v.pos.y == y/(int)zoom)
						v.side = Side.flip(v.side);*/
				graph.sides[x/(int)zoom][y/(int)zoom] = Side.flip(graph.sides[x/(int)zoom][y/(int)zoom]);
				powerNeedsRecalculating = true;
			}
		}
		else
			buttonHeld = false;
		// get sizes
		if(Keyboard.isKeyDown(Keyboard.KEY_F) && !F_held) {
			int[][] plane1 = new int[(int)(XRES/zoom)][(int)(YRES/zoom)],
			        plane2 = new int[(int)(XRES/zoom)][(int)(YRES/zoom)];
			for(int i=0;i<(int)(XRES/zoom);i++) {
				for(int j=0;j<(int)(YRES/zoom);j++) {
					//System.out.println(graph.vertices.get(i*(int)(600/zoom)+j).side);
					//plane1[i][j] = graph.vertices.get(i*(int)(XRES/zoom)+j).side==Side.WEAK?0:-1;
					//plane2[i][j] = graph.vertices.get(i*(int)(XRES/zoom)+j).side==Side.STRONG?0:-1;
					plane1[i][j] = graph.sides[i][j]==Side.WEAK?0:-1;
					plane2[i][j] = graph.sides[i][j]==Side.STRONG?0:-1;
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
		// print top line's sequence
		if(Keyboard.isKeyDown(Keyboard.KEY_L) && !L_held) {
			for(int i=0;i<graph.sides.length;i++)
				System.out.print(graph.sides[i][0]==Side.STRONG?"1\t":"0\t");
			System.out.println();
		}
		L_held = Keyboard.isKeyDown(Keyboard.KEY_L);
	}
	// this method is for drawing the visualizaiton, and is called each frame
	public void draw() {
		// draw the vertices of the graph
		/*for(Vertex v : graph.vertices) {
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
			
		}*/
		double scolour;
		byte bcolour;
		double[] pcolour;
		double x, y;
		for(int i=0;i<graph.sides.length;i++) {
			for(int j=0;j<graph.sides[i].length;j++) {
				if(isColourful) {
					pcolour = Vertex.getColor(graph.powers[i][j], graph.sides[i][j]);
					GL11.glColor3d(pcolour[0], pcolour[1], pcolour[2]);
				}
				else if(isChangeful && prevIter != null) {
					SymmetryCounter.checkReflections = false;
					SymmetryCounter.checkRotations = false;
					SymmetryCounter.checkSideSym = false;
					SymmetryCounter.checkTranslations = false;
					bcolour = 0;
					for(int i2=0;i2<shapexsize;i2++) {
						for(int j2=0;j2<shapeysize;j2++) {
							/*scolour +=
								prevIter[Util.mod(i-i2,XRES)][Util.mod(j-j2,YRES)]!=null &&
								!prevIter[Util.mod(i-i2,XRES)][Util.mod(j-j2,YRES)].equals(currIter[i-i2][j-j2])
									?1/((double)(shapexsize*shapeysize)):0;*/
							if(prevIter[Util.mod(i-i2,XRES)][Util.mod(j-j2,YRES)]!=null &&
							   (!prevIter[Util.mod(i-i2,XRES)][Util.mod(j-j2,YRES)].equals(currIter[Util.mod(i-i2,XRES)][Util.mod(j-j2,YRES)]))) {
								bcolour += (byte)(255/(shapexsize*shapeysize));
							}
						}
					}
					GL11.glColor3b(bcolour<0?(byte)(bcolour+128):0, bcolour>64 && bcolour<=128?bcolour:0, bcolour<=64?bcolour:0);
				}
				else {
					scolour = graph.sides[i][j].getColor();
					GL11.glColor3d(scolour, scolour, scolour);
				}
				GL11.glBegin(GL11.GL_QUADS);
					GL11.glVertex2d(zoom*i, zoom*j);
					GL11.glVertex2d(zoom*(i+1), zoom*j);
					GL11.glVertex2d(zoom*(i+1), zoom*(j+1));
					GL11.glVertex2d(zoom*i, zoom*(j+1));
				GL11.glEnd();
			}
		}
		setSymmetryDefaults();
	}
	// this method is for animating, and is called each frame along with the time in ms since last frame
	public void move(int delta) {
		// iterate graph if enough time has elapsed
		if((timeSinceLastFrame+=delta)>frameTime && shouldIterate) {
			Integer[] ss;
			Collection<Util.IntPointer> shapesA;
			timeSinceLastFrame = 0;
			// handle shapes history
			if(storeShapes) {
				shapesA = graph.countShapes(shapexsize, shapeysize, shapes).values();//graph.countSidesSym();//graph.count2shapes();
				ss = new Integer[shapesA.size()];
				int i = 0;
				for(Util.IntPointer num : shapesA)
					ss[i++] = num.x;
				hashes.add(ss);
			}
			// handle change-space
			prevIter = currIter;
			// iterate
			graph.stepTorus();
			// finish handling change space
			currIter = new SymmetryCounter.Arrangement[XRES][YRES];
			for(int i=0;i<currIter.length;i++)
				for(int j=0;j<currIter[i].length;j++)
					graph.getShape(i, j, shapexsize, shapeysize, shapes, currIter[i][j] = new SymmetryCounter.Arrangement(null, shapexsize, shapeysize));
			//Game.IterateGraph(graph);
			// handle change history
			Integer[] changedProps = new Integer[5];
			for(int i=0;i<changedProps.length;i++)
				changedProps[i] = 0;
			int changeCount;
			for(int i=0;i<graph.sides.length;i++) {
				for(int j=0;j<graph.sides[i].length;j++) {
					changeCount = 0;
					for(int i2=0;i2<shapexsize;i2++)
						for(int j2=0;j2<shapeysize;j2++)
							if(prevIter[Util.mod(i-i2,XRES)][Util.mod(j-j2,YRES)]!=null &&
							  (!prevIter[Util.mod(i-i2,XRES)][Util.mod(j-j2,YRES)].equals(currIter[Util.mod(i-i2,XRES)][Util.mod(j-j2,YRES)])))
								changeCount++;
					changedProps[changeCount]++;
							
				}
			}
			changeHashes.add(changedProps);
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
	public void setSymmetryDefaults() {
		SymmetryCounter.checkTranslations = true;
		SymmetryCounter.checkRotations = true;
		SymmetryCounter.checkReflections = true;
		SymmetryCounter.checkSideSym = false;
	}
}