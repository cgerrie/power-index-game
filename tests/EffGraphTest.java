package tests;
import simulation.EffGraph;
import simulation.Side;

/* Charlie Gerrie 2018
 * 
 * This is a test of the new EffGraph simulator
 */

public class EffGraphTest {
	private static final Side STRONG = Side.STRONG,
	                          WEAK = Side.WEAK;
	public static void main(String[] args) {
		/*Side[][] sides = {{  null,  null,STRONG,STRONG,STRONG,  null,  null},
		                  {  null,STRONG,STRONG,STRONG,STRONG,STRONG,  null},
		                  {STRONG,STRONG,STRONG,  WEAK,STRONG,STRONG,STRONG},
		                  {STRONG,STRONG,STRONG,  WEAK,STRONG,STRONG,STRONG},
		                  {STRONG,STRONG,STRONG,  WEAK,STRONG,STRONG,STRONG},
		                  {  null,STRONG,STRONG,STRONG,STRONG,STRONG,  null},
		                  {  null,  null,STRONG,STRONG,STRONG,  null,  null}};
		int[][] computeStates = {{0,0,0,0,0,0,0},
		                         {0,0,1,1,1,0,0},
		                         {0,1,2,2,2,1,0},
		                         {0,1,2,2,2,1,0},
		                         {0,1,2,2,2,1,0},
		                         {0,0,1,1,1,0,0},
		                         {0,0,0,0,0,0,0}};*/
		Side[][] sides = {{STRONG,STRONG,STRONG},
		                  {WEAK,STRONG,WEAK},
		                  {WEAK,WEAK,WEAK}};
		int[][] computeStates = {{2,2,2},
		                         {2,2,2},
		                         {2,2,2}};
		EffGraph graph = new EffGraph(sides, computeStates);
		graph.stepTorus();
		// print out graph's sides
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				System.out.print((graph.sides[i][j]!=null?graph.sides[i][j]:"")
				                 +"\t");
			}
			System.out.println();
		}
	}
}