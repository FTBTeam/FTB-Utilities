package latmod.ftbu.mod.cmd;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.cmd.*;
import latmod.ftbu.mod.FTBUConfig;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdMotd extends CommandLM
{
	public CmdMotd()
	{ super("motd", CommandLevel.ALL); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{ printMotd(getCommandSenderAsPlayer(ics)); return null; }
	
	public static void printMotd(EntityPlayerMP ep)
	{
		if(!LatCoreMC.isDedicatedServer()) return;
		
		if(!FTBUConfig.Login.motd.isEmpty()) for(String s : FTBUConfig.Login.motd)
			LatCoreMC.printChat(ep, s.replace("$and$", "&").replace("$player$", ep.getDisplayName()).replace("$", LatCoreMC.FORMATTING));
		CmdRules.printRules(ep);
	}
}