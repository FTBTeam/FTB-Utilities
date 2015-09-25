package latmod.ftbu.net;

import latmod.ftbu.util.*;
import latmod.ftbu.world.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;

public enum ClientAction
{
	NULL
	{
		public boolean onAction(int extra, EntityPlayerMP ep, LMPlayerServer owner)
		{ return false; }
	},
	
	ACTION_ADD_FRIEND
	{
		public boolean onAction(int extra, EntityPlayerMP ep, LMPlayerServer owner)
		{
			LMPlayerServer p = LMWorldServer.inst.getPlayer(extra);
			if(p == null || p.equalsPlayer(owner)) return false;
			
			if(!owner.friends.contains(p.playerID))
			{
				owner.friends.add(p.playerID);
				p.sendUpdate(true);
				
				if(p.isOnline())
				{
					Notification n = new Notification("friend_request", LatCoreMC.setColor(EnumChatFormatting.GREEN, new ChatComponentText("New friend request from " + owner.getName() + "!")), 4000);
					n.setDesc(new ChatComponentText("Click to add as friend"));
					n.setClickEvent(new NotificationClick(NotificationClick.CMD, "/ftbu friends add " + owner.getName()));
					LatCoreMC.notifyPlayer(p.getPlayer(), n);
				}
			}
			
			return true;
		}
	},
	
	ACTION_REM_FRIEND
	{
		public boolean onAction(int extra, EntityPlayerMP ep, LMPlayerServer owner)
		{
			LMPlayerServer p = LMWorldServer.inst.getPlayer(extra);
			if(p == null || p.equalsPlayer(owner)) return false;
			
			if(owner.friends.contains(p.playerID))
			{
				owner.friends.removeValue(p.playerID);
				owner.sendUpdate(true);
				p.sendUpdate(true);
				Notification n = new Notification("friend_removed", LatCoreMC.setColor(EnumChatFormatting.RED, new ChatComponentText("Removed a friend")), 800);
				n.setDesc(new ChatComponentText(p.getName()));
				LatCoreMC.notifyPlayer(ep, n);
			}
			
			return true;
		}
	},
	
	ACTION_DENY_FRIEND
	{
		public boolean onAction(int extra, EntityPlayerMP ep, LMPlayerServer owner)
		{
			LMPlayerServer p = LMWorldServer.inst.getPlayer(extra);
			if(p == null || p.equalsPlayer(owner)) return false;
			
			if(p.friends.contains(owner.playerID))
			{
				p.friends.removeValue(owner.playerID);
				owner.sendUpdate(true);
				p.sendUpdate(true);
				
				Notification n = new Notification("friend_denied", LatCoreMC.setColor(EnumChatFormatting.RED, new ChatComponentText("Denied a friend request")), 800);
				n.setDesc(new ChatComponentText(p.getName()));
				LatCoreMC.notifyPlayer(ep, n);
			}
			
			return true;
		}
	},
	
	ACTION_SET_SAFE_CHUNKS
	{
		public boolean onAction(int extra, EntityPlayerMP ep, LMPlayerServer owner)
		{
			owner.claims.settings.setSafe(owner, extra == 1);
			return true;
		}
	},
	
	ACTION_CHAT_LINKS
	{
		public boolean onAction(int extra, EntityPlayerMP ep, LMPlayerServer owner)
		{
			owner.chatLinks = (extra == 1);
			return true;
		}
	},
	
	ACTION_RENDER_BADGE
	{
		public boolean onAction(int extra, EntityPlayerMP ep, LMPlayerServer owner)
		{
			owner.renderBadge = (extra == 1);
			return true;
		}
	},
	
	ACTION_REQUEST_PLAYER_INFO
	{
		public boolean onAction(int extra, EntityPlayerMP ep, LMPlayerServer owner)
		{
			LMNetHelper.sendTo(ep, new MessageLMPlayerInfo(LMWorldServer.inst.getPlayer(extra)));
			return false;
		}
	},
	
	ACTION_REQUEST_SERVER_CONFIG
	{
		public boolean onAction(int extra, EntityPlayerMP ep, LMPlayerServer owner)
		{
			return false;
		}
	},
	
	;
	public static final ClientAction[] VALUES = values();
	public final byte ID;
	
	ClientAction()
	{ ID = (byte)ordinal(); }
	
	public abstract boolean onAction(int extra, EntityPlayerMP ep, LMPlayerServer owner);
	
	public void send(int extra)
	{ LMNetHelper.sendToServer(new MessageClientAction(this, extra)); }
}