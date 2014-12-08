package latmod.latcore.cmd;

import latmod.core.*;
import latmod.core.LMGamerules.RuleID;
import latmod.core.util.*;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.*;
import net.minecraft.util.*;

public class CmdLatCoreAdmin extends CommandBaseLC
{
	public CmdLatCoreAdmin(int e)
	{ super("latcoreadmin", e); }
	
	public void printHelp(ICommandSender ics)
	{
	}
	
	public String[] getSubcommands(ICommandSender ics)
	{ return new String[] { "killblock", "gamerule", "player" }; }
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		if(i == 0) return getSubcommands(ics);
		
		if(i == 2 && isArg(args, 0, "player"))
			return new String[] { "uuid", "delete", "saveinv", "loadinv", "nick", "skin", "cape" };
		
		if(isArg(args, 0, "gamerule"))
		{
			if(i == 1 || i == 2)
			{
				FastList<String> l = new FastList<String>();
				
				for(int j = 0; j < LMGamerules.rules.keys.size(); j++)
				{
					String s = i == 1 ? LMGamerules.rules.keys.get(j).group : LMGamerules.rules.keys.get(j).key;
					if(!l.contains(s)) l.add(s);
				}
				
				return l.toArray(new String[0]);
			}
		}
		
		return super.getTabStrings(ics, args, i);
	}
	
	public Boolean isUsername(String[] args, int i)
	{
		if(i == 1 && isArg(args, 0, "player")) return false;
		return null;
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(args == null || args.length == 0)
			return "Subcommands: " + LatCore.strip(getTabStrings(ics, args, 0));
		
		if(args[0].equals("player"))
		{
			if(args.length < 2) return "Missing arguments!";
			
			LMPlayer p = LMPlayer.getPlayer(args[1]);
			
			if(p == null) throw new PlayerNotFoundException();
			
			if(args[2].equals("uuid"))
			{
				IChatComponent toPrint = new ChatComponentText(p.getDisplayName() + "'s UUID: ");
				IChatComponent uuid = new ChatComponentText(p.uuid.toString());
				uuid.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Copy to chat")));
				uuid.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, p.uuid.toString()));
				uuid.getChatStyle().setColor(EnumChatFormatting.GOLD);
				toPrint.appendSibling(uuid);
				ics.addChatMessage(uuid);
				return null;
			}
			else if(args[2].equals("delete"))
			{
				if(p.isOnline()) return "The player must be offline!";
				
				return null;
			}
			else if(args[2].equals("saveinv"))
			{
				if(!p.isOnline()) return "The player must be online!";
				
				return null;
			}
			else if(args[2].equals("loadinv"))
			{
				if(!p.isOnline()) return "The player must be online!";
				
				return null;
			}
			else if(args[2].equals("nick"))
			{
				if(args.length != 4) return "/" + commandName + " ";
				
				p.setCustomName(args[3].trim());
				p.sendUpdate("CustomName");
				
				return FINE + "Custom nickname changed to " + p.getDisplayName() + " for " + p.username;
			}
			else if(args[2].equals("skin"))
			{
				if(args.length != 4) return "Missing arguments!";
				
				p.customSkin = args[3].trim();
				if(p.customSkin.length() == 0 || p.customSkin.equals("null"))
					p.customSkin = null;
				
				p.sendUpdate("CustomSkin");
				
				return FINE + "Custom skin changed to " + p.customSkin + " for " + p.username;
			}
			else if(args[2].equals("cape"))
			{
				if(args.length != 4) return "Missing arguments!";
				
				p.customCape = args[3].trim();
				if(p.customCape.length() == 0 || p.customCape.equals("null"))
					p.customCape = null;
				
				p.sendUpdate("CustomCape");
				
				return FINE + "Custom cape changed to " + p.customCape + " for " + p.username;
			}
		}
		else if(args[0].equals("killblock"))
		{
			EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
			
			try
			{
				MovingObjectPosition mop = LatCoreMC.rayTrace(ep);
				
				//ep.worldObj.setTileEntity(mop.blockX, mop.blockY, mop.blockZ, null);
				ep.worldObj.setBlockToAir(mop.blockX, mop.blockY, mop.blockZ);
				
				return FINE + "Block destroyed";
			}
			catch(Exception e)
			{ return "Failed to destroy the block!"; }
		}
		else if(args[0].equals("gamerule"))
		{
			if(args.length >= 3)
			{
				RuleID id = new RuleID(args[1], args[2]);
				
				if(args.length >= 4)
				{
					LMGamerules.set(id, args[3]);
					return FINE + "LMGamerule '" + id + "' set to " + args[3];
				}
				else
				{
					LMGamerules.Rule r = LMGamerules.get(id);
					if(r == null) return FINE + "LMGamerule '" + id + "' does not exist";
					return FINE + "LMGamrule '" + id + "' is '" + r.value + "'";
				}
			}
		}
		
		return onCommand(ics, null);
	}
}