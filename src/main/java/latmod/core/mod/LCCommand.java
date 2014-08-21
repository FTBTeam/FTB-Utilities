package latmod.core.mod;

import java.util.UUID;

import latmod.core.LatCoreMC;
import latmod.core.security.JsonPlayer;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;

public class LCCommand extends CommandBase
{
	public String getCommandName()
	{
		return "latcore";
	}
	
	public int getRequiredPermissionLevel()
	{ return 0; }
	
	public boolean canCommandSenderUseCommand(ICommandSender ics)
	{ return true; }

	public String getCommandUsage(ICommandSender ics)
	{ return "/latcore <subcommand>"; }

	public void processCommand(ICommandSender ics, String[] args)
	{
		if(args == null || args.length == 0)
		{
			LatCoreMC.printChat(ics, "Subcommands: uuid, whitelist, blacklist, team, versions");
		}
		else if(args != null)
		{
			if(args[0].equalsIgnoreCase("uuid"))
			{
				EntityPlayer ep = getCommandSenderAsPlayer(ics);
				
				JsonPlayer jp = JsonPlayer.getPlayer(ep.getUniqueID());
				
				if(args.length >= 2)
					jp = JsonPlayer.getPlayer(args[1]);
				
				if(jp == null) throw new PlayerNotFoundException();
				
				LatCoreMC.printChat(ics, jp.displayName + "'s UUID: " + jp.uuid);
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
					JsonPlayer epP = JsonPlayer.getPlayer(ep.getUniqueID());
					
					if(args[1].equals("list"))
					{
						String s = "";
						
						if(epP == null) throw new PlayerNotFoundException();
						
						for(int i = 0; i < epP.whitelist.size(); i++)
						{
							String uuidS = epP.whitelist.get(i);
							JsonPlayer jp = JsonPlayer.getPlayer(UUID.fromString(uuidS));
							
							if(jp != null)
							{
								s += jp.displayName;
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
							JsonPlayer jp = JsonPlayer.getPlayer(args[1].equals("add") ? args[2] : UUID.fromString(args[2]));
							
							if(jp == null) throw new PlayerNotFoundException();
							
							if(!epP.whitelist.contains(jp.uuid))
							{
								epP.whitelist.add(jp.uuid);
								LatCoreMC.printChat(ics, "Added " + jp.displayName + " to whitelist");
							}
							else LatCoreMC.printChat(ics, jp.displayName + " already added to whitelist!");
						}
						if(args[1].equals("rem") || args[1].equals("remUUID"))
						{
							JsonPlayer jp = JsonPlayer.getPlayer(args[1].equals("rem") ? args[2] : UUID.fromString(args[2]));
							
							if(jp == null) throw new PlayerNotFoundException();
							
							if(epP.whitelist.contains(jp.uuid))
							{
								epP.whitelist.remove(jp.uuid);
								LatCoreMC.printChat(ics, "Removed " + jp.displayName + " from whitelist");
							}
							else LatCoreMC.printChat(ics, jp.displayName + " is not added to whitelist!");
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
					JsonPlayer epP = JsonPlayer.getPlayer(ep.getUniqueID());
					
					if(args[1].equals("list"))
					{
						String s = "";
						
						if(epP == null) throw new PlayerNotFoundException();
						
						for(int i = 0; i < epP.blacklist.size(); i++)
						{
							String uuidS = epP.blacklist.get(i);
							JsonPlayer jp = JsonPlayer.getPlayer(UUID.fromString(uuidS));
							
							if(jp != null)
							{
								s += jp.displayName;
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
							JsonPlayer jp = JsonPlayer.getPlayer(args[1].equals("add") ? args[2] : UUID.fromString(args[2]));
							
							if(jp == null) throw new PlayerNotFoundException();
							
							if(!epP.blacklist.contains(jp.uuid))
							{
								epP.blacklist.add(jp.uuid);
								LatCoreMC.printChat(ics, "Added " + jp.displayName + " to blacklist");
							}
							else LatCoreMC.printChat(ics, jp.displayName + " already added to blacklist!");
						}
						if(args[1].equals("rem") || args[1].equals("remUUID"))
						{
							JsonPlayer jp = JsonPlayer.getPlayer(args[1].equals("rem") ? args[2] : UUID.fromString(args[2]));
							
							if(jp == null) throw new PlayerNotFoundException();
							
							if(epP.blacklist.contains(jp.uuid))
							{
								epP.blacklist.remove(jp.uuid);
								LatCoreMC.printChat(ics, "Removed " + jp.displayName + " from blacklist");
							}
							else LatCoreMC.printChat(ics, jp.displayName + " is not added to blacklist!");
						}
					}
				}
			}
			else if(args[0].equalsIgnoreCase("team"))
			{
				if(args.length >= 2)
				{
					EnumLatModTeam e = EnumLatModTeam.get(args[1]);
					
					LatCoreMC.printChat(ics, "LatMod Team:");
					
					String s = "";
					
					for(int i = 0 ; i < e.names.size(); i++)
					{
						s += e.names.get(i);
						
						if(i != e.names.size() - 1)
							s += ", ";
					}
					
					if(s.length() > 0) LatCoreMC.printChat(ics, s);
					else LatCoreMC.printChat(ics, "Team list is empty? Hm. Weird. Oh well...");
				}
				else LatCoreMC.printChat(ics, "/latcore team <name>");
			}
			else if(args[0].equalsIgnoreCase("versions"))
			{
				ThreadCheckVersions.init(ics, true);
			}
			else processCommand(ics, null);
		}
	}
}