package latmod.ftbu.mod.config;

import java.io.File;
import java.util.*;

import latmod.core.util.LMJsonUtils;
import latmod.ftbu.api.readme.ReadmeInfo;
import latmod.ftbu.util.LatCoreMC;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ConfigLogin
{
	private static transient File saveFile;
	
	@ReadmeInfo(info = "Message of the day. This will be displayed when player joins the server.", def = "Blank")
	public List<String> motd;
	
	@ReadmeInfo(info = "Rules link you can click on. This will be displayed when player joins the server.", def = "Blank")
	public String rules;
	
	@ReadmeInfo(info = "URL for per-server custom badges file (Json). Example can be seen here: http://pastebin.com/LvBB9HmV ", def = "Blank")
	public String customBadges;
	
	@ReadmeInfo(info = "Items to give player when it first joins the server. Format: StringID Size Metadata, does not support NBT yet.", def = "minecraft:apple 16 0")
	public List<ItemStack> startingItems;
	
	public static void load()
	{
		saveFile = new File(LatCoreMC.latmodFolder, "ftbu/login.txt");
		FTBUConfig.login = LMJsonUtils.fromJsonFile(saveFile, ConfigLogin.class);
		if(FTBUConfig.login == null) FTBUConfig.login = new ConfigLogin();
		FTBUConfig.login.loadDefaults();
		save();
	}
	
	public void loadDefaults()
	{
		if(motd == null)
		{
			motd = new ArrayList<String>();
			motd.add("Welcome to the server!");
		}
		
		if(rules == null) rules = "";
		if(customBadges == null) customBadges = "";
		
		if(startingItems == null)
		{
			startingItems = new ArrayList<ItemStack>();
			startingItems.add(new ItemStack(Items.apple, 16));
		}
	}
	
	public static void save()
	{
		if(FTBUConfig.login == null) load();
		if(!LMJsonUtils.toJsonFile(saveFile, FTBUConfig.login))
			LatCoreMC.logger.warn(saveFile.getName() + " failed to save!");
	}
	
	public List<ItemStack> getStartingItems(UUID id)
	{ return startingItems; }
}