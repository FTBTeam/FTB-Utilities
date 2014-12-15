package latmod.core.cmd.mod;

import latmod.core.*;
import latmod.core.cmd.CommandLevel;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class CmdListOverride extends CommandBaseLC
{
	public CmdListOverride(CommandLevel l)
	{ super("list", l); }
	
	public String[] getSubcommands(ICommandSender ics)
	{ return null; }
	
	public void printHelp(ICommandSender ics)
	{
		printHelpLine(ics, "[uuid]");
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		LatCoreMC.printChat(ics, "Players currently online: [ " + MinecraftServer.getServer().getConfigurationManager().playerEntityList.size() + " ]");
		for(int i = 0; i < MinecraftServer.getServer().getConfigurationManager().playerEntityList.size(); i++)
		{
			EntityPlayer ep = (EntityPlayer)MinecraftServer.getServer().getConfigurationManager().playerEntityList.get(i);
			LMPlayer p = LMPlayer.getPlayer(ep);
			
			if(args.length > 0 && args[0].equals("uuids"))
				LatCoreMC.printChat(ics, LatCoreMC.removeFormatting(p.getDisplayName()) + " :: " + ep.getUniqueID());
			else
				LatCoreMC.printChat(ics, LatCoreMC.removeFormatting(p.getDisplayName()));
		}
		
		return null;
	}
}