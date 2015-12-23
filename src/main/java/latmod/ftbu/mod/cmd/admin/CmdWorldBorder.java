package latmod.ftbu.mod.cmd.admin;

import ftb.lib.cmd.*;
import latmod.ftbu.world.LMWorldServer;
import latmod.lib.MathHelperLM;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;

public class CmdWorldBorder extends CommandSubLM
{
	public CmdWorldBorder()
	{
		super("world_border", CommandLevel.OP);
		add(new CmdOn("on"));
		add(new CmdOff("off"));
		add(new CmdSet("set"));
		add(new CmdGet("get"));
		add(new CmdCenter("center"));
	}
	
	private static class CmdOn extends CommandLM
	{
		public CmdOn(String s)
		{ super(s, CommandLevel.OP); }
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{
			LMWorldServer.inst.settings.border_enabled.set(true);
			return new ChatComponentText("World border enabled");
		}
	}
	
	private static class CmdOff extends CommandLM
	{
		public CmdOff(String s)
		{ super(s, CommandLevel.OP); }
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{
			LMWorldServer.inst.settings.border_enabled.set(false);
			return new ChatComponentText("World border disabled");
		}
	}
	
	private static class CmdSet extends CommandLM
	{
		public CmdSet(String s)
		{ super(s, CommandLevel.OP); }
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{
			checkArgs(args, 2);
			
			int dim = parseInt(ics, args[0]);
			int dist = parseInt(ics, args[1]);
			
			LMWorldServer.inst.settings.setBorderSize(dim, dist);
			return new ChatComponentText("World border for dimension " + dim + " set to " + dist);
		}
	}
	
	private static class CmdGet extends CommandLM
	{
		public CmdGet(String s)
		{ super(s, CommandLevel.OP); }
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{
			checkArgs(args, 1);
			int dim = parseInt(ics, args[0]);
			return new ChatComponentText("World border for dimension " + dim + ": " + LMWorldServer.inst.settings.getBorderSize(dim));
		}
	}
	
	private static class CmdCenter extends CommandLM
	{
		public CmdCenter(String s)
		{ super(s, CommandLevel.OP); }
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{
			int x, z, dim;
			
			if(args.length >= 2)
			{
				x = parseInt(ics, args[0]);
				z = parseInt(ics, args[1]);
				if(args.length >= 3) dim = parseInt(ics, args[2]);
				else dim = ics.getEntityWorld().provider.dimensionId;
			}
			else
			{
				EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
				x = MathHelperLM.floor(ep.posX);
				z = MathHelperLM.floor(ep.posZ);
				dim = ep.dimension;
			}
			
			LMWorldServer.inst.settings.setBorderPos(dim, x, z);
			LMWorldServer.inst.update();
			return new ChatComponentText("World center for dimension " + dim + " set to " + x + " : " + z);
		}
	}
}