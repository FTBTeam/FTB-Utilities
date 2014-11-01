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
		public boolean checkTeamLatMod;
		public boolean checkUpdates;
		
		public General()
		{
			super("general");
			
			addOreNames = getBool("addOreNames", true);
			addRegistryNames = getBool("addRegistryNames", true);
			addFluidContainerNames = getBool("addFluidContainerNames", true);
			checkTeamLatMod = getBool("checkTeamLatMod", true);
			checkUpdates = getBool("checkUpdates", true);
		}
	}
	
	public class Commands extends Category
	{
		public int latcore;
		public int setnick;
		public int realnick;
		public int setskin;
		public int setcape;
		
		public Commands()
		{
			super("commands");
			setCategoryDesc(
					"0 - Command is disabled",
					"1 - Command can be used by anyone",
					"2 - Command can only be used by OPs");
			
			latcore = getInt("latcore", 1, 0, 2);
			setnick = getInt("setnick", 2, 0, 2);
			realnick = getInt("realnick", 2, 0, 2);
			setskin = getInt("setskin", 2, 0, 2);
			setcape = getInt("setcape", 2, 0, 2);
		}
	}
}