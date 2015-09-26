package latmod.ftbu.mod.client.minimap;

import java.nio.ByteBuffer;

import cpw.mods.fml.relauncher.*;
import latmod.core.util.PixelBuffer;
import latmod.ftbu.util.client.LatCoreMCClient;
import latmod.ftbu.world.LMWorldClient;

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
			PixelBuffer image = area.toPixelBuffer();
			ByteBuffer bb = LatCoreMCClient.toByteBuffer(image.pixels, false);
			area.pixelBuffer = bb;
		}
		catch(Exception e)
		{ e.printStackTrace(); }
	}
}