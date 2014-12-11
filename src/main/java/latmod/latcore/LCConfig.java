package latmod.latcore;

import latmod.core.*;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class LCConfig extends LMConfig
{
	public static LCConfig instance;
	
	public LCConfig(FMLPreInitializationEvent e)
	{
		super(e, "/LatMod/LatCoreMC.cfg");
		instance = this;
		load();
	}
	
	public void load()
	{
		General.load(get("general"));
		Client.load(get("client"));
		Commands.load(get("commands"));
		Recipes.load(get("recipes"));
		save();
	}
	
	public static class General
	{
		public static boolean checkUpdates;
		public static boolean addWailaTanks;
		public static boolean addWailaInv;
		
		public static void load(Category c)
		{
			checkUpdates = c.getBool("checkUpdates", true, "Check Updates");
			addWailaTanks = c.getBool("addWailaTanks", false, "Add Waila Tanks");
			addWailaInv = c.getBool("addWailaInv", false, "Add Waila Inv");
		}
	}
	
	public static class Client
	{
		public static boolean enablePlayerDecorators;
		public static boolean rotateBlocks;
		public static boolean renderHighlights;
		
		public static boolean onlyAdvanced;
		public static boolean addOreNames;
		public static boolean addRegistryNames;
		public static boolean addFluidContainerNames;
		
		public static void load(Category c)
		{
			enablePlayerDecorators = c.getBool("enablePlayerDecorators", true, "Enable Player Decorators");
			rotateBlocks = c.getBool("rotateBlocks", true, "Rotate custom blocks");
			renderHighlights = c.getBool("renderHighlights", true, "Render custom block highlights");
			
			onlyAdvanced = c.getBool("onlyAdvanced", false, "Only advanced tool tips");
			addOreNames = c.getBool("addOreNames", false, "Add OreDictionary names");
			addRegistryNames = c.getBool("addRegistryNames", false, "Add Registry names");
			addFluidContainerNames = c.getBool("addFluidContainerNames", false, "Add Fluid names");
		}
	}
	
	public static class Commands
	{
		public static int latcore;
		public static int latcoreadmin;
		public static int realnick;
		public static int teleport;
		public static int list;
		public static int gamemode;
		public static int gamerule;
		
		public static void load(Category c)
		{
			c.setCategoryComment(
					"0 - Command is disabled",
					"1 - Command can be used by anyone",
					"2 - Command can only be used by OPs");
			
			latcore = c.getInt("latcore", 1, 0, 2, "latcore");
			latcoreadmin = c.getInt("latcoreadmin", 2, 0, 2, "latcoreadmin");
			realnick = c.getInt("realnick", 1, 0, 2, "realnick");
			teleport = c.getInt("teleport", 2, 0, 2, "teleport");
			list = c.getInt("list", 1, 0, 2, "list");
			gamemode = c.getInt("gamemode", 2, 0, 2, "gamemode");
			gamerule = c.getInt("gamerule", 2, 0, 2, "gamerule");
		}
	}
	
	public static class Recipes
	{
		public static boolean smeltFleshToLeather;
		public static boolean craftWoolWithDye;
		
		public static void load(Category c)
		{
			smeltFleshToLeather = c.getBool("smeltFleshToLeather", true, "Rotten Flesh to Leather");
			craftWoolWithDye = c.getBool("craftWoolWithDye", true, "Craft Wool With Dye");
		}
		
		public static void loadRecipes()
		{
			if(smeltFleshToLeather)
				LC.mod.recipes.addSmelting(new ItemStack(Items.rotten_flesh), new ItemStack(Items.leather));
			
			if(craftWoolWithDye)
			{
				for(int i = 0; i < 16; i++)
					LC.mod.recipes.addRecipe(new ItemStack(Blocks.wool, 8, i), "WWW", "WDW", "WWW",
							'W', new ItemStack(Blocks.wool, 1, LatCoreMC.ANY),
							'D', EnumDyeColor.VALUES[i].dyeName);
			}
		}
	}
}