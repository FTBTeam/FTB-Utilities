package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.cmd.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.world.LMWorldServer;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.*;

public class CmdAdminDelWarp extends CommandLM
{
	public CmdAdminDelWarp(String s)
	{ super(s, CommandLevel.OP); }
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		if(i == 0) return LMWorldServer.inst.warps.list();
		return super.getTabStrings(ics, args, i);
	}
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		checkArgs(args, 1);
		if(LMWorldServer.inst.warps.rem(args[0]))
			return new ChatComponentTranslation(FTBU.mod.assets + "cmd.warp_del", args[0]);
		return error(new ChatComponentTranslation(FTBU.mod.assets + "cmd.warp_not_set", args[0]));
	}
}