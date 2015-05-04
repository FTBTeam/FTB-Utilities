package latmod.core.mod.cmd;

import latmod.core.*;
import latmod.core.cmd.CommandLevel;
import latmod.core.mod.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;

public class CmdLMFriends extends CommandBaseLC
{
	public CmdLMFriends()
	{ super("friendsLM", CommandLevel.ALL); }
	
	public void printHelp(ICommandSender ics)
	{
		printHelpLine(ics, "[gui]");
		printHelpLine(ics, "add <player>");
		printHelpLine(ics, "rem <player>");
	}
	
	public int getRequiredPermissionLevel()
	{ return 0; }
	
	public boolean canCommandSenderUseCommand(ICommandSender ics)
	{ return !LCConfig.General.disableLMFriendsCommand; }
	
	public String[] getSubcommands(ICommandSender ics)
	{ return new String[] { "help", "add", "rem", "list" }; }
	
	public NameType getUsername(String[] args, int i)
	{
		if(i == 1 && isArg(args, 0, "add", "rem")) return NameType.OFF;
		return NameType.NONE;
	}
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		if(i == 0) return getSubcommands(ics);
		if(i == 1 && isArg(args, 0, "list")) return new String[]{ "friends", /*"groups", "members"*/ };
		
		return super.getTabStrings(ics, args, i);
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		LMPlayer owner = getLMPlayer(ep);
		
		if(args == null || args.length == 0 || args[0].equals("gui"))
		{ LatCoreMC.openGui(ep, LCGuiHandler.FRIENDS, null); return null; }
		else if(args[0].equals("help")) { printHelp(ep); return null; }
		
		return onStaticCommand(ep, owner, args);
	}
	
	public static String onStaticCommand(EntityPlayerMP ep, LMPlayer owner, String[] args)
	{
		checkArgs(args, 1);
		
		if(args[0].equals("list"))
		{
			checkArgs(args, 2);
			
			if(args[1].equals("friends"))
			{
				if(owner.friends.isEmpty()) return FINE + "No friends added";
				
				LatCoreMC.printChat(ep, "Your friends:");
				
				for(int i = 0; i < owner.friends.size(); i++)
				{
					LMPlayer p = owner.friends.get(i);
					EnumChatFormatting col = EnumChatFormatting.GREEN;
					if(p.isFriendRaw(owner) && !owner.isFriendRaw(p)) col = EnumChatFormatting.GOLD;
					if(!p.isFriendRaw(owner) && owner.isFriendRaw(p)) col = EnumChatFormatting.BLUE;
					LatCoreMC.printChat(ep, col + "[" + i + "]: " + p.username);
				}
			}
			
			return null;
		}
		
		LMPlayer p = null;
		
		if(args.length >= 2 && isArg(args, 0, "add", "rem")) p = getLMPlayer(args[1]);
		if(p != null && p.equals(owner)) return "Invalid player!";
		
		checkArgs(args, 2);
		
		if(args[0].equals("add"))
		{
			if(!owner.friends.contains(p))
			{
				owner.friends.add(p);
				return changed(owner, p, "Added " + p.username + " as friend");
			}
			else return p.username + " is already added as friend!";
		}
		else if(args[0].equals("rem"))
		{
			if(owner.friends.contains(p))
			{
				owner.friends.remove(p);
				return changed(owner, p, "Removed " + p.username + " from friends");
			}
			else return p.username + " is not added as friend!";
		}
		
		return null;
	}
	
	private static String changed(LMPlayer o, LMPlayer p, String s)
	{
		o.sendUpdate(LMPlayer.ACTION_GROUPS_CHANGED, true);
		if(p != null) p.sendUpdate(LMPlayer.ACTION_GROUPS_CHANGED, true);
		return FINE + s;
	}
}