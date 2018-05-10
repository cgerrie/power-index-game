import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/* Charlie Gerrie
 * 
 * This class if for generating values for a markov chain describing transitions between number of weaks on a graph
 */
public class MarkovGenerator {
	public static void main(String[] args) {
		try {
			File outfile = new File("markovDataTaurus");
			PrintWriter outWriter = new PrintWriter(outfile);
			int[][] chain = getMarkov();
			for(int i=0;i<10;i++) {
				for(int j=0;j<10;j++) {
					System.out.print(chain[i][j]+"\t");
					outWriter.print(chain[i][j]+"\t");
				}
				System.out.println();
				outWriter.println();
			}
			outWriter.flush();
			outWriter.close();
			System.out.println("done writing to file");
		} catch(FileNotFoundException e) {
			System.err.println("file not found");
		}
	}
	public static int[][] getMarkov() {
		int[][] chain = new int[10][10];
		for(int i=0;i<10;i++) for(int j=0;j<10;j++) chain[i][j] = 0;
		String setup;
		int numberOfWeaksBefore, numberOfWeaksAfter;
		for(int y=0;y<512;y++) {
			// find graph from y
			setup = String.format("%9s", Integer.toBinaryString(y)).replace(' ', '0');
			numberOfWeaksBefore = Util.countZeroes(setup);
			Graph graph = new Graph(new Graph.GridGraph(3,3,true,true),
			                        Graph.SpecificSides.fromBinaryString(setup));
			Game.IterateGraph(graph);
			numberOfWeaksAfter = 0;
			for(Vertex v : graph.vertices)
				if(v.side==Side.WEAK)
					numberOfWeaksAfter++;
			chain[numberOfWeaksBefore][numberOfWeaksAfter]++;
		}
		return chain;
	}
}
