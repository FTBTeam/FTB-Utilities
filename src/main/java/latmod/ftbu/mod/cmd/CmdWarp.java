package latmod.ftbu.mod.cmd;

import ftb.lib.*;
import ftb.lib.cmd.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.config.FTBUConfigCmd;
import latmod.ftbu.world.LMWorldServer;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;

public class CmdWarp extends CommandLM
{
	public CmdWarp()
	{ super(FTBUConfigCmd.name_warp.get(), CommandLevel.ALL); }
	
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <ID>"; }
	
	public String[] getTabStrings(ICommandSender ics, String[] args, int i) throws CommandException
	{
		if(i == 0) return LMWorldServer.inst.warps.list();
		return super.getTabStrings(ics, args, i);
	}
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		EntityPos p = LMWorldServer.inst.warps.get(args[0]);
		if(p == null) return error(new ChatComponentTranslation(FTBU.mod.assets + "cmd.warp_not_set", args[0]));
		LMDimUtils.teleportPlayer(ep, p);
		return new ChatComponentTranslation(FTBU.mod.assets + "cmd.warp_tp", args[0]);
	}
}