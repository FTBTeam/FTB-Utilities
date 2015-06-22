package latmod.ftbu.mod;

import latmod.ftbu.core.*;
import latmod.ftbu.core.util.*;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class FTBUConfig extends LMConfig implements IServerConfig
{
	public static FTBUConfig instance;
	
	public FTBUConfig(FMLPreInitializationEvent e)
	{
		super(e, "/LatMod/FTBU.cfg");
		instance = this;
		load();
	}
	
	public void load()
	{
		General.load(get("general"));
		Login.load(get("login"));
		WorldBorder.load(get("world_border"));
		save();
	}
	
	public void readConfig(NBTTagCompound tag)
	{
	}
	
	public void writeConfig(NBTTagCompound tag)
	{
	}
	
	public static class General
	{
		public static boolean allowCreativeInteractSecure;
		public static String commandFTBU;
		public static String commandAdmin;
		public static double restartTimer;
		
		public static void load(Category c)
		{
			allowCreativeInteractSecure = c.getBool("allowCreativeInteractSecure", true);
			c.setComment("allowCreativeInteractSecure", "If set to true, creative players will be able to access protected chests / chunks");
			
			commandFTBU = c.getString("commandFTBU", "ftbu");
			c.setComment("commandFTBU", "Command name for ftbu command");
			
			commandAdmin = c.getString("commandAdmin", "admin");
			c.setComment("commandAdmin", "Command name for admin command");
			
			restartTimer = c.getFloat("restartTimer", 0F, 0F, 720F);
			c.setComment("restartTimer",
					"Server will automatically shut down after X hours",
					"0 - Disabled",
					"0.5 - 30 minutes",
					"1 - 1 Hour",
					"24 - 1 Day",
					"168 - 1 Week",
					"720 - 1 Month");
		}
	}
	
	public static class Login
	{
		public static FastList<String> motd;
		public static String rules;
		
		public static void load(Category c)
		{
			motd = c.getStringArray("motd", new String[] { "Welcome to the server!" });
			c.setComment("motd", "Message of the day. Will appear when player logs in");
			
			rules = c.getString("rules", "");
			c.setComment("rules", "Rules page link, blank - ");
		}
	}
	
	public static class WorldBorder
	{
		public static boolean enabled;
		public static int radius;
		private static FastMap<Integer, Integer> custom;
		
		public static void load(Category c)
		{
			enabled = c.getBool("enabled", false);
			c.setComment("enabled", "World border enabled");
			
			radius = c.getInt("radius", 10000, 0, 20000000);
			c.setComment("radius", "World border radius in blocks for DIM0 (Overworld)");
			
			String s[] = c.getString("custom", "").split(", ");
			
			for(String s1 : s)
			{
				if(s1 != null && !s1.isEmpty() && s1.contains(" = "))
				{
					String[] s2 = s1.split(" = ");
					if(s2 != null && s2.length == 2)
					{
						int dimID = Converter.toInt(s2[0], 0);
						int rad = Converter.toInt(s2[1], 0);
						
						if(dimID != 0 && rad > 0)
						{
							if(custom == null) custom = new FastMap<Integer, Integer>();
							custom.put(Integer.valueOf(dimID), Integer.valueOf(rad));
						}
					}
				}
			}
			
			c.setComment("custom", "Custom world borders.\nDimID can't be 0 and radius must be > 0.\nExample:\n6 = 10000, -1 = 3000, 1 = 500");
		}
		
		public static int getWorldBorder(int dim)
		{
			if(!enabled) return 0;
			if(dim == 0) return radius;
			
			if(custom != null && !custom.isEmpty())
			{
				Integer i = custom.get(dim);
				if(i != null) return i.intValue();
			}
			
			return (int)(radius * LatCoreMC.getWorldScale(dim));
		}
	}
}