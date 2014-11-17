package latmod.latcore.cmd;

import latmod.core.*;
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
		if(i == 0) return new String[] { "uuid", "killblock", "gamerule" };
		if(i == 1 && isArg(args, 0, "gamerule")) LMGamerules.rules.keys.toArray(new String[0]);
		return super.getTabStrings(ics, args, i);
	}
	
	public Boolean isUsername(String[] args, int i)
	{
		if(i == 1 && isArg(args, 0, "uuid")) return false;
		return null;
	}
	
	public void onCommand(ICommandSender ics, String[] args)
	{
		if(args == null || args.length == 0)
		{
			LatCoreMC.printChat(ics, "Subcommands: uuid, killblock, gamerule");
		}
		
		if(args[0].equals("uuid"))
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
		else if(args[0].equals("killblock"))
		{
			EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
			
			try
			{
				MovingObjectPosition mop = LatCoreMC.rayTrace(ep);
				
				//ep.worldObj.setTileEntity(mop.blockX, mop.blockY, mop.blockZ, null);
				ep.worldObj.setBlockToAir(mop.blockX, mop.blockY, mop.blockZ);
			}
			catch(Exception e)
			{ LatCoreMC.printChat(ics, "Failed to destroy the block!"); }
		}
		else if(args[0].equals("gamerule"))
		{
		}
		else processCommand(ics, null);
	}
}