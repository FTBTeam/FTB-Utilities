package latmod.ftbu.mod.client.minimap;

import java.nio.ByteBuffer;

import cpw.mods.fml.relauncher.*;
import ftb.lib.client.FTBLibClient;
import latmod.ftbu.world.LMWorldClient;
import latmod.lib.PixelBuffer;

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
			ByteBuffer bb = FTBLibClient.toByteBuffer(image.pixels, false);
			area.pixelBuffer = bb;
		}
		catch(Exception e)
		{ e.printStackTrace(); }
	}
}