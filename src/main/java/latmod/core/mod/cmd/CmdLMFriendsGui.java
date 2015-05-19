package latmod.core.mod.cmd;

import latmod.core.LatCoreMC;
import latmod.core.cmd.CommandLevel;
import latmod.core.mod.LCGuiHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class CmdLMFriendsGui extends CommandBaseLC
{
	public CmdLMFriendsGui()
	{
		super("friendsLM", CommandLevel.ALL);
	}
	
	public String[] getSubcommands(ICommandSender ics)
	{ return null; }
	
	public void printHelp(ICommandSender ics)
	{
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(ics == null || !(ics instanceof EntityPlayer)) return "Invalid player!";
		LatCoreMC.openGui((EntityPlayer)ics, LCGuiHandler.FRIENDS, null);
		return null;
	}
}