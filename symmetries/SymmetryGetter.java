package symmetries;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import simulation.Side;

public class SymmetryGetter {
	public static HashMap<SymmetryCounter.Arrangement, List<Side[][]>> getMapping(int xres, int yres) {
		// configure SymmetryCounter
		SymmetryCounter.xres = xres;
		SymmetryCounter.yres = yres;
		// init map
		HashMap<SymmetryCounter.Arrangement, List<Side[][]>> map = new HashMap<>();
		// iterate through all setups to find arrangements
		long max = 1 << (xres*yres);
		Side[][] graph;
		SymmetryCounter.Arrangement currentArrangement;
		LinkedList<Side[][]> newList;
		for(long state=0;state<max;state++) {
			graph = SymmetryCounter.getGridFromIter(state);
			currentArrangement = new SymmetryCounter.Arrangement(graph, xres, yres);
			if(map.containsKey(currentArrangement))
				map.get(currentArrangement).add(graph);
			else {
				newList = new LinkedList<>();
				newList.add(graph);
				map.put(currentArrangement, newList);
			}
		}
		return map;
	}
	public static void main(String[] args) {
		/*SymmetryCounter.Arrangement ar1 = new SymmetryCounter.Arrangement(SymmetryCounter.getGridFromIter(0x0b9), 3, 3),
		                            ar2 = new SymmetryCounter.Arrangement(SymmetryCounter.getGridFromIter(0x146), 3, 3);
		HashMap<SymmetryCounter.Arrangement, List<Side[][]>> map = new HashMap<>();
		System.out.println(ar1+"\n"+ar2+"\n"+ar1.equals(ar2));
		map.put(ar1, new LinkedList<>());
		System.out.println(map.containsKey(ar2));*/
		SymmetryCounter.checkSideSym = false;
		System.out.println(getMapping(3,3).size());
		for(SymmetryCounter.Arrangement e : getMapping(3,3).keySet())
			System.out.println(e);
	}
}
