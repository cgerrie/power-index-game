import java.util.stream.Collectors;

/* Charlie Gerrie
 * 
 * This application generates a transition matrix of a graph, and can export an image of it
 */

public class MatrixGenerator {
	public static void main(String[] args) {
		// for each number of weaks/strongs
			// generate cycles
			// rank cycles
			// generate ordered list of arrangements to best present cycles
		// concatenate each list of arrangements
		// generate matrix for arrangements
		// export image
			// initialize Image
		int BOXSIZE = 7, XRES = 512*BOXSIZE+1, YRES = 512*BOXSIZE+1; // OFFSET?
		Image image = new Image(XRES,YRES,new byte[]{-1,-1,-1});
			// add lines
				// light-grey grid
		byte[] lgray = new byte[]{-16,-16,-16};
		for(int x=0;x<XRES;x+=BOXSIZE)
			image.drawVerticalLine(x, lgray);
		for(int y=0;y<YRES;y+=BOXSIZE)
			image.drawHorizontalLine(y, lgray);
				// cycles squares
		
				// number of weaks/strongs section separating lines
		byte[] green = new byte[]{0,-1,0};
		for(int i=1, j = BOXSIZE;i<=9;i++) {
			image.drawHorizontalLine(j, green);
			image.drawVerticalLine(j, green);
			j += BOXSIZE*Util.choose(9, i);
		}
			// fill in grid with matrix values (0->white, 1->black)
		boolean[][] mat = getMatrix();
		byte[] white = new byte[]{-1,-1,-1},
		       black = new byte[]{0,0,0};
		for(int i=0;i<512;i++) {
			for(int j=0;j<512;j++) {
				image.drawSquare(i*BOXSIZE+1, j*BOXSIZE+1, BOXSIZE-1, mat[j][511-i]?black:white);
			}
		}
			// add legends at top and side
		// output image
		image.writeToFile("testImage.png");
	}
	public static boolean[][] getMatrix() {
		boolean[][] mat = new boolean[512][512];
		String setup;
		// TODO I think I need to switch x and y
		int numberOfWeaks, x;
		for(int y=0;y<512;y++) {
			// find graph from y
			setup = String.format("%9s", Integer.toBinaryString(y)).replace(' ', '0');
			numberOfWeaks = Util.countZeroes(setup);
			Graph graph = new Graph(new Graph.GridGraph(3,3,false,false),
			                        Graph.SpecificSides.fromBinaryString(setup));
			// find next step
			Game.IterateGraph(graph);
			// translate into x
			x = 0;
			for(int i=0;i<9;i++)
				x += graph.vertices.get(i).side==Side.WEAK?Math.pow(2, i):0;
			// set all of y row to false except for x
			for(int i=0;i<512;i++)
				mat[i][y] = i==x;
		}
		return mat;
	}
	
}
