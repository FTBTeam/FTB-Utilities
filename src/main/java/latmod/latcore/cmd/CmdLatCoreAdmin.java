package latmod.latcore.cmd;

import latmod.core.*;
import latmod.core.LMGamerules.Rule;
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
	
	public String getCommandUsage(ICommandSender ics)
	{ return "/latcoreadmin <subcommand>"; }
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		if(i == 0) return new String[] { "killblock", "gamerule", "player" };
		
		if(i == 1 && isArg(args, 0, "player"))
			return new String[] { "uuid", "delete", "saveinv", "loadinv" };
		
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
		if(i == 1 && isArg(args, 0, "uuid")) return false;
		if(i == 2 && isArg(args, 0, "player")) return false;
		return null;
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(args == null || args.length == 0)
			return "Subcommands: " + LatCore.strip(getTabStrings(ics, args, 0));
		
		if(args[0].equals("player"))
		{
			if(args.length >= 2)
			{
				LMPlayer jp;
				
				if(args.length >= 3)
					jp = LMPlayer.getPlayer(args[2]);
				else
					jp = LMPlayer.getPlayer(getCommandSenderAsPlayer(ics).getUniqueID());
				
				if(jp == null) throw new PlayerNotFoundException();
				
				if(args[1].equals("uuid"))
				{
					IChatComponent toPrint = new ChatComponentText(jp.getDisplayName() + "'s UUID: ");
					IChatComponent uuid = new ChatComponentText(jp.uuid.toString());
					uuid.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Copy to chat")));
					uuid.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, jp.uuid.toString()));
					uuid.getChatStyle().setColor(EnumChatFormatting.GOLD);
					toPrint.appendSibling(uuid);
					ics.addChatMessage(uuid);
					return null;
				}
				else if(args[1].equals("delete"))
				{
					if(jp.isOnline()) return "The player must be offline!";
					
					return null;
				}
				else if(args[1].equals("saveinv"))
				{
					if(!jp.isOnline()) return "The player must be online!";
					
					return null;
				}
				else if(args[1].equals("loadinv"))
				{
					if(!jp.isOnline()) return "The player must be online!";
					
					return null;
				}
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
				
				return null;
			}
			catch(Exception e)
			{ return "Failed to destroy the block!"; }
		}
		else if(args[0].equals("gamerule"))
		{
			if(args.length >= 3)
			{
				RuleID id = new RuleID(args[1], args[2]);
				
				Rule r = LMGamerules.get(id);
				
				if(r != null)
				{
				}
				else LatCoreMC.printChat(ics, "");
			}
		}
		
		return onCommand(ics, null);
	}
}