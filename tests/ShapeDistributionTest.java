package tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import simulation.EffGraph;
import simulation.Side;
import simulation.Util;

public class ShapeDistributionTest {
	private static final int XSIZE = 50,
	                         YSIZE = 50,
	                         BLOCKSIZE = 10;
	private static final String file = "50x50(rand)distData10";
	public static void main(String[] args) {
		Side[][] set = new Side[XSIZE][YSIZE];
		for(int i=0;i<set.length;i++)
			for(int j=0;j<set[i].length;j++)
				//set[i] = i%2==0?Side.WEAK:Side.STRONG;
				//set[i] = i<set.length/2?(i/((int)(600/zoom)))%4==0||(i/((int)(600/zoom)))%4==1?Side.WEAK:Side.STRONG:Side.STRONG;
				//set[i] = (i/((int)(xres/zoom)))%4==0||(i/((int)(xres/zoom)))%4==2?Side.WEAK:Side.STRONG;
				set[i][j] = Math.random()>0.5?Side.WEAK:Side.STRONG;
		int[][] compstates = new int[XSIZE][YSIZE];
		for(int i=0;i<compstates.length;i++)
			for(int j=0;j<compstates[i].length;j++)
				compstates[i][j] = 11;
		//graph = new EffGraph(set,compstates);
		EffGraph graph = new EffGraph(set,compstates);
		System.err.println("Done initializing graph");
		for(int i=0;i<100;i++)
			graph.stepTorus();
		System.err.println("Done simulating graph");
		int[][] barGraph = new int[XSIZE*YSIZE][6];
		int[] currBlockGraph;
		int blockI = 0;
		for(int x=0;x<XSIZE;x++) {
			for(int y=0;y<YSIZE;y++) {
				currBlockGraph = new int[6];
				for(int i=0;i<BLOCKSIZE-1;i++)
					for(int j=0;j<BLOCKSIZE-1;j++)
						currBlockGraph[graph.getShapeSym(Util.mod(x+i,XSIZE),Util.mod(y+j,YSIZE))]++;
				barGraph[blockI++] = currBlockGraph;
			}
		}
		try {
			File outfile = new File(file);
			PrintWriter outWriter = new PrintWriter(outfile);
			
			for(int i=0;i<barGraph.length;i++) {
				for(int j=0;j<barGraph[i].length;j++) {
					outWriter.print(barGraph[i][j]+"\t");
				}
				outWriter.println();
			}
			
			outWriter.flush();
			outWriter.close();
			System.out.println("done writing to data to "+file);
		} catch(FileNotFoundException e) {
			System.err.println("dist file not found");
		}
		
	}
}
