package latmod.latcore;

import latmod.core.LMConfig;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class LCConfig extends LMConfig
{
	public General general;
	public Commands commands;
	
	public LCConfig(FMLPreInitializationEvent e)
	{
		super(e, "/LatMod/LatCoreMC.cfg");
		
		general = new General();
		commands = new Commands();
		
		save();
	}
	
	public class General extends Category
	{
		public boolean addOreNames;
		public boolean addRegistryNames;
		public boolean addFluidContainerNames;
		public boolean checkUpdates;
		
		public General()
		{
			super("general");
			
			addOreNames = getBool("addOreNames", false);
			addRegistryNames = getBool("addRegistryNames", false);
			addFluidContainerNames = getBool("addFluidContainerNames", false);
			checkUpdates = getBool("checkUpdates", true);
		}
	}
	
	public class Commands extends Category
	{
		public int latcore;
		public int latcoreadmin;
		public int realnick;
		public int teleport;
		public int list;
		public int gamemode;
		public int gamerule;
		
		public Commands()
		{
			super("commands");
			setCategoryDesc(
					"0 - Command is disabled",
					"1 - Command can be used by anyone",
					"2 - Command can only be used by OPs");
			
			latcore = getInt("latcore", 1, 0, 2);
			latcoreadmin = getInt("latcoreadmin", 2, 0, 2);
			realnick = getInt("realnick", 1, 0, 2);
			teleport = getInt("teleport", 2, 0, 2);
			list = getInt("list", 1, 0, 2);
			gamemode = getInt("gamemode", 2, 0, 2);
			gamerule = getInt("gamerule", 2, 0, 2);
		}
	}
}