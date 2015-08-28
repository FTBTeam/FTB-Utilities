package latmod.ftbu.mod.cmd.all;

import latmod.ftbu.core.cmd.SubCommand;
import latmod.ftbu.core.world.LMWorldServer;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.*;

public class CmdFTBUWorldID extends SubCommand
{
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{ return new ChatComponentText("WorldID: " + LMWorldServer.inst.worldIDS); }
}