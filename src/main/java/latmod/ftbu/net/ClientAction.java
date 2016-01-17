package latmod.ftbu.net;

import ftb.lib.FTBLib;
import latmod.ftbu.api.guide.*;
import latmod.ftbu.badges.*;
import latmod.ftbu.world.*;

import java.util.HashMap;

public abstract class ClientAction
{
	public static final ClientAction NULL = new ClientAction()
	{
		public boolean onAction(int extra, LMPlayerServer owner)
		{ return false; }
	};
	
	public static final ClientAction ADD_FRIEND = new ClientAction()
	{
		public boolean onAction(int extra, LMPlayerServer owner)
		{
			if(extra > 0)
			{
				LMPlayerServer p = owner.world.getPlayer(extra);
				if(p == null || p.equalsPlayer(owner)) return false;
				
				if(!owner.friends.contains(p.playerID))
				{
					owner.friends.add(p.playerID);
					owner.sendUpdate();
					p.sendUpdate();
					p.checkNewFriends();
					
					new MessageLMPlayerInfo(owner, p.playerID).sendTo(owner.getPlayer());
					new MessageLMPlayerInfo(owner, owner.playerID).sendTo(owner.getPlayer());
				}
			}
			else
			{
				for(LMPlayerServer p : owner.world.playerMap.values())
				{
					if(!p.equalsPlayer(owner) && p.isFriendRaw(owner) && !owner.isFriendRaw(p))
					{
						owner.friends.add(p.playerID);
						owner.sendUpdate();
						p.sendUpdate();
						p.checkNewFriends();
						
						new MessageLMPlayerInfo(owner, p.playerID).sendTo(owner.getPlayer());
					}
				}
				
				new MessageLMPlayerInfo(owner, owner.playerID).sendTo(owner.getPlayer());
			}
			
			return true;
		}
	};
	
	public static final ClientAction REM_FRIEND = new ClientAction()
	{
		public boolean onAction(int extra, LMPlayerServer owner)
		{
			LMPlayerServer p = owner.world.getPlayer(extra);
			if(p == null || p.equalsPlayer(owner)) return false;
			
			if(owner.friends.contains(p.playerID))
			{
				owner.friends.removeValue(p.playerID);
				owner.sendUpdate();
				p.sendUpdate();
				p.checkNewFriends();
				
				new MessageLMPlayerInfo(owner, p.playerID).sendTo(owner.getPlayer());
				new MessageLMPlayerInfo(owner, owner.playerID).sendTo(owner.getPlayer());
			}
			
			return true;
		}
	};
	
	public static final ClientAction DENY_FRIEND = new ClientAction()
	{
		public boolean onAction(int extra, LMPlayerServer owner)
		{
			LMPlayerServer p = owner.world.getPlayer(extra);
			if(p == null || p.equalsPlayer(owner)) return false;
			
			if(p.friends.contains(owner.playerID))
			{
				p.friends.removeValue(owner.playerID);
				owner.sendUpdate();
				p.sendUpdate();
				
				new MessageLMPlayerInfo(owner, p.playerID).sendTo(owner.getPlayer());
				new MessageLMPlayerInfo(owner, owner.playerID).sendTo(owner.getPlayer());
			}
			
			return true;
		}
	};
	
	public static final ClientAction REQUEST_PLAYER_INFO = new ClientAction()
	{
		public boolean onAction(int extra, LMPlayerServer owner)
		{
			new MessageLMPlayerInfo(owner, extra).sendTo(owner.getPlayer());
			return false;
		}
	};
	
	public static final ClientAction REQUEST_SERVER_INFO = new ClientAction()
	{
		public boolean onAction(int extra, LMPlayerServer owner)
		{
			GuideFile.displayGuide(owner.getPlayer(), new ServerGuideFile(owner));
			return false;
		}
	};
	
	public static final ClientAction REQUEST_SELF_UPDATE = new ClientAction()
	{
		public boolean onAction(int extra, LMPlayerServer owner)
		{
			new MessageLMPlayerUpdate(owner, true).sendTo(owner.getPlayer());
			return false;
		}
	};
	
	public static final ClientAction REQUEST_BADGE = new ClientAction()
	{
		public boolean onAction(int extra, LMPlayerServer owner)
		{
			Badge b = ServerBadges.getServerBadge(owner.world.getPlayer(extra));
			if(b != Badge.emptyBadge) new MessageSendBadge(extra, b.ID).sendTo(owner.getPlayer());
			return false;
		}
	};
	
	public static final ClientAction BUTTON_RENDER_BADGE = new ClientAction()
	{
		public boolean onAction(int extra, LMPlayerServer owner)
		{
			owner.renderBadge = extra == 1;
			return true;
		}
	};
	
	public static final ClientAction BUTTON_CHAT_LINKS = new ClientAction()
	{
		public boolean onAction(int extra, LMPlayerServer owner)
		{
			owner.getSettings().set(PersonalSettings.CHAT_LINKS, extra == 1);
			return true;
		}
	};
	
	public static final ClientAction BUTTON_CLAIMED_CHUNKS_SETTINGS = new ClientAction()
	{
		public boolean onAction(int extra, final LMPlayerServer owner)
		{
			FTBLib.printChat(owner.getPlayer(), "Settings Gui is temporarily replaced with /lmplayer_settings!");
			//ConfigRegistry.tempMap.put(provider.getID(), provider);
			//new MessageEditConfig(LMAccessToken.generate(owner.getPlayer()), true, provider).sendTo(owner.getPlayer());
			return false;
		}
	};
	
	private static final HashMap<Byte, ClientAction> map = new HashMap<>();
	
	private static void register(int i, ClientAction c)
	{
		c.ID = (byte) i;
		map.put(c.getID(), c);
	}
	
	static
	{
		register(0, NULL);
		register(1, ADD_FRIEND);
		register(2, REM_FRIEND);
		register(3, DENY_FRIEND);
		register(4, REQUEST_PLAYER_INFO);
		register(5, REQUEST_SERVER_INFO);
		register(6, REQUEST_SELF_UPDATE);
		register(7, REQUEST_BADGE);
		register(8, BUTTON_RENDER_BADGE);
		register(9, BUTTON_CHAT_LINKS);
		register(10, BUTTON_CLAIMED_CHUNKS_SETTINGS);
	}
	
	private byte ID;
	
	private ClientAction() { }
	
	public byte getID()
	{ return ID; }
	
	public abstract boolean onAction(int extra, LMPlayerServer owner);
	
	public void send(int extra)
	{ new MessageClientAction(this, extra).sendToServer(); }
	
	public static ClientAction get(byte id)
	{
		if(id == 0) return NULL;
		ClientAction a = map.get(id);
		if(a == null) return NULL;
		return a;
	}
}
