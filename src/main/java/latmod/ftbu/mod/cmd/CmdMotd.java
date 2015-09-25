package latmod.ftbu.mod.cmd;

import latmod.ftbu.cmd.*;
import latmod.ftbu.mod.config.FTBUConfig;
import latmod.ftbu.util.LatCoreMC;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IChatComponent;

public class CmdMotd extends CommandLM
{
	public CmdMotd()
	{ super("motd", CommandLevel.ALL); }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		if(!printMotd(getCommandSenderAsPlayer(ics)))
			throw new FeatureDisabledException();
		return null;
	}
	
	public static boolean printMotd(EntityPlayerMP ep)
	{
		if(!FTBUConfig.general.isDedi() || FTBUConfig.login.motd.isEmpty()) return false;
		
		for(String s : FTBUConfig.login.motd)
			LatCoreMC.printChat(ep, s.replace("$player$", ep.getDisplayName()).replace("$", LatCoreMC.FORMATTING));
		CmdRules.printRules(ep);
		return true;
	}
}