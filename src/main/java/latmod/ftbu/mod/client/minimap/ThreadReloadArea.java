package latmod.ftbu.mod.client.minimap;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import latmod.ftbu.core.util.LMColorUtils;
import latmod.ftbu.core.world.LMWorldClient;

import org.lwjgl.BufferUtils;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class ThreadReloadArea extends Thread
{
	public final MArea area;
	
	public ThreadReloadArea(MArea a)
	{
		super("LMMA_" + a.posX + "_" + a.posY);
		area = a;
	}
	
	public void run()
	{
		if(LMWorldClient.inst != null) try
		{
			ByteBuffer bb = BufferUtils.createByteBuffer(MArea.size_sq * 4);
			BufferedImage image = area.toImage();
			int[] pixels = image.getRGB(0, 0, MArea.size, MArea.size, null, 0, MArea.size);
			
			for(int i = 0; i < MArea.size_sq; i++)
			{
				bb.put((byte)LMColorUtils.getRed(pixels[i]));
				bb.put((byte)LMColorUtils.getGreen(pixels[i]));
				bb.put((byte)LMColorUtils.getBlue(pixels[i]));
				bb.put((byte)255);
			}
			
			bb.flip();
			area.pixelBuffer = bb;
		}
		catch(Exception e)
		{ e.printStackTrace(); }
	}
}