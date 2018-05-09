/* Charlie Gerrie
 * 
 * This application generates a transition matrix of a graph, and can export an image of it
 */

import java.awt.Point;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class MatrixGenerator {

	public static void main(String[] args) {
		// for each number of weaks/strongs
			// generate cycles
			// rank cycles
			// generate ordered list of arrangements to best present cycles
		// concatenate each list of arrangements
		// generate matrix for arrangements
		// export image
			// initialize 3-array
			// add lines
				// light-grey grid
				// cycles squares
				// number of weaks/strongs section separating lines
			// fill in grid with matrix values (1->white, 0->black)
			// add legends at top and side
	}
	public static void writeToImage(String fileLocation, byte[][][] pixels) {
		final int xres = pixels.length,
		          yres = pixels[0].length;
		byte[] outputPixels1d = new byte[3*xres*yres];
		int offset = 0;
		for(int y=0;y<yres;y++) {
			//System.out.println("y:"+y+"/"+yres);
			for(int x=0;x<xres;x++) {
				outputPixels1d[offset++] = pixels[x][y][0];
				outputPixels1d[offset++] = pixels[x][y][1];
				outputPixels1d[offset++] = pixels[x][y][2];
			}
		}
		// write image
		try {
			//InputStream in = new ByteArrayInputStream(outputPixels1d);
			DataBuffer buffer = new DataBufferByte(outputPixels1d, outputPixels1d.length);
			WritableRaster raster = Raster.createInterleavedRaster(buffer, xres, yres, 3*xres, 3, new int[] {0, 1, 2}, (Point)null);
			ColorModel cm = new ComponentColorModel(ColorModel.getRGBdefault().getColorSpace(), false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
			BufferedImage outputImg = new BufferedImage(cm, raster, true, null);
			ImageIO.write(outputImg,"png", new File(fileLocation));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
