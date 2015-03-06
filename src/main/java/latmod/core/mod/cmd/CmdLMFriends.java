package latmod.core.mod.cmd;

import latmod.core.LMPlayer;
import latmod.core.cmd.CommandLevel;
import latmod.core.mod.*;
import latmod.core.net.*;
import latmod.core.util.FastList;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdLMFriends extends CommandBaseLC
{
	public CmdLMFriends()
	{ super("friendsLM", CommandLevel.ALL); }
	
	public void printHelp(ICommandSender ics)
	{
		printHelpLine(ics, "[gui]");
		printHelpLine(ics, "add <player>");
		printHelpLine(ics, "rem <player>");
		printHelpLine(ics, "addgroup <name>");
		printHelpLine(ics, "remgroup <name>");
		printHelpLine(ics, "addto <name> <player>");
		printHelpLine(ics, "remfrom <name> <player>");
	}
	
	public int getRequiredPermissionLevel()
	{ return 0; }
	
	public boolean canCommandSenderUseCommand(ICommandSender ics)
	{ return !LCConfig.General.disableLMFriendsCommand; }
	
	public String[] getSubcommands(ICommandSender ics)
	{ return new String[] { "help", "add", "rem", "addgroup", "remgroup", "rengroup", "addto", "remfrom" }; }
	
	public NameType getUsername(String[] args, int i)
	{
		if(i == 1 && isArg(args, 0, "add", "rem")) return NameType.OFF;
		if(i == 2 && isArg(args, 0, "addto", "remfrom")) return NameType.OFF;
		return NameType.NONE;
	}
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		if(i == 0) return getSubcommands(ics);
		if(i == 1 && isArg(args, 0, "remgroup", "rengroup", "addto", "remfrom"))
		{
			LMPlayer p = LMPlayer.getPlayer(ics);
			if(p != null && p.groups.hasKeys())
			{
				FastList<String> l = new FastList<String>();
				for(int j = 0; j < p.groups.size(); j++)
					l.add(p.groups.values.get(j).name);
				return l.toArray(new String[0]);
			}
		}
		return super.getTabStrings(ics, args, i);
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		
		if(args == null || args.length == 0 || args[0].equals("gui"))
		{ MessageLM.NET.sendTo(new MessageCustomServerAction(LCEventHandler.ACTION_OPEN_FRIENDS_GUI, null), ep); return null; }
		else if(args[0].equals("help")) { printHelp(ics); return null; }
		
		LMPlayer owner = LMPlayer.getPlayer(ep);
		LMPlayer p = null;
		boolean changed = false;
		
		if(args.length >= 2 && isArg(args, 0, "add", "rem")) p = LMPlayer.getPlayer(args[1]);
		if(args.length >= 3 && !isArg(args, 0, "add", "rem")) p = LMPlayer.getPlayer(args[2]);
		
		if(args[0].equals("add"))
		{
			if(p != null && !owner.friends.contains(p))
			{
				owner.friends.add(p);
				changed = true;
			}
		}
		else if(args[0].equals("rem"))
		{
			if(p != null && owner.friends.contains(p))
			{
				owner.friends.remove(p);
				changed = true;
			}
		}
		else
		{
			Integer gid = owner.groups.getKey(args[1]);
			int groupID = (gid == null) ? 0 : gid.intValue();
			
			if(args[0].equals("addgroup"))
			{
				if(owner.groups.size() < 8)
				{
					LMPlayer.Group g = new LMPlayer.Group(owner, ++owner.lastGroupID, "Unnamed");
					owner.groups.put(g.groupID, g);
					changed = true;
				}
			}
			else if(args[0].equals("remgroup"))
			{
				if(groupID > 0 && owner.groups.hasKeys())
				{
					if(owner.groups.remove(groupID))
						changed = true;
				}
			}
			else if(args[0].equals("rengroup"))
			{
				String groupName = args[2];
				
				if(groupID > 0 && owner.groups.hasKeys() && !groupName.isEmpty())
				{
					LMPlayer.Group g = owner.groups.get(groupID);
					if(g != null && !g.name.equals(groupName))
					{
						g.name = groupName;
						changed = true;
					}
				}
			}
			else if(args[0].equals("addto"))
			{
				if(p != null && owner.groups.hasKeys() && owner.groups.keys.contains(groupID))
				{
					LMPlayer.Group g = owner.groups.get(groupID);
					
					if(!g.members.contains(p))
						g.members.add(p);
					
					changed = true;
				}
			}
			else if(args[0].equals("remfrom"))
			{
				if(p != null && owner.groups.hasKeys() && owner.groups.keys.contains(groupID))
				{
					LMPlayer.Group g = owner.groups.get(groupID);
					
					if(g.members.contains(p))
						g.members.remove(p);
					
					changed = true;
				}
			}
		}
		
		if(changed)
		{
			owner.sendUpdate(LMPlayer.ACTION_GROUPS_CHANGED);
			
			if(p != null)
				p.sendUpdate(LMPlayer.ACTION_GROUPS_CHANGED);
		}
		
		return onCommand(ics, null);
	}
}