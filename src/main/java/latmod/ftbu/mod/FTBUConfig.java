package latmod.ftbu.mod;

import latmod.ftbu.core.*;
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
		public static int restartTimer;
		
		public static void load(Category c)
		{
			allowCreativeInteractSecure = c.getBool("allowCreativeInteractSecure", true);
			c.setComment("allowCreativeInteractSecure", "If set to true, creative players will be able to access protected chests / chunks");
			
			commandFTBU = c.getString("commandFTBU", "ftbu");
			c.setComment("commandFTBU", "Command name for ftbu command");
			
			commandAdmin = c.getString("commandAdmin", "admin");
			c.setComment("commandAdmin", "Command name for admin command");
			
			restartTimer = c.getInt("restartTimer", 0, 0, 720);
			c.setComment("restartTimer",
					"Server will automatically shut down after X hours",
					"0 - Disabled",
					"1 - 1 Hour",
					"24 - 1 Day",
					"168 - 1 Week",
					"720 - 1 Month");
		}
	}
	
	public static class Login
	{
		public static String motd;
		public static String rules;
		
		public static void load(Category c)
		{
			motd = c.getString("motd", "");
			c.setComment("motd", "Message of the day. Will appear when player logs in");
			
			rules = c.getString("rules", "");
			c.setComment("rules", "Rules page link, blank - ");
		}
	}
	
	public static class World
	{
		public static void load(Category c)
		{
		}
	}
}