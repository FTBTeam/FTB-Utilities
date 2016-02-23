package ftb.utils.mod.handlers;

import ftb.lib.FTBLib;
import ftb.lib.api.ServerTickCallback;
import ftb.lib.api.players.*;
import ftb.utils.world.*;
import net.minecraft.event.*;
import net.minecraft.util.*;
import net.minecraftforge.fml.common.eventhandler.*;

import java.util.ArrayList;

public class FTBUChatEventHandler
{
	private static final String[] LINK_PREFIXES = {"http://", "https://"};
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onChatEvent(net.minecraftforge.event.ServerChatEvent e)
	{
		String[] msg = FTBLib.removeFormatting(e.message).split(" "); // https://github.com/LatvianModder
		
		ArrayList<String> links = new ArrayList<>();
		
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
			
			FTBLib.addCallback(new ServerTickCallback()
			{
				public void onCallback()
				{
					for(LMPlayer p : LMWorldMP.inst.getOnlinePlayers())
					{
						if(FTBUPlayerDataMP.get(p.toPlayerMP()).getFlag(FTBUPlayerData.CHAT_LINKS))
						{
							p.getPlayer().addChatMessage(line);
						}
					}
				}
			});
		}
	}
	
	private static int getFirstLinkIndex(String s)
	{
		for(String s1 : LINK_PREFIXES)
		{
			int idx = s.indexOf(s1);
			if(idx != -1) return idx;
		}
		
		return -1;
	}
}