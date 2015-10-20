package latmod.ftbu.mod.cmd;

import ftb.lib.FTBLib;
import latmod.ftbu.cmd.*;
import latmod.ftbu.mod.config.FTBUConfigLogin;
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
		if(FTBUConfigLogin.motd.get().length == 0) return false;
		
		for(String s : FTBUConfigLogin.motd.get())
			FTBLib.printChat(ep, s.replace("$player$", ep.getDisplayName()).replace("$", FTBLib.FORMATTING));
		CmdRules.printRules(ep);
		return true;
	}
}