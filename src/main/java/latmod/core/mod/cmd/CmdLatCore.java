package latmod.core.mod.cmd;

import java.util.UUID;

import latmod.core.LatCoreMC;
import latmod.core.mod.*;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.*;
import net.minecraft.util.*;

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
			LatCoreMC.printChat(ics, "Subcommands: versions, uuid, friend, enemy");
		}
		else if(args[0].equalsIgnoreCase("versions"))
		{
			ThreadCheckVersions.init(ics, true);
		}
		else if(args != null)
		{
			if(args[0].equalsIgnoreCase("uuid"))
			{
				LMPlayer jp;
				
				if(args.length >= 2)
					jp = LMPlayer.getPlayer(args[1]);
				else
					jp = LMPlayer.getPlayer(getCommandSenderAsPlayer(ics).getUniqueID());
				
				if(jp == null) throw new PlayerNotFoundException();
				
				IChatComponent toPrint = new ChatComponentText(jp.getDisplayName() + "'s UUID: ");
				IChatComponent uuid = new ChatComponentText(jp.uuid.toString());
				uuid.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Copy to chat")));
				uuid.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, jp.uuid.toString()));
				uuid.getChatStyle().setColor(EnumChatFormatting.GOLD);
				toPrint.appendSibling(uuid);
				ics.addChatMessage(uuid);
			}
			else if(args[0].equalsIgnoreCase("friend"))
			{
				if(args.length == 1)
				{
					LatCoreMC.printChat(ics, "/latcore friend add|remove <name>");
					LatCoreMC.printChat(ics, "/latcore friend addUUID|remUUID <UUID>");
					LatCoreMC.printChat(ics, "/latcore friend list|clear");
					return;
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
						if(args[1].equals("add") || args[1].equals("addUUID"))
						{
							UUID id;
							String name;
							
							if(args[1].equals("add"))
							{
								LMPlayer jp = LMPlayer.getPlayer(args[2]);
								if(jp == null) throw new PlayerNotFoundException();
								id = jp.uuid;
								name = jp.getDisplayName();
							}
							else
							{
								id = UUID.fromString(args[2]);
								name = id.toString();
							}
							
							if(!epP.whitelist.contains(id))
							{
								epP.whitelist.add(id);
								LatCoreMC.printChat(ics, "Added " + name + " to your friend list");
							}
							else LatCoreMC.printChat(ics, name + " already added to your friend list!");
						}
						if(args[1].equals("rem") || args[1].equals("remUUID"))
						{
							UUID id;
							String name;
							
							if(args[1].equals("rem"))
							{
								LMPlayer jp = LMPlayer.getPlayer(args[2]);
								if(jp == null) throw new PlayerNotFoundException();
								id = jp.uuid;
								name = jp.getDisplayName();
							}
							else
							{
								id = UUID.fromString(args[2]);
								name = id.toString();
							}
							
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
			else if(args[0].equalsIgnoreCase("enemy"))
			{
				if(args.length == 1)
				{
					LatCoreMC.printChat(ics, "/latcore enemy add|rem <name>");
					LatCoreMC.printChat(ics, "/latcore enemy addUUID|remUUID <UUID>");
					LatCoreMC.printChat(ics, "/latcore enemy list|clear");
					return;
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
						if(args[1].equals("add") || args[1].equals("addUUID"))
						{
							UUID id;
							String name;
							
							if(args[1].equals("add"))
							{
								LMPlayer jp = LMPlayer.getPlayer(args[2]);
								if(jp == null) throw new PlayerNotFoundException();
								id = jp.uuid;
								name = jp.getDisplayName();
							}
							else
							{
								id = UUID.fromString(args[2]);
								name = id.toString();
							}
							
							if(!epP.blacklist.contains(id))
							{
								epP.blacklist.add(id);
								LatCoreMC.printChat(ics, "Added " + name + " to your enemy list");
							}
							else LatCoreMC.printChat(ics, name + " already added to your enemy list!");
						}
						if(args[1].equals("rem") || args[1].equals("remUUID"))
						{
							UUID id;
							String name;
							
							if(args[1].equals("rem"))
							{
								LMPlayer jp = LMPlayer.getPlayer(args[2]);
								if(jp == null) throw new PlayerNotFoundException();
								id = jp.uuid;
								name = jp.getDisplayName();
							}
							else
							{
								id = UUID.fromString(args[2]);
								name = id.toString();
							}
							
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
			else processCommand(ics, null);
		}
	}
}