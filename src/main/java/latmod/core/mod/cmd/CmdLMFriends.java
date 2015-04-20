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
		//printHelpLine(ics, "addgroup <name>");
		//printHelpLine(ics, "remgroup <name>");
		//printHelpLine(ics, "addto <name> <player>");
		//printHelpLine(ics, "remfrom <name> <player>");
	}
	
	public int getRequiredPermissionLevel()
	{ return 0; }
	
	public boolean canCommandSenderUseCommand(ICommandSender ics)
	{ return !LCConfig.General.disableLMFriendsCommand; }
	
	public String[] getSubcommands(ICommandSender ics)
	{ return new String[] { "help", "add", "rem", /*"addgroup", "remgroup", "rengroup", "addto", "remfrom",*/ "list" }; }
	
	public NameType getUsername(String[] args, int i)
	{
		if(i == 1 && isArg(args, 0, "add", "rem")) return NameType.OFF;
		//if(i == 2 && isArg(args, 0, "addto", "remfrom")) return NameType.OFF;
		return NameType.NONE;
	}
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		if(i == 0) return getSubcommands(ics);
		if(i == 1 && isArg(args, 0, "list")) return new String[]{ "friends", /*"groups", "members"*/ };
		//if(i == 2 && isArg(args, 0, "list") && args[1].equals("members"))
		//	return Group.getAllGroupNames(getLMPlayer(ics));
		//if(i == 1 && isArg(args, 0, "remgroup", "rengroup", "addto", "remfrom"))
		//	return Group.getAllGroupNames(getLMPlayer(ics));
		
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
			/*else if(args[1].equals("groups"))
			{
				FastList<Group> groups = Group.getAllGroups(owner);
				
				if(groups.isEmpty()) return FINE + "No groups found";
				
				LatCoreMC.printChat(ep, "Your groups:");
				
				for(Group g : groups)
					LatCoreMC.printChat(ep, "[" + g.groupID + "]: " + g.title);
			}
			else if(args[1].equals("members"))
			{
				checkArgs(args, 3);
				
				Group g = Group.getGroup(args[1]);
				
				if(g.members.isEmpty()) return FINE + "No members added";
				
				LatCoreMC.printChat(ep, "Members of " + g.title + ":");
				
				for(int i = 0; i < g.members.size(); i++)
					LatCoreMC.printChat(ep, "[" + i + "]: " + g.members.keys.get(i).username);
			}*/
			
			return null;
		}
		
		LMPlayer p = null;
		
		if(args.length >= 2 && isArg(args, 0, "add", "rem")) p = getLMPlayer(args[1]);
		//if(args.length >= 3 && isArg(args, 0, "addto", "remfrom")) p = getLMPlayer(args[2]);
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
		else
		{
			/*
			int groupID = Group.getGroupID(args[1]);
			Group g = Group.getGroup(groupID);
			
			if(args[0].equals("addgroup"))
			{
				if(Group.getGroup(args[1]) != null) return "Group name is already taken!";
				
				if(args[1] != null)
				{
					g = new Group(++Group.lastGroupID);
					g.title = args[1];
					g.members.put(owner, Group.Status.OWNER);
					
					Group.groups.add(g);
					return changed(owner, null, "Group '" + g.title + "' created!");
				}
				else return "Can't add any more groups!";
			}
			else if(args[0].equals("remgroup"))
			{
				if(groupID > 0 && Group.groups.notEmpty())
				{
					if(Group.groups.remove(Group.getGroup(groupID)))
						return changed(owner, null, "Group " + args[1] + " deleted!");
					else return "Group " + args[1] + " not found!";
				}
			}
			
			checkArgs(args, 3);
			
			if(args[0].equals("rengroup"))
			{
				if(Group.getGroup(args[2]) != null) return "Group name is already taken!";
				
				if(g != null && Group.groups.notEmpty() && !args[2].isEmpty())
				{
					String name0 = g.title + ""; g.title = args[2];
					return changed(owner, null, "Renamed " + name0 + " to " + g.title);
				}
				else return "Group " + args[1] + " not found!";
			}
			else if(args[0].equals("addto"))
			{
				if(g != null && p != null && Group.groups.notEmpty())
				{
					if(!g.isPlayerInGroup(p)) g.members.put(p, Group.Status.MEMBER);
					return changed(owner, p, "Added " + p.username + " to " + g.title);
				}
				else return "Group " + args[1] + " not found!";
			}
			else if(args[0].equals("remfrom"))
			{
				if(groupID > 0 && p != null && Group.groups.notEmpty())
				{
					if(g.isPlayerInGroup(p)) g.members.remove(p);
					return changed(owner, p, "Removed " + p.username + " from " + g.title);
				}
				else return "Group " + args[1] + " not found!";
			}
			*/
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