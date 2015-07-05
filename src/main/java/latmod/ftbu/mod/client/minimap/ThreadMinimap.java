package latmod.ftbu.mod.client.minimap;

import latmod.ftbu.core.net.*;
import latmod.ftbu.core.world.LMPlayer;
import latmod.ftbu.mod.client.gui.GuiMinimap;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class ThreadMinimap extends Thread
{
	public final World worldObj;
	public final int startX, startZ;
	public final int size;
	public final LMPlayer player;
	
	public ThreadMinimap(World w, int x, int z, int s, LMPlayer p)
	{
		super("LM_Minimap");
		worldObj = w;
		startX = x;
		startZ = z;
		size = s;
		player = p;
		
		MessageLM.NET.sendToServer(new MessageAreaRequest(startX - 1, startZ - 1, worldObj.provider.dimensionId, (byte)(size + 2), player.playerID));
	}
	
	public void run()
	{
		try
		{
			Minimap m = Minimap.get(worldObj.provider.dimensionId);
			
			for(int z = 0; z < size; z++)
			for(int x = 0; x < size; x++)
			{
				int chunkX = startX + x;
				int chunkZ = startZ + z;
				
				int[] pixels = new int[16 * 16];
				for(int i = 0; i < pixels.length; i++)
				{
					int bx = chunkX * 16 + (i % 16);
					int bz = chunkZ * 16 + (i / 16);
					pixels[i] = Minimap.getBlockColor(worldObj, bx, bz);
				}
				
				m.setChunkPixels(chunkX, chunkZ, pixels);
			}
			
			GuiMinimap.shouldRedraw = true;
		}
		catch(Exception e)
		{ e.printStackTrace(); }
		
		Minimap.stopThread();
	}
}