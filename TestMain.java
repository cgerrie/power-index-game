
public class TestMain {
	public static void main(String[] args) {
		Graph testGraph = new Graph(new Graph.GridGraph(3,3,false,false), new Graph.RandomSides());	
		int i=0;
		for(Vertex v : testGraph.vertices) {
			System.out.println(v);
			System.out.println("i:"+(i++));
		}
	}
}
