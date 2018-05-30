import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class ManySquarePropTest {
	private static int simSize = 100,
	                   iterations = 100,
	                   simIterations = 50;
	private static double initProp = 0.3;
	public static void main(String[] args) {
		int[][][] shapeNums = new int[iterations][simIterations][];
		EffGraph graph;
		int[][] compstates = new int[simSize][simSize];
		for(int i=0;i<compstates.length;i++)
			for(int j=0;j<compstates[i].length;j++)
				compstates[i][j] = 2;
		Side[][] setup = new Side[simSize][simSize];
		// iterations
		for(int iter=0;iter<iterations;iter++) {
			System.err.println("running iteration "+iter);
			// init setup
			for(int i=0;i<setup.length;i++)
				for(int j=0;j<setup[i].length;j++)
					setup[i][j] = Math.random()>initProp?Side.WEAK:Side.STRONG;
			graph = new EffGraph(setup,compstates);
			shapeNums[iter][0] = graph.count2shapesSym();
			for(int t=1;t<simIterations;t++) {
				graph.stepTorus();
				shapeNums[iter][t] = graph.count2shapesSym();
			}
		}
		// calculate statistics
		// means
		double mean[][] = new double[simIterations][6];
		for(int t=0;t<simIterations;t++) {
			for(int type=0;type<6;type++) {
				for(int iter=0;iter<iterations;iter++)
					mean[t][type] += shapeNums[iter][t][type];
				mean[t][type] /= iterations;
			}
		}
		// stdevs
		double[][] stdev=  new double[simIterations][6];
		for(int t=0;t<simIterations;t++) {
			for(int type=0;type<6;type++) {
				for(int iter=0;iter<iterations;iter++)
					stdev[t][type] += Math.pow(shapeNums[iter][t][type] - mean[t][type], 2);
				stdev[t][type] = Math.sqrt(stdev[t][type]/iterations);
			}
		}
		try {
			File outfile = new File("manySquarePropData30starting");
			PrintWriter outWriter = new PrintWriter(outfile);
			
			// print stats
			for(int t=0;t<simIterations;t++) {
				for(int type=0;type<6;type++)
					outWriter.print(mean[t][type] + "\t");
				outWriter.print("\t\t");
				for(int type=0;type<6;type++)
					outWriter.print(stdev[t][type] + "\t");
				outWriter.println();
			}
			
			outWriter.flush();
			outWriter.close();
			System.out.println("done writing to file");
		} catch(FileNotFoundException e) {
			System.err.println("file not found");
		}
	}
}
