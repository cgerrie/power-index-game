package tests;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import simulation.EffGraph;
import simulation.Side;

public class SoundTest {
	static short[] header={0x5249,
	                       0x4646,
	                       0x4600,
	                       0x1f02,
	                       0x5741,
	                       0x5645,
	                       0x666d,
	                       0x7420,
	                       0x1000,
	                       0x0000,
	                       0x0100,
	                       0x0200,
	                       0x44ac,
	                       0x0000,
	                       0x10b1,
	                       0x0200,
	                       0x0400,
	                       0x1000,
	                       0x4c49,
	                       0x5354,
	                       0x1a00,
	                       0x0000,
	                       0x494e,
	                       0x464f,
	                       0x4953,
	                       0x4654,
	                       0x0e00,
	                       0x0000,
	                       0x4c61,
	                       0x7666,
	                       0x3537,
	                       0x2e32,
	                       0x312e,
	                       0x3130,
	                       0x3100,
	                       0x6461,
	                       0x7461}; // 16-bit signed pcm wav header
	public static void main(String[] args) throws IOException {
		int generationSkip = 50,
		    generations = 1500/generationSkip,
		    graphSize = 500;
		double proportion = 0.1;
		String file = "game.wav";
		
		short[][] data = new short[generations+1][graphSize*graphSize];
		
		Side[][] set = new Side[graphSize][graphSize];
		for(int i=0;i<set.length;i++)
			for(int j=0;j<set[i].length;j++)
				set[i][j] = Side.WEAK;//Math.random()>proportion?Side.WEAK:Side.STRONG;
		set[graphSize/2][graphSize/2] = Side.STRONG;
		set[graphSize/2+1][graphSize/2] = Side.STRONG;
		set[graphSize/2][graphSize/2+1] = Side.STRONG;
		set[graphSize/2+1][graphSize/2+1] = Side.STRONG;
		
		int[][] compstates = new int[graphSize][graphSize];
		for(int i=0;i<compstates.length;i++)
			for(int j=0;j<compstates[i].length;j++)
				compstates[i][j] = 11;
		EffGraph graph = new EffGraph(set,compstates);
		fillArrayWithGraphData(graph, data[0], (short)32677);
		for(int t=1;t<=generations;t++) {
			for(int i=0;i<generationSkip;i++)
				graph.stepTorus();
			fillArrayWithGraphData(graph, data[t], (short)32677);
		}
		
		short[] linearData = new short[(generations+1)*graphSize*graphSize];
		int i=0;
		for(int t=0;t<data.length;t++)
			for(int p=0;p<data[t].length;p++)
				linearData[i++] = data[t][p];
		writeWav(file, linearData);
		System.out.println("done");
	}
	private static void fillArrayWithGraphData(EffGraph graph, short[] data, short amplitude) {
		int i=0;
		for(int x=0;x<graph.sides.length;x++)
			for(int y=0;y<graph.sides.length;y++)
				data[i++] = graph.sides[x][y]==Side.STRONG?amplitude:(short)-amplitude;
	}
	public static void writeWav(String file, short[] data) throws IOException {
		int datalength = data.length;
		byte[] datalengthbytes={(byte)(datalength >> 24),
		                        (byte)(datalength >> 16),
		                        (byte)(datalength >> 8),
		                        (byte)(datalength)};
		// write data
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			
			//write header
			for(int i=0;i<header.length;i++)
				out.writeShort(header[i]);

			//write length in little endian
			for(int i=3;i>=0;i--)
				out.writeByte(datalengthbytes[i]);

			//write data
			for(int i=0;i<data.length;i++) {
				out.writeShort(data[i]);
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(out != null)
				out.close();
		}
	}
}
