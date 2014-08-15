package latmod.core.mod;

import java.util.UUID;

import latmod.core.LatCore;
import latmod.core.security.*;
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
			LatCore.printChat(ics, "Subcommands: uuid, whitelist, blacklist, team");
		}
		else if(args != null)
		{
			if(args[0].equalsIgnoreCase("uuid"))
			{
				EntityPlayer ep = getCommandSenderAsPlayer(ics);
				
				JsonPlayer jp = LMSecurity.getPlayer(ep.getUniqueID());
				
				if(args.length >= 2)
					jp = LMSecurity.getPlayer(args[1]);
				
				if(jp == null) throw new PlayerNotFoundException();
				
				LatCore.printChat(ics, jp.displayName + "'s UUID: " + jp.uuid);
			}
			else if(args[0].equalsIgnoreCase("whitelist") || args[0].equals("wl"))
			{
				EntityPlayer ep = getCommandSenderAsPlayer(ics);
				
				if(args.length >= 2 && ep != null)
				{
					JsonPlayer epP = LMSecurity.getPlayer(ep.getUniqueID());
					
					if(args[1].equals("list"))
					{
						String s = "";
						
						if(epP == null) throw new PlayerNotFoundException();
						
						for(int i = 0; i < epP.whitelist.size(); i++)
						{
							String uuidS = epP.whitelist.get(i);
							JsonPlayer jp = LMSecurity.getPlayer(UUID.fromString(uuidS));
							
							if(jp != null)
							{
								s += jp.displayName;
								if(i != epP.whitelist.size() - 1)
									s += ", ";
							}
						}
						
						if(s.length() > 0)
							LatCore.printChat(ics, s);
						else LatCore.printChat(ics, "Whitelist is empty");
					}
					else if(args[1].equals("clear"))
					{
						epP.whitelist.clear();
					}
					else if(args.length >= 3)
					{
						if(args[1].equals("add"))
						{
							JsonPlayer jp = LMSecurity.getPlayer(args[2]);
							
							if(jp == null) throw new PlayerNotFoundException();
							
							if(!epP.whitelist.contains(jp.uuid))
							{
								epP.whitelist.add(jp.uuid);
								LatCore.printChat(ics, "Added " + jp.displayName + " to whitelist");
							}
						}
						else if(args[1].equals("remove"))
						{
							JsonPlayer jp = LMSecurity.getPlayer(args[2]);
							
							if(jp == null) throw new PlayerNotFoundException();
							
							if(epP.whitelist.contains(jp.uuid))
							{
								epP.whitelist.remove(jp.uuid);
								LatCore.printChat(ics, "Removed " + jp.displayName + " from whitelist");
							}
						}
					}
				}
			}
			else if(args[0].equalsIgnoreCase("blacklist") || args[0].equals("bl"))
			{
				EntityPlayer ep = getCommandSenderAsPlayer(ics);
				
				if(args.length >= 2 && ep != null)
				{
					JsonPlayer epP = LMSecurity.getPlayer(ep.getUniqueID());
					
					if(args[1].equals("list"))
					{
						String s = "";
						
						if(epP == null) throw new PlayerNotFoundException();
						
						for(int i = 0; i < epP.blacklist.size(); i++)
						{
							String uuidS = epP.blacklist.get(i);
							JsonPlayer jp = LMSecurity.getPlayer(UUID.fromString(uuidS));
							
							if(jp != null)
							{
								s += jp.displayName;
								if(i != epP.whitelist.size() - 1)
									s += ", ";
							}
						}
						
						if(s.length() > 0)
							LatCore.printChat(ics, s);
						else LatCore.printChat(ics, "Blacklist is empty");
					}
					else if(args[1].equals("clear"))
					{
						epP.blacklist.clear();
					}
					else if(args.length >= 3)
					{
						if(args[1].equals("add"))
						{
							JsonPlayer jp = LMSecurity.getPlayer(args[2]);
							
							if(jp == null) throw new PlayerNotFoundException();
							
							if(!epP.blacklist.contains(jp.uuid))
							{
								epP.blacklist.add(jp.uuid);
								LatCore.printChat(ics, "Added " + jp.displayName + " to blacklist");
							}
						}
						else if(args[1].equals("remove"))
						{
							JsonPlayer jp = LMSecurity.getPlayer(args[2]);
							
							if(jp == null) throw new PlayerNotFoundException();
							
							if(epP.blacklist.contains(jp.uuid))
							{
								epP.blacklist.remove(jp.uuid);
								LatCore.printChat(ics, "Removed " + jp.displayName + " from blacklist");
							}
						}
					}
				}
			}
			else if(args[0].equalsIgnoreCase("team"))
			{
				LatCore.printChat(ics, "LatMod Team:");
				
				String s = "";
				
				for(int i = 0 ; i < LC.teamLatModNames.size(); i++)
				{
					s += LC.teamLatModNames.get(i);
					
					if(i != LC.teamLatModNames.size() - 1)
						s += ", ";
				}
				
				if(s.length() > 0) LatCore.printChat(ics, s);
				else LatCore.printChat(ics, "Team list is empty? Hm. Weird");
			}
			else processCommand(ics, null);
		}
	}
}