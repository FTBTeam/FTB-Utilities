package latmod.core.mod.cmd;

import latmod.core.*;
import latmod.core.cmd.CommandLevel;
import latmod.core.mod.*;
import latmod.core.util.LatCore;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.*;
import net.minecraft.util.*;

public class CmdLatCore extends CommandBaseLC
{
	public CmdLatCore()
	{
		super("latcore", CommandLevel.ALL);
		if(LCConfig.General.addCommandAlias)
			aliases.add("lc");
	}
	
	public void printHelp(ICommandSender ics)
	{
		printHelpLine(ics, "<friends> <add | rem> <player>");
		printHelpLine(ics, "<friends> <list>");
		printHelpLine(ics, "<uuid> [player]");
		printHelpLine(ics, "<playerID> [player]");
	}
	
	public int getRequiredPermissionLevel()
	{ return 0; }
	
	public boolean canCommandSenderUseCommand(ICommandSender ics)
	{ return !LCConfig.General.disableLatCoreCommand; }
	
	public String[] getSubcommands(ICommandSender ics)
	{ return new String[] { "version", "friends", "uuid", "playerID" }; }
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		if(i == 0) return getSubcommands(ics);
		return super.getTabStrings(ics, args, i);
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(args == null || args.length == 0)
			return "Subcommands: " + LatCore.strip(getSubcommands(ics));
		
		if(args[0].equals("version"))
			return FINE + "Current version: " + LC.VERSION;
		
		else if(args[0].equals("friends"))
		{
			checkArgs(args, 2);
			
			LMPlayer owner = getLMPlayer(ics);
			
			if(args[1].equals("list"))
			{
				if(owner.friends.isEmpty()) return FINE + "No friends added";
				
				LatCoreMC.printChat(ics, "Your friends:");
				
				for(int i = 0; i < owner.friends.size(); i++)
				{
					LMPlayer p = owner.friends.get(i);
					EnumChatFormatting col = EnumChatFormatting.GREEN;
					if(p.isFriendRaw(owner) && !owner.isFriendRaw(p)) col = EnumChatFormatting.GOLD;
					if(!p.isFriendRaw(owner) && owner.isFriendRaw(p)) col = EnumChatFormatting.BLUE;
					LatCoreMC.printChat(ics, col + "[" + i + "]: " + p.username);
				}
				
				return null;
			}
			else
			{
				checkArgs(args, 3);
				
				LMPlayer p = getLMPlayer(args[2]);
				
				if(p.equals(owner)) return "Invalid player!";
				
				if(args[1].equals("add"))
				{
					if(!owner.friends.contains(p))
					{
						owner.friends.add(p);
						return changed(owner, p, "Added " + p.username + " as friend");
					}
					
					return p.username + " is already a friend!";
				}
				else if(args[1].equals("rem"))
				{
					if(owner.friends.contains(p))
					{
						owner.friends.remove(p);
						return changed(owner, p, "Removed " + p.username + " from friends");
					}
					
					return p.username + " is not added as friend!";
				}
			}
			
			return null;
		}
		else if(args[0].equals("uuid"))
		{
			checkArgs(args, 2);
			
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
			checkArgs(args, 2);
			
			LMPlayer p = LMPlayer.getPlayer(args.length > 1 ? args[1] : ics);
			return FINE + "PlayerID for " + p.username + ": " + p.playerID;
		}
		
		return onCommand(ics, null);
	}
	
	private static String changed(LMPlayer o, LMPlayer p, String s)
	{
		o.sendUpdate(LMPlayer.ACTION_GROUPS_CHANGED, true);
		if(p != null) p.sendUpdate(LMPlayer.ACTION_GROUPS_CHANGED, true);
		return FINE + s;
	}
}