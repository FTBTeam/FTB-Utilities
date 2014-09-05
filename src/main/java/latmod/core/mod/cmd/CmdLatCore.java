package latmod.core.mod.cmd;

import java.util.UUID;

import latmod.core.LatCoreMC;
import latmod.core.mod.*;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;

public class CmdLatCore extends CommandBaseLC
{
	public CmdLatCore(int e)
	{ super(e); }
	
	public String getCommandName() 
	{ return "latcore"; }
	
	public String getCommandUsage(ICommandSender ics)
	{ return "/latcore <subcommand>"; }
	
	public void processCommand(ICommandSender ics, String[] args)
	{
		if(args == null || args.length == 0)
		{
			LatCoreMC.printChat(ics, "Subcommands: versions, uuid, whitelist, blacklist");
		}
		else if(args[0].equalsIgnoreCase("versions"))
		{
			ThreadCheckVersions.init(ics, true);
		}
		else if(args != null)
		{
			if(args[0].equalsIgnoreCase("uuid"))
			{
				EntityPlayer ep = getCommandSenderAsPlayer(ics);
				
				LMPlayer jp = LMPlayer.getPlayer(ep.getUniqueID());
				
				if(args.length >= 2)
					jp = LMPlayer.getPlayer(args[1]);
				
				if(jp == null) throw new PlayerNotFoundException();
				
				LatCoreMC.printChat(ics, jp.username + "'s UUID: " + jp.uuid);
			}
			else if(args[0].equalsIgnoreCase("whitelist") || args[0].equals("wl"))
			{
				if(args.length == 1)
				{
					LatCoreMC.printChat(ics, "/latcore whitelist add|remove <player name>");
					LatCoreMC.printChat(ics, "/latcore whitelist addUUID|remUUID <player UUID>");
					LatCoreMC.printChat(ics, "/latcore whitelist list|clear");
					return;
				}
				
				EntityPlayer ep = getCommandSenderAsPlayer(ics);
				
				if(ep == null)
				{
					LatCoreMC.printChat(ics, "Player can't be null!");
					return;
				}
				
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
						else LatCoreMC.printChat(ics, "Whitelist is empty");
					}
					else if(args[1].equals("clear"))
					{
						epP.whitelist.clear();
						LatCoreMC.printChat(ics, "Whitelist cleared");
					}
					else if(args.length >= 3)
					{
						if(args[1].equals("add") || args[1].equals("addUUID"))
						{
							LMPlayer jp = LMPlayer.getPlayer(args[1].equals("add") ? args[2] : UUID.fromString(args[2]));
							
							if(jp == null) throw new PlayerNotFoundException();
							
							if(!epP.whitelist.contains(jp.uuid))
							{
								epP.whitelist.add(jp.uuid);
								LatCoreMC.printChat(ics, "Added " + jp.username + " to whitelist");
							}
							else LatCoreMC.printChat(ics, jp.username + " already added to whitelist!");
						}
						if(args[1].equals("rem") || args[1].equals("remUUID"))
						{
							LMPlayer jp = LMPlayer.getPlayer(args[1].equals("rem") ? args[2] : UUID.fromString(args[2]));
							
							if(jp == null) throw new PlayerNotFoundException();
							
							if(epP.whitelist.contains(jp.uuid))
							{
								epP.whitelist.remove(jp.uuid);
								LatCoreMC.printChat(ics, "Removed " + jp.username + " from whitelist");
							}
							else LatCoreMC.printChat(ics, jp.username + " is not added to whitelist!");
						}
					}
				}
			}
			else if(args[0].equalsIgnoreCase("blacklist") || args[0].equals("bl"))
			{
				if(args.length == 1)
				{
					LatCoreMC.printChat(ics, "/latcore blacklist add|rem <player name>");
					LatCoreMC.printChat(ics, "/latcore blacklist addUUID|remUUID <player UUID>");
					LatCoreMC.printChat(ics, "/latcore blacklist list|clear");
					return;
				}
				
				EntityPlayer ep = getCommandSenderAsPlayer(ics);
				
				if(ep == null)
				{
					LatCoreMC.printChat(ics, "Player can't be null!");
					return;
				}
				
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
						else LatCoreMC.printChat(ics, "Blacklist is empty");
					}
					else if(args[1].equals("clear"))
					{
						epP.blacklist.clear();
						LatCoreMC.printChat(ics, "Blacklist cleared");
					}
					else if(args.length >= 3)
					{
						if(args[1].equals("add") || args[1].equals("addUUID"))
						{
							LMPlayer jp = LMPlayer.getPlayer(args[1].equals("add") ? args[2] : UUID.fromString(args[2]));
							
							if(jp == null) throw new PlayerNotFoundException();
							
							if(!epP.blacklist.contains(jp.uuid))
							{
								epP.blacklist.add(jp.uuid);
								LatCoreMC.printChat(ics, "Added " + jp.username + " to blacklist");
							}
							else LatCoreMC.printChat(ics, jp.username + " already added to blacklist!");
						}
						if(args[1].equals("rem") || args[1].equals("remUUID"))
						{
							LMPlayer jp = LMPlayer.getPlayer(args[1].equals("rem") ? args[2] : UUID.fromString(args[2]));
							
							if(jp == null) throw new PlayerNotFoundException();
							
							if(epP.blacklist.contains(jp.uuid))
							{
								epP.blacklist.remove(jp.uuid);
								LatCoreMC.printChat(ics, "Removed " + jp.username + " from blacklist");
							}
							else LatCoreMC.printChat(ics, jp.username + " is not added to blacklist!");
						}
					}
				}
			}
			else processCommand(ics, null);
		}
	}
}