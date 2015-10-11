package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.cmd.*;
import latmod.ftbu.mod.config.*;
import latmod.lib.LMStringUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.*;

public class CmdAdminSpawnArea extends CommandLM //TODO: Remove
{
	public CmdAdminSpawnArea(String s)
	{ super(s, CommandLevel.OP); }

	public String[] getTabStrings(ICommandSender ics, String[] args, int i)
	{ return (i == 0) ? new String[] { "safe", "pvp" } : null; }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		checkArgs(args, 2);
		
		if(args[0].equals("safe"))
		{
			FTBUConfigGeneral.safeSpawn.set(args[1].equals("true"));
			FTBUConfig.save();
			return new ChatComponentText("SafeSpawn set to: " + FTBUConfigGeneral.safeSpawn.get());
		}
		else if(args[0].equals("pvp"))
		{
			FTBUConfigGeneral.spawnPVP.set(args[1].equals("true"));
			FTBUConfig.save();
			return new ChatComponentText("SpawnPVP set to: " + FTBUConfigGeneral.spawnPVP.get());
		}
		
		return new ChatComponentText(LMStringUtils.strip(getTabStrings(ics, args, 0)));
	}
}