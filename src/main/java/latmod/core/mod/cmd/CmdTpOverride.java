package latmod.core.mod.cmd;

import latmod.core.*;
import latmod.core.cmd.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdTpOverride extends CommandBaseLC // CommandTeleport
{
	public static CommandLevel commandLevel = CommandLevel.OP;
	
	public CmdTpOverride()
	{ super("tp", commandLevel); }
	
	public NameType getUsername(String[] args, int i)
	{ return (i == 0 || i == 1) ? NameType.ON : NameType.NONE; }
	
	public String[] getSubcommands(ICommandSender ics)
	{ return null; }
	
	public void printHelp(ICommandSender ics)
	{
		printHelpLine(ics, "[who] <to>");
		printHelpLine(ics, "[who] <x> <y> <z>");
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(args.length == 0 || args.length > 4)
		{ printHelp(ics); return null; }
		
		EntityPlayerMP who = null;
		double x = 0D, y = 0D, z = 0D;
		int dim = 0;
		
		if(args.length == 2 || args.length == 4)
		{
			if(args[0].equals("@a"))
			{
				String[] players = LMPlayer.getAllNames(true);
				for(int i = 0; i < players.length; i++)
				{
					String[] args1 = args.clone();
					args1[0] = players[i];
					onCommand(ics, args1);
				}
				return null;
			}
			else
			who = CommandLM.getPlayer(ics, args[0]);
		}
		else
			who = getCommandSenderAsPlayer(ics);
		
		if(args.length == 1 || args.length == 2)
		{
			EntityPlayerMP to = CommandLM.getPlayer(ics, args[args.length - 1]);
			x = to.posX;
			y = to.posY;
			z = to.posZ;
			dim = to.dimension;
			
			LatCoreMC.printChat(who, "Teleported to " + LMPlayer.getPlayer(to).getDisplayName());
			if(who != ics) LatCoreMC.printChat(ics, "Teleported " + LMPlayer.getPlayer(who).getDisplayName() + " to " + LMPlayer.getPlayer(to).getDisplayName());
		}
		else if(args.length == 3 || args.length == 4)
		{
			int ai = args.length == 4 ? 0 : -1;
			x = func_110666_a(ics, who.posX, args[++ai]);
			y = func_110666_a(ics, who.posY, args[++ai]);
			z = func_110666_a(ics, who.posZ, args[++ai]);
			dim = who.dimension;
			
			LatCoreMC.printChat(who, "Teleported to " + LatCore.stripInt(x, y, z));
			if(who != ics) LatCoreMC.printChat(who, "Teleported " + LMPlayer.getPlayer(who).getDisplayName() + " to " + LatCore.stripInt(x, y, z));
		}
		else printHelp(ics);
		
		if(who.dimension != dim)
		{
			LatCoreMC.teleportEntity(who, dim);
			//return "Can't teleport to another dimension!";
		}
		
		who.playerNetServerHandler.setPlayerLocation(x, y, z, who.rotationYaw, who.rotationPitch);
		return null;
	}
}