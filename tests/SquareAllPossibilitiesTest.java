package tests;
import simulation.EffGraph;
import simulation.Side;

public class SquareAllPossibilitiesTest {
	public static void main(String[] args) {
		Side[][] setup = new Side[6][6];
		int[][] compstate = {{ 0, 0, 8, 0, 0, 0},
		                     { 0, 8, 9, 9, 0, 0},
		                     { 8, 9,10,10, 9, 0},
		                     { 0, 9,10,10, 1, 0},
		                     { 0, 0, 9, 1, 0, 0},
		                     { 0, 0, 0, 0, 0, 0}};
		boolean[][] iterstate = {{false,false, true, true,false,false},
		                         {false, true, true, true, true,false},
		                         { true, true, true, true, true, true},
		                         { true, true, true, true, true, true},
		                         {false, true, true, true, true,false},
		                         {false,false, true, true,false,false}};
		EffGraph graph;
		int[][] transitionCountMatrix = new int[6][6];
		// iterations
		int p, beforestate;
		int[] props;
		for(int setupi=0;setupi<(1 << 24);setupi++) {
			if(setupi*100%(1 << 24)==0)
				System.err.println((setupi*100/(1 << 24))+"% done");
			// initialize setup
			p=0;
			for(int i=0;i<6;i++)
				for(int j=0;j<6;j++)
					if(iterstate[i][j])
						setup[i][j] = (1 & (setupi >> (p++)))!=0?Side.STRONG:Side.WEAK;
			// init graph
			graph = new EffGraph(setup,compstate);
			beforestate = graph.getShapeSym(2, 2);
			// specific proportions (test that 2-square props are within 95% confidence intervals)
			props = graph.count2ShapesSymSpecified();
			if(props[0]>=1 && props[0]<=3 && // white square
			   props[1]>=1 && props[1]<=3 && // black square
			   props[2]>=3 && props[2]<=6 && // side
			   props[3]==0 &&                // diag
			   props[4]>=1 && props[4]<=3 && // white corner
			   props[5]>=1 && props[5]<=3)   // black corner
			{
				graph.step();			
				transitionCountMatrix[beforestate][graph.getShapeSym(2, 2)]++;
			}
		}
		// output transitionCountMatrix
		for(int i=0;i<6;i++) {
			for(int j=0;j<6;j++) {
				System.out.print(transitionCountMatrix[i][j]+"\t");
			}
			System.out.println();
		}
	}
}
