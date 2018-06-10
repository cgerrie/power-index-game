package tests;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import simulation.EffGraph;
import simulation.Side;

public class CellEquivalenceHypothesisTest {
	private static final double startingStrongProportion = 0.5;
	private static final int dim = 100,
	                         iterations = 50;
	public static void main(String[] args) {
		// init setup
		Side[][] setup = new Side[dim][dim];
		randomizeSetup(setup, startingStrongProportion);
		// init compstates
		int[][] compstates = new int[dim][dim];
		for(int i=0;i<dim;i++)
			for(int j=0;j<dim;j++)
				compstates[i][j] = 2;
		// to init the graph
		EffGraph graph = new EffGraph(setup, compstates);
		double prop;
		for(int i=0;i<iterations;i++) {
			randomizeSetup(graph.sides, getStrongProportion(graph.sides));
			graph.stepTorus();
			System.out.println("i: "+i+"\tprop: "+getStrongProportion(graph.sides));
		}
	}
	public static double getStrongProportion(Side[][] setup) {
		int total = 0;
		double prop = 0;
		for(int i=0;i<setup.length;i++) {
			for(int j=0;j<setup[i].length;j++) {
				total++;
				if(setup[i][j].equals(Side.STRONG))
					prop++;
			}
		}
		return prop/total;
	}
	public static void randomizeSetup(Side[][] setup, double strongProportion) {
		// init coords list
		List<Integer[]> pairs = new ArrayList<>(),
		                strongPairs = new ArrayList<>(),
		                weakPairs = new ArrayList<>();
		for(int i=0;i<setup.length;i++)
			for(int j=0;j<setup[i].length;j++)
				pairs.add(new Integer[]{i,j});
		// choose strongs and weaks
		Random rand = new Random();
		int strongs = (int)(strongProportion*pairs.size()),
		    weaks = pairs.size()-strongs,
		    chosenIndex;
		for(;strongs>0;strongs--) {
			chosenIndex = rand.nextInt(pairs.size());
			strongPairs.add(pairs.get(chosenIndex));
			pairs.remove(chosenIndex);
		}
		for(;weaks>0;weaks--) {
			chosenIndex = rand.nextInt(pairs.size());
			weakPairs.add(pairs.get(chosenIndex));
			pairs.remove(chosenIndex);
		}
		for(Integer[] coord : strongPairs)
			setup[coord[0]][coord[1]] = Side.STRONG;
		for(Integer[] coord : weakPairs)
			setup[coord[0]][coord[1]] = Side.WEAK;
	}
}
