package latmod.ftbu.mod.cmd;

import latmod.ftbu.core.*;
import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.world.LMWorldServer;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdWarp extends CommandLM
{
	public CmdWarp()
	{ super("warp", CommandLevel.ALL); }
	
	public String[] getTabStrings(ICommandSender ics, String[] args, int i)
	{
		if(i == 0) return LMWorldServer.inst.listWarps();
		return super.getTabStrings(ics, args, i);
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		checkArgs(args, 1);
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		EntityPos p = LMWorldServer.inst.getWarp(args[0]);
		if(p == null) return "Warp '" + args[0] + "' not set!";
		LMDimHelper.teleportPlayer(ep, p);
		return FINE + "Teleported to '" + args[0] + "'";
	}
}