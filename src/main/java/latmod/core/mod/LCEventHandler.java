package latmod.core.mod;
import java.io.*;
import java.util.*;

import latmod.core.*;
import latmod.core.security.*;
import latmod.core.util.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class LCEventHandler
{
	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent e)
	{
		if(e.showAdvancedItemTooltips && e.itemStack != null && e.itemStack.getItem() != null)
		{
			if(LC.config.general.addOreNames)
			{
				FastList<String> ores = ODItems.getOreNames(e.itemStack);
				
				if(ores != null && !ores.isEmpty())
				{
					e.toolTip.add("Ore Dictionary names:");
					for(String or : ores)
					e.toolTip.add("> " + or);
				}
			}
			
			if(LC.config.general.addRegistryNames)
			{
				e.toolTip.add("Registry name:");
				e.toolTip.add(LMUtils.getRegName(e.itemStack.getItem(), true));
			}
		}
	}
	
	@SubscribeEvent
	public void playerJoined(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent e)
	{
		UUID id = e.player.getUniqueID();
		LC.logger.info("UUID: " + id);
		
		JsonPlayer p = JsonPlayer.getPlayer(id);
		if(p == null)
		{
			p = new JsonPlayer();
			p.whitelist = new ArrayList<String>();
			p.blacklist = new ArrayList<String>();
			p.uuid = id.toString();
			p.displayName = e.player.getCommandSenderName();
			
			JsonPlayer.list.players.add(p);
		}
		else
		{
			p.displayName = e.player.getCommandSenderName();
		}
		
		if(EnumLatModTeam.TEAM.uuids.contains(e.player.getUniqueID()))
			LatCore.printChat(e.player, "Hello, Team LatMod member!");
		
		if(LC.config.general.notifyUpdates)
		{
			FastList<String> toPrint = new FastList<String>();
			
			for(int i = 0; i < LC.versionsToCheck.size(); i++)
			{
				String mod_id = LC.versionsToCheck.keys.get(i);
				String mod_version = LC.versionsToCheck.values.get(i);
				
				Map<String, String> m = LC.versionsFile.get(mod_id);
				
				if(m != null && m.size() > 0)
				{
					String[] versions = m.keySet().toArray(new String[0]);
					
					if(versions.length > 0)
					{
						if(versions.length > 1) Arrays.sort(versions, ThreadCheckVersions.comparator);
						
						String lver = versions[0];
						
						if(!lver.equals(mod_version))
						{
							if(toPrint.isEmpty()) toPrint.add("These LatvianModder's mods has updates:");
							toPrint.add(mod_id + EnumChatFormatting.GOLD + " [ " + lver + " ]: " + EnumChatFormatting.GRAY + m.get(lver));
						}
					}
				}
			}
			
			if(!toPrint.isEmpty()) for(String s : toPrint)
				LatCore.printChat(e.player, s);
		}
	}
	
	@SubscribeEvent
	public void worldLoaded(WorldEvent.Load e)
	{
		if(LatCore.canUpdate() && e.world.provider.dimensionId == 0)
		{
			File f = LMCommon.newFile(new File(e.world.getSaveHandler().getWorldDirectory(), "LatCoreMC.json"));
			
			JsonPlayer.list = new JsonPlayerList();
			JsonPlayer.list.players = new FastList<JsonPlayer>();
			
			if(f.exists())
			{
				try
				{
					FileInputStream fis = new FileInputStream(f);
					byte[] b = new byte[fis.available()];
					fis.read(b); fis.close();
					
					String s = new String(b);
					
					if(s.length() > 0 && s.startsWith("{") && s.endsWith("}"))
						JsonPlayer.list = LMUtils.fromJson(s, JsonPlayerList.class);
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
			File f = LMCommon.newFile(new File(e.world.getSaveHandler().getWorldDirectory(), "LatCoreMC.json"));
			
			try
			{
				if(JsonPlayer.list == null)
					JsonPlayer.list = new JsonPlayerList();
				
				if(JsonPlayer.list.players == null)
					JsonPlayer.list.players = new FastList<JsonPlayer>();
				
				String s = LMUtils.toJson(JsonPlayer.list, true);
				
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(s.getBytes());
				fos.close();
			}
			catch(Exception ex)
			{ ex.printStackTrace(); }
		}
	}
	
	@SubscribeEvent
	public void playerName(PlayerEvent.NameFormat e)
	{
		JsonPlayer jp = JsonPlayer.getPlayer(e.entityPlayer);
		if(jp != null && jp.customName != null && jp.customName.length() > 0)
		{
			String s = jp.customName + "";
			e.displayname = s.trim().replace("&", "\u00a7").replace("%name%", jp.displayName);
		}
	}
}