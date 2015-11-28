package latmod.ftbu.net;

import latmod.ftbu.api.guide.ServerGuideFile;
import latmod.ftbu.util.*;
import latmod.ftbu.world.*;
import net.minecraft.entity.player.EntityPlayerMP;

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
					owner.sendUpdate();
					p.sendUpdate();
					p.checkNewFriends();
				}
			}
			else
			{
				for(int i = 0; i < LMWorldServer.inst.players.size(); i++)
				{
					LMPlayerServer p = LMWorldServer.inst.players.get(i).toPlayerMP();
					
					if(!p.equalsPlayer(owner) && p.isFriendRaw(owner) && !owner.isFriendRaw(p))
					{
						owner.friends.add(p.playerID);
						owner.sendUpdate();
						p.sendUpdate();
						p.checkNewFriends();
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
				p.checkNewFriends();
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
			LatCoreMC.displayGuide(ep, new ServerGuideFile(owner));
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