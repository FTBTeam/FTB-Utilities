package latmod.ftbu.mod.client.minimap;

import latmod.ftbu.core.world.LMWorldClient;
import latmod.ftbu.mod.client.gui.GuiMinimap;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class ThreadMinimap extends Thread
{
	public final World worldObj;
	public final int startX, startY;
	public final int size;
	
	public ThreadMinimap(World w, int x, int y, int s)
	{
		super("LM_Minimap");
		setDaemon(true);
		worldObj = w;
		startX = x;
		startY = y;
		size = s;
	}
	
	public void run()
	{
		if(LMWorldClient.inst != null) try
		{
			Minimap m = Minimap.get(worldObj.provider.dimensionId);
			
			for(int y = 0; y < size; y++)
			for(int x = 0; x < size; x++)
			{
				MChunk c = m.loadChunk(startX + x, startY + y);
				
				for(int i = 0; i < 256; i++)
				{
					int bx = c.posX * 16 + (i % 16);
					int by = c.posY * 16 + (i / 16);
					int col = Minimap.getBlockColor(worldObj, bx, by);
					c.setPixel(bx, by, col);
				}
			}
			
			GuiMinimap.shouldRedraw = true;
		}
		catch(Exception e)
		{ e.printStackTrace(); }
		
		Minimap.stopThread();
	}
}