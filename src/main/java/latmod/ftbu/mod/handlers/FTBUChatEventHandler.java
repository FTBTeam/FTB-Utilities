package latmod.ftbu.mod.handlers;

import cpw.mods.fml.common.eventhandler.*;
import ftb.lib.FTBLib;
import latmod.ftbu.api.ServerTickCallback;
import latmod.ftbu.mod.FTBUTicks;
import latmod.ftbu.world.*;
import latmod.lib.FastList;
import net.minecraft.event.*;
import net.minecraft.util.*;

public class FTBUChatEventHandler
{
	private static final String[] LINK_PREFIXES = { "http://", "https://" };
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onChatEvent(net.minecraftforge.event.ServerChatEvent e)
	{
		String[] msg = e.message.split(" ");
		
		FastList<String> links = new FastList<String>();
		
		for(String s : msg)
		{
			int index = getFirstLinkIndex(s);
			if(index != -1) links.add(s.substring(index).trim());
		}
		
		if(!links.isEmpty())
		{
			final IChatComponent line = new ChatComponentText("");
			boolean oneLink = links.size() == 1;
			
			for(int i = 0; i < links.size(); i++)
			{
				String link = links.get(i);
				IChatComponent c = new ChatComponentText(oneLink ? "[ Link ]" : ("[ Link #" + (i + 1) + " ]"));
				c.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(link)));
				c.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
				line.appendSibling(c);
				if(!oneLink) line.appendSibling(new ChatComponentText(" "));
			}
			
			line.getChatStyle().setColor(EnumChatFormatting.GOLD);
			
			FTBUTicks.addCallback(new ServerTickCallback()
			{
				public void onCallback()
				{
					for(LMPlayer p : LMWorldServer.inst.getAllOnlinePlayers())
					{ if(p.settings.chatLinks) FTBLib.printChat(p.getPlayer(), line); }
				}
			});
		}
	}
	
	private static int getFirstLinkIndex(String s)
	{
		for(int i = 0; i < LINK_PREFIXES.length; i++)
		{
			int idx = s.indexOf(LINK_PREFIXES[i]);
			if(idx > 0) return idx;
		}
		
		return -1;
	}
}