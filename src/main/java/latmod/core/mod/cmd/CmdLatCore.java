package latmod.core.mod.cmd;

import latmod.core.LMPlayer;
import latmod.core.cmd.CommandLevel;
import latmod.core.mod.*;
import latmod.core.net.*;
import latmod.core.util.LatCore;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.*;
import net.minecraft.util.*;

public class CmdLatCore extends CommandBaseLC
{
	public CmdLatCore()
	{ super("latcore", CommandLevel.ALL); }
	
	public void printHelp(ICommandSender ics)
	{
		printHelpLine(ics, "<versions>");
		printHelpLine(ics, "<friends>");
		printHelpLine(ics, "<uuid> [player]");
		printHelpLine(ics, "<playerID> [player]");
	}
	
	public int getRequiredPermissionLevel()
	{ return 0; }
	
	public boolean canCommandSenderUseCommand(ICommandSender ics)
	{ return !LCConfig.General.disableLatCoreCommand; }
	
	public String[] getSubcommands(ICommandSender ics)
	{ return new String[] { "versions", "friends", "uuid", "playerID" }; }
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		if(i == 0) return getSubcommands(ics);
		return super.getTabStrings(ics, args, i);
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(args == null || args.length == 0)
			return "Subcommands: " + LatCore.strip(getSubcommands(ics));
		
		if(args[0].equals("versions"))
		{
			ThreadCheckVersions.init(ics, true);
			return null;
		}
		else if(args[0].equals("friends"))
		{
			EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
			MessageLM.NET.sendTo(new MessageCustomServerAction(LCEventHandler.ACTION_OPEN_FRIENDS_GUI, null), ep);
			return null;
		}
		else if(args[0].equals("uuid"))
		{
			LMPlayer p = LMPlayer.getPlayer(args.length > 1 ? args[1] : ics);
			
			IChatComponent toPrint = new ChatComponentText("UUID for " + p.username + ": ");
			IChatComponent uuid = new ChatComponentText(p.uuid.toString());
			uuid.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Copy to chat")));
			uuid.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, p.uuid.toString()));
			uuid.getChatStyle().setColor(EnumChatFormatting.GOLD);
			toPrint.appendSibling(uuid);
			ics.addChatMessage(toPrint);
			return null;
		}
		if(args[0].equals("playerID"))
		{
			LMPlayer p = LMPlayer.getPlayer(args.length > 1 ? args[1] : ics);
			return FINE + "PlayerID for " + p.username + ": " + p.playerID;
		}
		
		return onCommand(ics, null);
	}
}