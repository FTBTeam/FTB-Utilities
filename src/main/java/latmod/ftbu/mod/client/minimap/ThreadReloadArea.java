package latmod.ftbu.mod.client.minimap;

import java.nio.ByteBuffer;

import latmod.ftbu.core.util.PixelBuffer;
import latmod.ftbu.core.world.LMWorldClient;
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
			PixelBuffer image = area.toPixelBuffer();
			ByteBuffer bb = image.toByteBuffer(false);
			area.pixelBuffer = bb;
		}
		catch(Exception e)
		{ e.printStackTrace(); }
	}
}