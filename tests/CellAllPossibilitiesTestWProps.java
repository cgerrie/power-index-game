package tests;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import simulation.EffGraph;
import simulation.Side;

public class CellAllPossibilitiesTestWProps {
	public static void main(String[] args) {
		Side[][] setup = new Side[5][5];
		int[][] compstate = {{0,0,0,0,0},
		                     {0,0,1,0,0},
		                     {0,1,2,1,0},
		                     {0,0,1,0,0},
		                     {0,0,0,0,0}};
		boolean[][] iterstate = {{false,false, true,false,false},
		                         {false, true, true, true,false},
		                         { true, true, true, true, true},
		                         {false, true, true, true,false},
		                         {false,false, true,false,false}};
		EffGraph graph;
		//int[][] transitionCountMatrix = new int[5][5];
		double[][] transitionProb = new double[2][100];
		// iterations
		int p, strongs;
		double propValue;
		for(int setupi=0;setupi<(1 << 13);setupi++) {
			if(setupi*100%(1 << 13)==0)
				System.err.println((setupi*100/(1 << 13))+"% done");
			// initialize setup
			strongs = 0;
			p = 0;
			setup[2][2] = Side.WEAK;
			for(int i=0;i<5;i++) {
				for(int j=0;j<5;j++) {
					if(iterstate[i][j]) {
						if((1 & (setupi >> (p++)))!=0) {
							setup[i][j] = Side.STRONG;
							strongs++;
						}
						else
							setup[i][j] = Side.WEAK;
					}
				}
			}
			// init graph
			graph = new EffGraph(setup,compstate);
			graph.step();
			for(int prop=0;prop<100;prop++) {
				propValue = ((double)prop)/100;
				transitionProb[graph.sides[2][2].equals(Side.WEAK)?0:1][prop] += Math.pow(propValue, 13-strongs)*Math.pow(1-propValue, strongs);
			}
			//transitionCountMatrix[beforestate][graph.getShapeSym(2, 2)]++;
		}
		// output transitionCountMatrix
		try {
			File outfile = new File("cellTransitionsData2");
			PrintWriter outWriter = new PrintWriter(outfile);
			
			// print stats
			for(int i=0;i<100;i++)
				outWriter.println(transitionProb[0][i]+"\t"+transitionProb[1][i]);
			
			outWriter.flush();
			outWriter.close();
			System.out.println("done writing to file");
		} catch(FileNotFoundException e) {
			System.err.println("file not found");
		}
	}
}
