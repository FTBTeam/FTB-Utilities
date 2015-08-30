package latmod.ftbu.mod.client.minimap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import latmod.ftbu.core.util.*;
import latmod.ftbu.core.world.LMWorldClient;

import org.lwjgl.opengl.*;

public class MArea
{
	public static final int size_c = 32;
	public static final int size = size_c * 16;
	public static final int size_sq = size * size;
	
	public final Minimap minimap;
	public final int posX, posY;
	public final FastMap<Short, MChunk> chunks;
	public final long index;
	public final File file;
	
	public boolean isDirty = true;
	public int textureID = -1;
	public ThreadReloadArea thread = null;
	public ByteBuffer pixelBuffer = null;
	public long lastRefreshMillis = -1L;
	
	public MArea(Minimap m, int x, int y)
	{
		minimap = m;
		posX = x;
		posY = y;
		chunks = new FastMap<Short, MChunk>();
		index = Bits.intsToLong(posX, posY);
		file = new File(LMWorldClient.inst.clientDataFolder, "minimap/" + minimap.dim + "," + posX + "," + posY + ".png");
		load();
	}
	
	public static long getIndexC(int cx, int cy)
	{
		int x = cx / size_c;
		int y = cy / size_c;
		if(cx < 0) x -= 1;
		if(cy < 0) y -= 1;
		return Bits.intsToLong(x, y);
	}
	
	public static long getIndexB(double x, double y)
	{ return getIndexC(MathHelperLM.chunk(x), MathHelperLM.chunk(y)); }
	
	public void setTexture()
	{
		if(textureID == -1)
			textureID = GL11.glGenTextures();
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		
		if(isDirty)
		{
			long ms = LMUtils.millis();
			
			if(ms - lastRefreshMillis >= 2000L)
			{
				lastRefreshMillis = ms;
				if(thread == null) thread = null;
				thread = new ThreadReloadArea(this);
				thread.start();
				isDirty = false;
			}
		}
		
		if(pixelBuffer != null)
		{
			int filter = Minimap.blur.getB() ? GL11.GL_LINEAR : GL11.GL_NEAREST;
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, size, size, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixelBuffer);
			pixelBuffer = null;
			thread = null;
		}
	}
	
	public PixelBuffer toPixelBuffer()
	{
		PixelBuffer image = new PixelBuffer(size, size);
		
		for(int i = 0; i < chunks.values.size(); i++)
		{
			MChunk c = chunks.values.get(i);
			image.setRGB(c.rposX * 16, c.rposY * 16, 16, 16, c.pixels, 0, 16);
		}
		
		return image;
	}
	
	public void load()
	{
		if(file.exists())
		{
			Thread thread = new Thread()
			{
				public void run()
				{
					try
					{
						BufferedImage image = ImageIO.read(file);
						if(image != null && image.getWidth() == size && image.getHeight() == size)
						{
							for(int y = 0; y < size_c; y++)
							for(int x = 0; x < size_c; x++)
							{
								int[] pixelsTemp = image.getRGB(x * 16, y * 16, 16, 16, null, 0, 16);
								
								for(int i = 0; i < 256; i++)
								{
									if(pixelsTemp[i] != 0)
									{
										MChunk c = minimap.loadChunk(posX * size_c + x, posY * size_c + y);
										System.arraycopy(pixelsTemp, 0, c.pixels, 0, 256);
										break;
									}
								}
							}
						}
					}
					catch(Exception e)
					{ e.printStackTrace(); }
				}
			};
			
			thread.start();
		}
	}

	public void save()
	{
		Thread thread = new Thread()
		{
			public void run()
			{
				try
				{
					BufferedImage image = toPixelBuffer().toImage(BufferedImage.TYPE_INT_ARGB);
					ImageIO.write(image, "PNG", LMFileUtils.newFile(file));
				}
				catch(Exception e)
				{ e.printStackTrace(); }
			}
		};
		
		thread.start();
	}
}