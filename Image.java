/* Charlie Gerrie
 * 
 * This class represents an image, and contains methods to export it to a file
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

public class Image {
	public byte[][][] pixels;
	public final int xres, yres;
	public Image(int x, int y) {
		pixels = new byte[x][y][3];
		xres = x;
		yres = y;
	}
	public void drawVerticalLine(int x, byte[] color) {
		for(int i=0;i<yres;i++) {
			pixels[x][i][0] = color[0];
			pixels[x][i][1] = color[1];
			pixels[x][i][2] = color[2];
		}
	}
	public void drawHorizontalLine(int y, byte[] color) {
		for(int i=0;i<xres;i++) {
			pixels[i][y][0] = color[0];
			pixels[i][y][1] = color[1];
			pixels[i][y][2] = color[2];
		}
	}
	public void writeToImage(String fileLocation) {
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
