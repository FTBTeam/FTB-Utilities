package latmod.latcore;

import latmod.core.LMConfig;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class LCConfig extends LMConfig
{
	public LCConfig(FMLPreInitializationEvent e)
	{
		super(e, "/LatMod/LatCoreMC.cfg");
		
		General.load(get("general"));
		Client.load(get("client"));
		Commands.load(get("commands"));
		Recipes.load(get("recipes"));
		
		save();
	}
	
	public static class General
	{
		public static boolean checkUpdates;
		
		public static void load(Category c)
		{
			checkUpdates = c.getBool("checkUpdates", true);
		}
	}
	
	public static class Client
	{
		public static boolean addOreNames;
		public static boolean addRegistryNames;
		public static boolean addFluidContainerNames;
		
		public static void load(Category c)
		{
			addOreNames = c.getBool("addOreNames", false);
			addRegistryNames = c.getBool("addRegistryNames", false);
			addFluidContainerNames = c.getBool("addFluidContainerNames", false);
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
			c.setCategoryDesc(
					"0 - Command is disabled",
					"1 - Command can be used by anyone",
					"2 - Command can only be used by OPs");
			
			latcore = c.getInt("latcore", 1, 0, 2);
			latcoreadmin = c.getInt("latcoreadmin", 2, 0, 2);
			realnick = c.getInt("realnick", 1, 0, 2);
			teleport = c.getInt("teleport", 2, 0, 2);
			list = c.getInt("list", 1, 0, 2);
			gamemode = c.getInt("gamemode", 2, 0, 2);
			gamerule = c.getInt("gamerule", 2, 0, 2);
		}
	}
	
	public static class Recipes
	{
		public static boolean smeltFleshToLeather;
		public static boolean craftWoolWithDye;
		
		public static void load(Category c)
		{
			smeltFleshToLeather = c.getBool("smeltFleshToLeather", true);
			craftWoolWithDye = c.getBool("craftWoolWithDye", true);
		}
	}
}