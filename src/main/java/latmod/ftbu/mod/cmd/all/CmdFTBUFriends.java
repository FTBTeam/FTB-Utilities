package latmod.ftbu.mod.cmd.all;

import latmod.ftbu.core.*;
import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.FTBUGuiHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;

public class CmdFTBUFriends extends SubCommand
{
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		if(i == 0) return new String[] { "add", "rem", "list", "gui" };
		return null;
	}
	
	public NameType getUsername(String[] args, int i)
	{
		if(i == 1) return NameType.OFF;
		return NameType.NONE;
	}
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) //LANG
	{
		if(args.length == 0 || args[0].equals("gui"))
		{
			final EntityPlayerMP ep = CommandLM.getCommandSenderAsPlayer(ics);
			LatCoreMCClient.addCallbackEvent(new ServerTickCallback()
			{
				public void onCallback()
				{ FTBUGuiHandler.instance.openGui(ep, FTBUGuiHandler.FRIENDS, null); }
			});
			
			return null;
		}
		
		CommandLM.checkArgs(args, 1);
		
		LMPlayerServer owner = CommandLM.getLMPlayer(ics);
		
		if(args[0].equals("list"))
		{
			if(owner.friends.isEmpty()) return new ChatComponentText("No friends added");
			
			LatCoreMC.printChat(ics, "Your friends:");
			
			for(int i = 0; i < owner.friends.size(); i++)
			{
				LMPlayerServer p = LMWorldServer.inst.getPlayer(owner.friends.get(i));
				EnumChatFormatting col = EnumChatFormatting.GREEN;
				if(p.isFriendRaw(owner) && !owner.isFriendRaw(p)) col = EnumChatFormatting.GOLD;
				if(!p.isFriendRaw(owner) && owner.isFriendRaw(p)) col = EnumChatFormatting.BLUE;
				LatCoreMC.printChat(ics, col + "[" + i + "]: " + p.getName());
			}
			
			return null;
		}
		else if(args[0].equals("addall"))
		{
			int friendsAdded = 0;
			
			for(LMPlayerServer p1 : LMWorldServer.inst.players)
			{
				if(p1.isFriendRaw(owner) && !owner.isFriendRaw(p1))
				{
					friendsAdded++;
					
					if(!owner.friends.contains(p1.playerID))
					{
						owner.friends.add(p1.playerID);
						changed(owner, p1, null);
					}
				}
			}
			
			return new ChatComponentText("Added " + friendsAdded + " friends!");
		}
		else
		{
			CommandLM.checkArgs(args, 2);
			
			LMPlayerServer p = CommandLM.getLMPlayer(args[1]);
			
			if(p.equalsPlayer(owner)) return CommandLM.error(new ChatComponentText("Invalid player!"));
			
			if(args[0].equals("add"))
			{
				if(!owner.friends.contains(p.playerID))
				{
					owner.friends.add(p.playerID);
					return changed(owner, p, "Added " + p.getName() + " as friend");
				}
				
				return CommandLM.error(new ChatComponentText(p.getName() + " is already a friend!"));
			}
			else if(args[0].equals("rem"))
			{
				if(owner.friends.contains(p.playerID))
				{
					owner.friends.removeValue(p.playerID);
					return changed(owner, p, "Removed " + p.getName() + " from friends");
				}
				
				return CommandLM.error(new ChatComponentText(p.getName() + " is not added as friend!"));
			}
		}
		
		return null;
	}
	
	private static IChatComponent changed(LMPlayerServer o, LMPlayerServer p, String s)
	{
		o.sendUpdate(true);
		if(p != null) p.sendUpdate(true);
		return new ChatComponentText(s);
	}
}