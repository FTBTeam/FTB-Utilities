package latmod.core.mod;
import java.io.*;

import latmod.core.*;
import latmod.core.security.*;
import latmod.core.util.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class LCEventHandler
{
	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent e)
	{
		if(e.showAdvancedItemTooltips && e.itemStack != null)
		{
			FastList<String> ores = ODItems.getOreNames(e.itemStack);
			
			if(ores != null && !ores.isEmpty())
			{
				e.toolTip.add("Ore Dictionary names:");
				for(String or : ores)
				e.toolTip.add("> " + or);
			}
		}
	}
	
	@SubscribeEvent
	public void playerJoined(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent e)
	{
		LC.logger.info("UUID: " + e.player.getUniqueID());
		LC.logger.info("P_UUID: " + e.player.getPersistentID());
	}
	
	@SubscribeEvent
	public void worldLoaded(WorldEvent.Load e)
	{
		if(LatCore.canUpdate() && e.world.provider.dimensionId == 0)
		{
			File f = LMCommon.newFile(new File(e.world.getSaveHandler().getWorldDirectory(), "LatCoreMC.dat"));
			
			LMSecurity.list = new JsonPlayerList();
			LMSecurity.list.players = new FastList<JsonPlayer>();
			
			if(f.exists())
			{
				try
				{
					FileInputStream fis = new FileInputStream(f);
					byte[] b = new byte[fis.available()];
					fis.read(b); fis.close();
					
					String s = new String(b);
					LMSecurity.list = LMUtils.fromJson(s, JsonPlayerList.class);
				}
				catch(Exception ex)
				{ ex.printStackTrace(); }
			}
		}
	}
	
	@SubscribeEvent
	public void worldSaved(WorldEvent.Save e)
	{
		if(LatCore.canUpdate() && e.world.provider.dimensionId == 0)
		{
			File f = LMCommon.newFile(new File(e.world.getSaveHandler().getWorldDirectory(), "LatCoreMC.dat"));
			
			try
			{
				String s = LMUtils.toJson(LMSecurity.list, true);
				
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(s.getBytes());
				fos.close();
			}
			catch(Exception ex)
			{ ex.printStackTrace(); }
		}
	}
}