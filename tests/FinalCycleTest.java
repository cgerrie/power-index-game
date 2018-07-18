package tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.*;

import simulation.EffGraph;
import simulation.Side;
import simulation.Util;
import symmetries.SymmetryCounter;

public class FinalCycleTest {
	private static final int ITERATIONS = 100,
	                         XSIZE = 50,
	                         YSIZE = 50;
	private static final double EPSILON = 0.01;
	private static final String FILE = "50x50cycleMultiData";
	volatile static Util.IntPointer threadsCompleted;
	public static void main(String[] args) throws InterruptedException,ExecutionException {
		ExecutorService executor = Executors.newFixedThreadPool(8);
		List<Future<Cycle>> futures = new LinkedList<>();
		threadsCompleted = new Util.IntPointer(0);
		int created = 0;
		for(int i=0;i<ITERATIONS;i++) {
			futures.add(executor.submit(new Simul(XSIZE, YSIZE, EPSILON, threadsCompleted)));
			System.err.println("created "+(created++)+"th sim");
		}
		System.err.println("done creating threads");
		executor.shutdown();
		while(!executor.isTerminated()) {
			System.err.println(threadsCompleted.x+" done so far");
			TimeUnit.SECONDS.sleep(5);
		}
		try {
			File outfile = new File(FILE);
			PrintWriter outWriter = new PrintWriter(outfile);
			for(Future<Cycle> f : futures)
				outWriter.println(f.get().toString());
			outWriter.flush();
			outWriter.close();
			System.err.println("done writing to file");
		} catch(FileNotFoundException e) {
			System.err.println("shape file not found");
		}
	}
	private static class Cycle {
		int startIter, endIter;
		public Cycle(int startIter, int endIter) {
			this.startIter = startIter;
			this.endIter = endIter;
		}
		public String toString() {
			return startIter+"\t"+endIter;
		}
	}
	private static class Simul implements Callable<Cycle> {
		EffGraph graph;
		volatile long timeToCalc = -1;
		private final long startTime = System.currentTimeMillis();
		volatile Util.IntPointer counter;
		public Simul(int xsize, int ysize, double epsilon, Util.IntPointer counter) {
			Side[][] board = PropSetter.generateGrid(xsize,
			                                         ysize,
			                                         new PropSetter.Condition[]{},//new PropSetter.Condition[]{PropSetter.prop_2},
			                                         epsilon);
			int[][] compstates = new int[xsize][ysize];
			for(int i=0;i<compstates.length;i++)
				for(int j=0;j<compstates[i].length;j++)
					compstates[i][j] = 11;
			graph = new EffGraph(board, compstates);
			this.counter = counter;
		}
		public Cycle call() {
			HashMap<String, Integer> map;
			int iter;
			for(map = new HashMap<>(), iter = 0;!map.containsKey(graph.toString());graph.stepTorus())
				map.put(graph.toString(), iter++);
			timeToCalc = System.currentTimeMillis()-startTime;
			counter.x++;
			return new Cycle(map.get(graph.toString()), iter);
		}
	}
}
