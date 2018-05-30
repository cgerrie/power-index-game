/* Charlie Gerrie 2018
 * 
 * This application
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class ProportionTestMain {
	public static void main(String[] args) {
		int runs = 5, iterationsPer = 50;
		try {
			File outfile = new File("data10x10Proportions");
			PrintWriter outWriter = new PrintWriter(outfile);
			Graph testGraph;
			double proportion;
			int weaks, strongs;
			outWriter.print("proportion\t");
			for(int i=0;i<runs;i++)
				outWriter.print("run "+i+"\t");
			outWriter.println();
			for(double p = 0.10; p<=0.26; p += 0.01) {
				outWriter.printf("%.2f\t",p);
				for(int i=0;i<runs;i++) {
					System.out.println("testing proportion "+p+" run #"+i);
					testGraph = new Graph(new Graph.GridGraph(10,10,true,true), new Graph.RandomSides(p));
					Game.CalculateWeights(testGraph);
					for(int j=0;j<iterationsPer;j++) {
						weaks = 0;
						strongs = 0;
						for(Vertex v : testGraph.vertices) {
							switch(v.side) {
							case WEAK: weaks++;
							           break;
							case STRONG: strongs++;
							             break;
							}
						}
						proportion = (double)strongs/(weaks+strongs);
						outWriter.print(proportion+"\t");
						
						Game.IterateGraph(testGraph);
					}
					outWriter.println();
				}
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
