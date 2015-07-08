package latmod.ftbu.mod.cmd;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.cmd.*;
import latmod.ftbu.mod.config.FTBUConfig;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdMotd extends CommandLM
{
	public CmdMotd()
	{ super("motd", CommandLevel.ALL); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(!printMotd(getCommandSenderAsPlayer(ics)))
			throw new FeatureDisabledException();
		return null;
	}
	
	public static boolean printMotd(EntityPlayerMP ep)
	{
		if(!FTBUConfig.isDedi()) return false;
		
		for(String s : FTBUConfig.login.motd)
			LatCoreMC.printChat(ep, s.replace("$player$", ep.getDisplayName()).replace("$", LatCoreMC.FORMATTING));
		CmdRules.printRules(ep);
		return true;
	}
}