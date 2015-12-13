package latmod.ftbu.mod.config;

import java.util.UUID;

import ftb.lib.FTBLib;
import ftb.lib.item.ItemStackTypeAdapter;
import latmod.lib.FastList;
import latmod.lib.config.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class FTBUConfigLogin
{
	public static final ConfigGroup group = new ConfigGroup("login");
	private static final ConfigEntryStringArray motd = new ConfigEntryStringArray("motd", new FastList<String>("Welcome to the server!")).setInfo("Message of the day. This will be displayed when player joins the server");
	public static final ConfigEntryString custom_badges = new ConfigEntryString("custom_badges", "").sync().setInfo("URL for per-server custom badges file (Json). Example can be seen here:\nhttp://pastebin.com/raw.php?i=ZXVhpEZ1");
	private static final ConfigEntryStringArray starting_items = new ConfigEntryStringArray("starting_items", new FastList<String>("minecraft:apple 16 0")).setInfo("Items to give player when it first joins the server.\nFormat: StringID Size Metadata\nDoes not support NBT yet");
	
	public static FastList<ItemStack> getStartingItems(UUID id)
	{
		FastList<ItemStack> list = new FastList<ItemStack>();
		
		for(String s : starting_items.get())
		{
			ItemStack is = ItemStackTypeAdapter.parseItem(s);
			if(is != null) list.add(is);
		}
		
		return list;
	}
	
	public static boolean printMotd(EntityPlayerMP ep)
	{
		for(String s : FTBUConfigLogin.motd.get())
			FTBLib.printChat(ep, s.replace("$player$", ep.getDisplayName()).replace("$", FTBLib.FORMATTING));
		return true;
	}
}