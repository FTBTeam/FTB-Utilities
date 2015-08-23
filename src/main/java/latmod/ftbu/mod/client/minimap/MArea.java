package latmod.ftbu.mod.client.minimap;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Arrays;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.util.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class MArea
{
	public static final int size_c = 128;
	public static final int size = size_c * 16;
	public static final int size_sq = size * size;
	
	public final Minimap minimap;
	public final int posX, posY;
	public final FastMap<Short, MChunk> chunks;
	public final long index;
	
	public boolean isDirty = true;
	public int textureID = -1;
	
	public MArea(Minimap m, int x, int y)
	{
		minimap = m;
		posX = x;
		posY = y;
		chunks = new FastMap<Short, MChunk>();
		index = Bits.intToLong(posX, posY);
	}
	
	public static long getIndexC(int cx, int cy)
	{
		int x = cx / size_c;
		int y = cy / size_c;
		if(cx < 0) x -= 1;
		if(cy < 0) y -= 1;
		return Bits.intToLong(x, y);
	}
	
	public static long getIndexB(int bx, int by)
	{ return getIndexC(MathHelperLM.chunk(bx), MathHelperLM.chunk(by)); }
	
	public BufferedImage generateImage()
	{
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		int[] pixels = new int[size_sq];
		Arrays.fill(pixels, 0xFF000000);
		
		image.setRGB(0, 0, size, size, pixels, 0, size);
		for(MChunk c : chunks.values)
		{
			int[] pixels1 = new int[256];
			Arrays.fill(pixels1, LMColorUtils.getRGBA(LatCoreMC.rand.nextInt(), 255));
			image.setRGB(c.rposX * 16, c.rposY * 16, 16, 16, c.pixels, 0, 16);
		}
		
		return image;
	}
	
	public void setTexture()
	{
		if(textureID == -1)
			textureID = GL11.glGenTextures();
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		
		if(isDirty)
		{
			isDirty = false;
			
			ByteBuffer dataBuffer = BufferUtils.createByteBuffer(size_sq * 4);
			
			BufferedImage image = generateImage();
			int pixels[] = image.getRGB(0, 0, size, size, null, 0, size);
			
			for(int i = 0; i < size_sq; i++)
			{
				dataBuffer.put((byte)LMColorUtils.getRed(pixels[i]));
				dataBuffer.put((byte)LMColorUtils.getGreen(pixels[i]));
				dataBuffer.put((byte)LMColorUtils.getBlue(pixels[i]));
				dataBuffer.put((byte)255);
			}
			
			dataBuffer.flip();
			
			int filter = Minimap.blur.getB() ? GL11.GL_LINEAR : GL11.GL_NEAREST;
			
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, size, size, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, dataBuffer);
		}
	}
}