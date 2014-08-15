package latmod.core.mod;
import java.io.*;
import java.util.UUID;

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
		UUID id = e.player.getUniqueID();
		
		LC.logger.info("UUID: " + id);
		
		JsonPlayer p = LMSecurity.getPlayer(id);
		if(p == null)
		{
			p = new JsonPlayer();
			p.whitelist = new FastList<String>();
			p.blacklist = new FastList<String>();
			p.uuid = id.toString();
			p.displayName = e.player.getCommandSenderName();
			
			LMSecurity.list.players.add(p);
		}
		else
		{
			p.displayName = e.player.getCommandSenderName();
		}
		
		if(LC.teamLatModUUIDs.contains(e.player.getUniqueID()))
			LatCore.printChat(e.player, "Hello, Team LatMod member!");
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
					
					if(s.length() > 0 && s.startsWith("{") && s.endsWith("}"))
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
				if(LMSecurity.list == null)
					LMSecurity.list = new JsonPlayerList();
				
				if(LMSecurity.list.players == null)
					LMSecurity.list.players = new FastList<JsonPlayer>();
				
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