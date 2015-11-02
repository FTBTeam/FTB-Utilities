package latmod.ftbu.net;

import ftb.lib.FTBLib;
import latmod.ftbu.api.guide.GuideFile;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.notification.*;
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
			if(extra > 0)
			{
				LMPlayerServer p = LMWorldServer.inst.getPlayer(extra);
				if(p == null || p.equalsPlayer(owner)) return false;
				
				if(!owner.friends.contains(p.playerID))
				{
					owner.friends.add(p.playerID);
					p.sendUpdate();
					
					if(p.isOnline())
					{
						Notification n = new Notification("friend_request", FTBLib.setColor(EnumChatFormatting.GREEN, new ChatComponentTranslation("ftbu:label.new_friend", owner.getName())), 4000);
						n.setDesc(new ChatComponentText("Click to add as friend"));
						n.setClickEvent(new ClickAction(ClickAction.FRIEND_ADD, owner.playerID));
						LatCoreMC.notifyPlayer(p.getPlayer(), n);
					}
				}
			}
			else
			{
				for(LMPlayer p : LMWorldServer.inst.players)
				{
					if(!p.equalsPlayer(owner) && p.isFriendRaw(owner) && !owner.isFriendRaw(p))
					{
						owner.friends.add(p.playerID);
						p.toPlayerMP().sendUpdate();
					}
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
				owner.sendUpdate();
				p.sendUpdate();
				Notification n = new Notification("friend_removed", FTBLib.setColor(EnumChatFormatting.RED, new ChatComponentText("Removed a friend")), 800);
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
				owner.sendUpdate();
				p.sendUpdate();
				
				Notification n = new Notification("friend_denied", FTBLib.setColor(EnumChatFormatting.RED, new ChatComponentText("Denied a friend request")), 800);
				n.setDesc(new ChatComponentText(p.getName()));
				LatCoreMC.notifyPlayer(ep, n);
			}
			
			return true;
		}
	},
	
	ACTION_EXPLOSIONS
	{
		public boolean onAction(int extra, EntityPlayerMP ep, LMPlayerServer owner)
		{
			owner.settings.explosions = (extra == 1);
			return true;
		}
	},
	
	ACTION_CHAT_LINKS
	{
		public boolean onAction(int extra, EntityPlayerMP ep, LMPlayerServer owner)
		{
			owner.settings.chatLinks = (extra == 1);
			return true;
		}
	},
	
	ACTION_RENDER_BADGE
	{
		public boolean onAction(int extra, EntityPlayerMP ep, LMPlayerServer owner)
		{
			owner.settings.renderBadge = (extra == 1);
			return true;
		}
	},
	
	ACTION_REQUEST_PLAYER_INFO
	{
		public boolean onAction(int extra, EntityPlayerMP ep, LMPlayerServer owner)
		{
			new MessageLMPlayerInfo(extra).sendTo(ep);
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
	
	ACTION_REQUEST_SERVER_INFO
	{
		public boolean onAction(int extra, EntityPlayerMP ep, LMPlayerServer owner)
		{
			GuideFile file = new GuideFile(new ChatComponentTranslation(FTBU.mod.assets + "button.server_info"));
			FTBU.proxy.addServerInfo(file, owner);
			LatCoreMC.displayGuide(ep, file);
			return false;
		}
	},
	
	ACTION_SET_CLAIM_BLOCKS
	{
		public boolean onAction(int extra, EntityPlayerMP ep, LMPlayerServer owner)
		{
			owner.settings.blocks = (extra == 0) ? owner.settings.blocks.next(LMSecurityLevel.VALUES_3) : owner.settings.blocks.prev(LMSecurityLevel.VALUES_3);
			return true;
		}
	},
	
	;
	public static final ClientAction[] VALUES = values();
	public final byte ID;
	
	ClientAction()
	{ ID = (byte)ordinal(); }
	
	public abstract boolean onAction(int extra, EntityPlayerMP ep, LMPlayerServer owner);
	
	public void send(int extra)
	{ new MessageClientAction(this, extra).sendToServer(); }
}