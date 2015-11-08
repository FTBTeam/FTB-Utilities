package latmod.ftbu.mod.config;

import java.util.UUID;

import ftb.lib.FTBLib;
import ftb.lib.api.config.ConfigSyncRegistry;
import ftb.lib.item.ItemStackTypeAdapter;
import latmod.ftbu.api.guide.GuideInfo;
import latmod.ftbu.mod.FTBU;
import latmod.lib.FastList;
import latmod.lib.config.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

public class FTBUConfigLogin
{
	public static final ConfigGroup group = new ConfigGroup("login");
	
	@GuideInfo(info = "Message of the day. This will be displayed when player joins the server.", def = "Blank")
	public static final ConfigEntryStringArray motd = new ConfigEntryStringArray("motd", FastList.asList("Welcome to the server!"));
	
	@GuideInfo(info = "Rules link you can click on. This will be displayed when player joins the server.", def = "Blank")
	public static final ConfigEntryString rules = new ConfigEntryString("rules", "");
	
	@GuideInfo(info = "URL for per-server custom badges file (Json). Example can be seen here: http://pastebin.com/raw.php?i=ZXVhpEZ1 .", def = "Blank")
	public static final ConfigEntryString customBadges = new ConfigEntryString("customBadges", "");
	
	@GuideInfo(info = "Items to give player when it first joins the server. Format: StringID Size Metadata, does not support NBT yet.", def = "minecraft:apple 16 0")
	public static final ConfigEntryStringArray startingItems = new ConfigEntryStringArray("startingItems", FastList.asList("minecraft:apple 16 0"));
	
	public static void load(ConfigFile f)
	{
		group.addAll(FTBUConfigLogin.class);
		f.add(group);
		
		ConfigSyncRegistry.add(customBadges);
	}
	
	public static FastList<ItemStack> getStartingItems(UUID id)
	{
		FastList<ItemStack> list = new FastList<ItemStack>();
		
		for(String s : startingItems.get())
		{
			ItemStack is = ItemStackTypeAdapter.parseItem(s);
			if(is != null) list.add(is);
		}
		
		return list;
	}
	
	public static boolean printRules(EntityPlayerMP ep)
	{
		if(FTBUConfigLogin.rules.get().isEmpty()) return false;
		
		IChatComponent c = new ChatComponentTranslation(FTBU.mod.assets + "cmd.rules");
		c.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, FTBUConfigLogin.rules.get()));
		c.getChatStyle().setColor(EnumChatFormatting.GOLD);
		ep.addChatMessage(c);
		return true;
	}
	
	public static boolean printMotd(EntityPlayerMP ep)
	{
		for(String s : FTBUConfigLogin.motd.get())
			FTBLib.printChat(ep, s.replace("$player$", ep.getDisplayName()).replace("$", FTBLib.FORMATTING));
		
		printRules(ep);
		return true;
	}
}