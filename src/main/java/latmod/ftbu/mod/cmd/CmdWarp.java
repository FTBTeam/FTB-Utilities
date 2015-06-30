package latmod.ftbu.mod.cmd;

import latmod.ftbu.core.LMWorld;
import latmod.ftbu.core.cmd.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdWarp extends CommandLM
{
	public CmdWarp()
	{ super("warp", CommandLevel.ALL); }
	
	public String[] getTabStrings(ICommandSender ics, String[] args, int i)
	{
		if(i == 0) return LMWorld.listWarps();
		return super.getTabStrings(ics, args, i);
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		checkArgs(args, 1);
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		LMWorld.Warp h1 = LMWorld.getWarp(args[0]);
		if(h1 == null) return "Warp '" + args[0] + "' not set!";
		h1.teleportPlayer(ep);
		return FINE + "Teleported to '" + args[0] + "'";
	}
}