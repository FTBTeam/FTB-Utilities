package latmod.ftbu.mod.config;

import java.io.File;
import java.util.UUID;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.event.FTBUReadmeEvent;
import latmod.ftbu.core.inv.InvUtils;
import latmod.ftbu.core.util.*;
import net.minecraft.item.ItemStack;

import com.google.gson.annotations.Expose;

public class ConfigLogin
{
	private static File saveFile;
	
	@Expose public String[] motd;
	@Expose public String rules;
	@Expose public String customBadges;
	@Expose private String[] startingItems;
	private final FastList<ItemStack> startingItemsList = new FastList<ItemStack>();
	
	public static void load()
	{
		saveFile = new File(LatCoreMC.latmodFolder, "ftbu/login.txt");
		FTBUConfig.login = LatCore.fromJsonFile(saveFile, ConfigLogin.class);
		if(FTBUConfig.login == null) FTBUConfig.login = new ConfigLogin();
		FTBUConfig.login.loadDefaults();
		save();
	}
	
	public void loadDefaults()
	{
		if(motd == null) motd = new String[] { "Welcome to the server!" };
		if(rules == null) rules = "";
		if(customBadges == null) customBadges = "";
		if(startingItems == null) startingItems = new String[] { "minecraft:apple 16 0" };
		
		startingItemsList.clear();
		for(String s : startingItems)
		{
			ItemStack is = InvUtils.parseItem(s);
			if(is != null && is.stackSize > 0)
				startingItemsList.add(is);
		}
	}
	
	public static void save()
	{
		if(FTBUConfig.login == null) load();
		if(!LatCore.toJsonFile(saveFile, FTBUConfig.login))
			LatCoreMC.logger.warn(saveFile.getName() + " failed to save!");
	}
	
	public static void saveReadme(FTBUReadmeEvent e)
	{
		FTBUReadmeEvent.ReadmeFile.Category login = e.file.get("latmod/ftbu/login.txt");
		login.add("motd", "Message of the day. This will be displayed when player joins the server.", "Blank");
		login.add("rules", "Rules link you can click on. This will be displayed when player joins the server.", "Blank");
		login.add("customBadges", "URL for per-server custom badges file (Json). Example can be seen here: http://pastebin.com/LvBB9HmV ", "Blank");
		login.add("startingItems", "Items to give player when it first joins the server. Format: StringID Size Metadata, does not support NBT yet.", "minecraft:apple 16 0");
	}

	public FastList<ItemStack> getStartingItems(UUID id)
	{ return startingItemsList; }
}