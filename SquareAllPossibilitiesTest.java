
public class SquareAllPossibilitiesTest {
	public static void main(String[] args) {
		Side[][] setup = new Side[6][6];
		int[][] compstate = {{0,0,0,0,0,0},
		                     {0,0,1,1,0,0},
		                     {0,1,2,2,1,0},
		                     {0,1,2,2,1,0},
		                     {0,0,1,1,0,0},
		                     {0,0,0,0,0,0}};
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
			graph.step();
			transitionCountMatrix[beforestate][graph.getShapeSym(2, 2)]++;
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
