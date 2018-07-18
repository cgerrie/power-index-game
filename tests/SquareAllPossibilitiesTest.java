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
		double[][] transitionProbabilityMatrix = new double[6][6];
		// iterations
		int p, beforestate, thisShape;
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
			/*if(props[0]>=1 && props[0]<=3 && // white square
			   props[1]>=1 && props[1]<=3 && // black square
			   props[2]>=3 && props[2]<=6 && // side
			   props[3]==0 &&                // diag
			   props[4]>=1 && props[4]<=3 && // white corner
			   props[5]>=1 && props[5]<=3)   // black corner
			{*/
				graph.step();
				thisShape = graph.getShapeSym(2, 2);
				transitionProbabilityMatrix[beforestate][thisShape]
					+= Math.exp(-Math.abs(props[0]-getPropForShape(thisShape,0)*13)
					            -Math.abs(props[1]-getPropForShape(thisShape,1)*13)
					            -Math.abs(props[2]-getPropForShape(thisShape,2)*13)
					            -Math.abs(props[3]-getPropForShape(thisShape,3)*13)
					            -Math.abs(props[4]-getPropForShape(thisShape,4)*13)
					            -Math.abs(props[5]-getPropForShape(thisShape,5)*13));
			//}
		}
		// normalize matrix
		double sum;
		for(int i=0;i<transitionProbabilityMatrix.length;i++) {
			sum = 0;
			for(int j=0;j<transitionProbabilityMatrix[i].length;j++)
				sum += transitionProbabilityMatrix[i][j];
			for(int j=0;j<transitionProbabilityMatrix[i].length;j++)
				transitionProbabilityMatrix[i][j] /= sum;	
		}
		// output transitionCountMatrix
		for(int i=0;i<6;i++) {
			for(int j=0;j<6;j++) {
				System.out.print(transitionProbabilityMatrix[i][j]+"\t");
			}
			System.out.println();
		}
	}
	private static double getPropForShape(int centre, int neighbor) {
		if(centre == 0) {
			if(neighbor == 0)      return 0.2873;
			else if(neighbor == 1) return 0.0479;
			else if(neighbor == 2) return 0.2966;
			else if(neighbor == 3) return 0.0171;
			else if(neighbor == 4) return 0.1258;
			else if(neighbor == 5) return 0.2253;
		} else if(centre == 1) {
			if(neighbor == 0)      return 0.0462;
			else if(neighbor == 1) return 0.2877;
			else if(neighbor == 2) return 0.2969;
			else if(neighbor == 3) return 0.0162;
			else if(neighbor == 4) return 0.2228;
			else if(neighbor == 5) return 0.1300;
		} else if(centre == 2) {
			if(neighbor == 0)      return 0.1233;
			else if(neighbor == 1) return 0.1280;
			else if(neighbor == 2) return 0.3432;
			else if(neighbor == 3) return 0.0229;
			else if(neighbor == 4) return 0.1898;
			else if(neighbor == 5) return 0.1928;
		} else if(centre == 3) {
			if(neighbor == 0)      return 0.1238;
			else if(neighbor == 1) return 0.1219;
			else if(neighbor == 2) return 0.4000;
			else if(neighbor == 3) return 0.0327;
			else if(neighbor == 4) return 0.1673;
			else if(neighbor == 5) return 0.1543;
		} else if(centre == 4) {
			if(neighbor == 0)      return 0.0910;
			else if(neighbor == 1) return 0.1672;
			else if(neighbor == 2) return 0.3305;
			else if(neighbor == 3) return 0.0167;
			else if(neighbor == 4) return 0.2027;
			else if(neighbor == 5) return 0.1918;
		} else if(centre == 5) {
			if(neighbor == 0)      return 0.1610;
			else if(neighbor == 1) return 0.0963;
			else if(neighbor == 2) return 0.3315;
			else if(neighbor == 3) return 0.0152;
			else if(neighbor == 4) return 0.1893;
			else if(neighbor == 5) return 0.2067;
		}
		// else
		return -1;
	}
}
