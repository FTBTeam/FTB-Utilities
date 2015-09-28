package latmod.ftbu.mod.cmd;

import latmod.ftbu.cmd.*;
import latmod.ftbu.util.*;
import latmod.ftbu.world.LMWorldServer;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;

public class CmdWarp extends CommandLM
{
	public CmdWarp()
	{ super("warp", CommandLevel.ALL); }
	
	public String[] getTabStrings(ICommandSender ics, String[] args, int i)
	{
		if(i == 0) return LMWorldServer.inst.warps.list();
		return super.getTabStrings(ics, args, i);
	}
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		checkArgs(args, 1);
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		EntityPos p = LMWorldServer.inst.warps.get(args[0]);
		if(p == null) return error(new ChatComponentText("Warp '" + args[0] + "' not set!"));//LANG
		LMDimUtils.teleportPlayer(ep, p);
		return new ChatComponentText("Teleported to '" + args[0] + "'");
	}
}