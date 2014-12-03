package latmod.latcore.cmd;

import java.util.UUID;

import latmod.core.*;
import latmod.core.util.LatCore;
import latmod.latcore.ThreadCheckVersions;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;

public class CmdLatCore extends CommandBaseLC
{
	public CmdLatCore(int e)
	{ super("latcore", e); }
	
	public void printHelp(ICommandSender ics)
	{
		printHelpLine(ics, "<versions>");
		printHelpLine(ics, "<friend | enemy> <add | rem> <player>");
		printHelpLine(ics, "<friend | enemy> <list | clear>");
	}
	
	public String[] getSubcommands(ICommandSender ics)
	{ return new String[] { "versions", "friend", "enemy" }; }
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		if(i == 0) return getSubcommands(ics);
		if(i == 1 && (isArg(args, 0, "friend") || isArg(args, 0, "enemy"))) return new String[] { "add", "rem", "list", "clear" };
		return super.getTabStrings(ics, args, i);
	}
	
	public Boolean isUsername(String[] args, int i)
	{
		if(i == 2 && (isArg(args, 0, "friend") || isArg(args, 0, "enemy")) && (isArg(args, 1, "add") || isArg(args, 1, "rem"))) return true;
		return null;
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(args == null || args.length == 0)
			return "Subcommands: " + LatCore.strip(getTabStrings(ics, args, 0));
		
		if(args[0].equals("versions"))
		{
			ThreadCheckVersions.init(ics, true);
			return null;
		}
		
		if(args[0].equals("friend"))
		{
			if(args.length == 1)
			{
				LatCoreMC.printChat(ics, "/latcore friend add|rem <name>");
				LatCoreMC.printChat(ics, "/latcore friend list|clear");
				return null;
			}
			
			EntityPlayer ep = getCommandSenderAsPlayer(ics);
			
			if(args.length >= 2)
			{
				LMPlayer epP = LMPlayer.getPlayer(ep.getUniqueID());
				
				if(args[1].equals("list"))
				{
					String s = "";
					
					if(epP == null) throw new PlayerNotFoundException();
					
					for(int i = 0; i < epP.whitelist.size(); i++)
					{
						LMPlayer jp = LMPlayer.getPlayer(epP.whitelist.get(i));
						
						if(jp != null)
						{
							s += jp.username;
							if(i != epP.whitelist.size() - 1)
								s += ", ";
						}
					}
					
					if(s.length() > 0)
						LatCoreMC.printChat(ics, s);
					else LatCoreMC.printChat(ics, "Friend list is empty");
				}
				else if(args[1].equals("clear"))
				{
					epP.whitelist.clear();
					LatCoreMC.printChat(ics, "Friend list cleared");
				}
				else if(args.length >= 3)
				{
					if(args[1].equals("add"))
					{
						LMPlayer jp = LMPlayer.getPlayer(args[2]);
						if(jp == null) throw new PlayerNotFoundException();
						UUID id = jp.uuid;
						String name = jp.getDisplayName();
						
						if(!epP.whitelist.contains(id))
						{
							epP.whitelist.add(id);
							LatCoreMC.printChat(ics, "Added " + name + " to your friend list");
						}
						else LatCoreMC.printChat(ics, name + " already added to your friend list!");
					}
					if(args[1].equals("rem"))
					{
						LMPlayer jp = LMPlayer.getPlayer(args[2]);
						if(jp == null) throw new PlayerNotFoundException();
						UUID id = jp.uuid;
						String name = jp.getDisplayName();
						
						if(epP.whitelist.contains(id))
						{
							epP.whitelist.remove(id);
							LatCoreMC.printChat(ics, "Removed " + name + " from your friend list");
						}
						else LatCoreMC.printChat(ics, name + " is not added to your friend list!");
					}
				}
			}
		}
		else if(args[0].equals("enemy"))
		{
			if(args.length == 1)
			{
				LatCoreMC.printChat(ics, "/latcore enemy add|rem <name>");
				LatCoreMC.printChat(ics, "/latcore enemy list|clear");
				return null;
			}
			
			EntityPlayer ep = getCommandSenderAsPlayer(ics);
			
			if(args.length >= 2)
			{
				LMPlayer epP = LMPlayer.getPlayer(ep.getUniqueID());
				
				if(args[1].equals("list"))
				{
					String s = "";
					
					if(epP == null) throw new PlayerNotFoundException();
					
					for(int i = 0; i < epP.blacklist.size(); i++)
					{
						LMPlayer jp = LMPlayer.getPlayer(epP.blacklist.get(i));
						
						if(jp != null)
						{
							s += jp.username;
							if(i != epP.blacklist.size() - 1)
								s += ", ";
						}
					}
					
					if(s.length() > 0)
						LatCoreMC.printChat(ics, s);
					else LatCoreMC.printChat(ics, "Enemy list is empty");
				}
				else if(args[1].equals("clear"))
				{
					epP.blacklist.clear();
					LatCoreMC.printChat(ics, "Enemy list cleared");
				}
				else if(args.length >= 3)
				{
					if(args[1].equals("add"))
					{
						LMPlayer jp = LMPlayer.getPlayer(args[2]);
						if(jp == null) throw new PlayerNotFoundException();
						UUID id = jp.uuid;
						String name = jp.getDisplayName();
						
						if(!epP.blacklist.contains(id))
						{
							epP.blacklist.add(id);
							LatCoreMC.printChat(ics, "Added " + name + " to your enemy list");
						}
						else LatCoreMC.printChat(ics, name + " already added to your enemy list!");
					}
					if(args[1].equals("rem"))
					{
						LMPlayer jp = LMPlayer.getPlayer(args[2]);
						if(jp == null) throw new PlayerNotFoundException();
						UUID id = jp.uuid;
						String name = jp.getDisplayName();
						
						if(epP.blacklist.contains(id))
						{
							epP.blacklist.remove(id);
							LatCoreMC.printChat(ics, "Removed " + name + " from your enemy list");
						}
						else LatCoreMC.printChat(ics, name + " is not added to your nemy list!");
					}
				}
			}
		}
		
		return onCommand(ics, null);
	}
}