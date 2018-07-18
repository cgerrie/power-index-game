package tests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;

import simulation.EffGraph;
import simulation.Side;
import simulation.Util;
import symmetries.SymmetryCounter;
import symmetries.SymmetryGetter;

public class PropSetter {
	public static Side[][] generateGrid(int xsize, int ysize, Condition[] conditions, double epsilon) {
		Side[][] board = new Side[xsize][ysize];
		for(int i=0;i<xsize;i++)
			for(int j=0;j<ysize;j++)
				board[i][j] = Math.random()>0.5?Side.WEAK:Side.STRONG;
		double totalDelta = totalDelta(conditions, board),
		       maxDelta, e;
		Condition toChange;
		while(totalDelta>epsilon) {
			maxDelta = 0;
			toChange = null;
			for(Condition c : conditions) {
				if((e = c.getDelta(board)) > maxDelta) {
					maxDelta = e;
					toChange = c;
				}
			}
			toChange.changeRandom(board);
			totalDelta = totalDelta(conditions, board);
		}
		return board;
	}
	private static double totalDelta(Condition[] conditions, Side[][] board) {
		double ret = 0;
		for(Condition c : conditions)
			ret += c.getDelta(board);
		return ret;
	}
	public static abstract class Condition {
		public abstract double getDelta(Side[][] board);
		public abstract void changeRandom(Side[][] board);
	}
	public static Condition prop_2 = new Condition(){
		private EffGraph graph = new EffGraph();
		private Random rand = new Random();
		public double getDelta(Side[][] board) {
			double ret = 0;
			graph.sides = board;
			int size = board.length*board[0].length;
			int[] quants = graph.count2shapesSym();
			ret += Math.abs(((double)quants[0]/size)-0.1342);
			ret += Math.abs(((double)quants[1]/size)-0.1342);
			ret += Math.abs(((double)quants[2]/size)-0.3344);
			ret += Math.abs(((double)quants[3]/size)-0.0201);
			ret += Math.abs(((double)quants[4]/size)-0.1884);
			ret += Math.abs(((double)quants[5]/size)-0.1884);
			return ret;
		}
		public void changeRandom(Side[][] board) {
			graph.sides = board;
			double initialDelta = getDelta(board),
			       delta = initialDelta;
			int x = 0, y = 0, originalShape = -1;
			while(delta >= initialDelta) {
				if(originalShape != -1)
					graph.set2Shape(x, y, originalShape);
				x = rand.nextInt(board.length);
				y = rand.nextInt(board[0].length);
				originalShape = graph.getShape(x, y);
				graph.set2Shape(x, y, rand.nextInt(16));
				delta = getDelta(board);
			}
		}
	};
	public static Condition prop_3 = new Condition(){

		// TODO SPEED UP CONVERGENCE BY SKEWING WHICH SHAPES ARE CHANGED TO BASED ON THEIR INDIVIDUAL ERROR
		
		private EffGraph graph = new EffGraph();
		private Random rand = new Random();
		private List<SymmetryCounter.Arrangement> shapes;
		private List<Double> shapeExpectedProps;
		private static final String PROPS_FILE = "3prop.csv";
		private static final int TRIES = 3;
		boolean isInited = false;
		public double getDelta(Side[][] board) {
			if(!isInited) {
				// set symmetry defaults
				SymmetryCounter.checkTranslations = true; // TODO CHANGE TO FALSE
				SymmetryCounter.checkRotations = true;
				SymmetryCounter.checkReflections = true;
				SymmetryCounter.checkSideSym = false;
			
				shapes = new ArrayList<>();
				shapes.addAll(SymmetryGetter.getMapping(3, 3).keySet());
			
				shapeExpectedProps = new LinkedList<>();
				try {
					Scanner file = new Scanner(new File(PROPS_FILE));
					while(file.hasNext())
						shapeExpectedProps.add(file.nextDouble());
				} catch(IOException e) {
					System.err.println("Error loading prop_3 props file");
					e.printStackTrace();
					System.exit(-1);
				}
				// done
				isInited = true;
			}
			// ADD ALL THE SHAPESA INTPOINTER ABS DIFFERENCES FROM EXPECTED PROPS FROM FILE
				// READ PROPS FROM FILE
				// FOR I=1 TO ||SHAPESA||
					// RET += ABS(EXPROP[I]-SHAPESA[I])
			double ret = 0;
			graph.sides = board;
			HashMap<SymmetryCounter.Arrangement, Util.IntPointer> shapesA = graph.countShapes(3, 3, shapes);
			double[] ss = new double[shapesA.size()];
			int ssi = 0;
			//for(Util.IntPointer num : shapesA)
			for(SymmetryCounter.Arrangement arr : shapes)
				ss[ssi++] = (double)shapesA.get(arr).x/(board.length*board[0].length);
			
			for(int i=0;i<shapesA.size();i++)
				ret += Math.abs(ss[i]-shapeExpectedProps.get(i));
			System.err.println("delta: "+ret);
			return ret;
		}
		public void changeRandom(Side[][] board) {
			// TODO DOES THIS NEED TO BE HERE?
			SymmetryCounter.xres = 3;
			SymmetryCounter.yres = 3;
			
			graph.sides = board;
			double initialDelta = getDelta(board),
			       delta = initialDelta;
			int x = 0, y = 0, originalShape = -1, tries = 0;
			while(delta >= initialDelta && (tries++) < TRIES) {
				if(originalShape != -1)
					graph.setNShape(3, x, y, originalShape);
				x = rand.nextInt(board.length);
				y = rand.nextInt(board[0].length);
				originalShape = graph.getNShape(3, x, y);
				
				// find which shape needs to be changed to the most
				// TODO FIT THIS WITH A PROBABILITY DISTRIBUTION
				// TODO FIX THIS TO BE WEIGHTED OFF UNDERREPRESENTATION, NOT ABSOLUTE PROPORTION!
				SymmetryCounter.Arrangement maxArrang = null;
				HashMap<SymmetryCounter.Arrangement, Util.IntPointer> shapesA = graph.countShapes(3, 3, shapes);
				int size = board.length*board[0].length,
				    skip = (int)(Math.random()*size);
				for(Entry<SymmetryCounter.Arrangement, Util.IntPointer> e : shapesA.entrySet())
					if((skip -= e.getValue().x)<=0)
						maxArrang = e.getKey();
				
				
				// find a random shape which fits that arrangement
				SymmetryCounter.Arrangement arr;
				int shapeToUse;
				for(arr = new SymmetryCounter.Arrangement(SymmetryCounter.getGridFromIter(shapeToUse = rand.nextInt(512)), 3, 3);
				    !arr.equals(maxArrang);
				    arr = new SymmetryCounter.Arrangement(SymmetryCounter.getGridFromIter(shapeToUse = rand.nextInt(512)), 3, 3));
								    
				
				// set it
				graph.set2Shape(x, y, shapeToUse);
				delta = getDelta(board);
			}
			if(delta >= initialDelta)
				graph.setNShape(3, x, y, originalShape);
		}
	};
	public static Condition prop_power = new Condition(){
		public double getDelta(Side[][] board) {
			return 0; // TODO CHANGE
		}
		public void changeRandom(Side[][] board) {
			
		}
	};
}
