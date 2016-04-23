package ftb.utils.net;

import ftb.utils.api.guide.ServerInfoFile;
import ftb.utils.world.LMPlayerServer;
import ftb.utils.world.PersonalSettings;

import java.util.HashMap;

public abstract class ClientAction
{
	public static final ClientAction NULL = new ClientAction()
	{
		@Override
		public boolean onAction(int extra, LMPlayerServer owner)
		{ return false; }
	};
	
	public static final ClientAction ADD_FRIEND = new ClientAction()
	{
		@Override
		public boolean onAction(int extra, LMPlayerServer owner)
		{
			if(extra > 0)
			{
				LMPlayerServer p = owner.world.getPlayer(extra);
				if(p == null || p.equalsPlayer(owner)) return false;
				
				if(!owner.friends.contains(p.getPlayerID()))
				{
					owner.friends.add(p.getPlayerID());
					owner.sendUpdate();
					p.sendUpdate();
					p.checkNewFriends();
				}
			}
			else
			{
				for(LMPlayerServer p : owner.world.playerMap.values())
				{
					if(!p.equalsPlayer(owner) && p.isFriendRaw(owner) && !owner.isFriendRaw(p))
					{
						owner.friends.add(p.getPlayerID());
						owner.sendUpdate();
						p.sendUpdate();
						p.checkNewFriends();
					}
				}
			}
			
			return true;
		}
	};
	
	public static final ClientAction REM_FRIEND = new ClientAction()
	{
		@Override
		public boolean onAction(int extra, LMPlayerServer owner)
		{
			LMPlayerServer p = owner.world.getPlayer(extra);
			if(p == null || p.equalsPlayer(owner)) return false;
			
			if(owner.friends.contains(p.getPlayerID()))
			{
				owner.friends.removeValue(p.getPlayerID());
				owner.sendUpdate();
				p.sendUpdate();
				p.checkNewFriends();
			}
			
			return true;
		}
	};
	
	public static final ClientAction DENY_FRIEND = new ClientAction()
	{
		@Override
		public boolean onAction(int extra, LMPlayerServer owner)
		{
			LMPlayerServer p = owner.world.getPlayer(extra);
			if(p == null || p.equalsPlayer(owner)) return false;
			
			if(p.friends.contains(owner.getPlayerID()))
			{
				p.friends.removeValue(owner.getPlayerID());
				owner.sendUpdate();
				p.sendUpdate();
			}
			
			return true;
		}
	};
	
	public static final ClientAction REQUEST_PLAYER_INFO = new ClientAction()
	{
		@Override
		public boolean onAction(int extra, LMPlayerServer owner)
		{
			new MessageLMPlayerInfo(owner, extra).sendTo(owner.getPlayer());
			return false;
		}
	};
	
	public static final ClientAction REQUEST_SERVER_INFO = new ClientAction()
	{
		@Override
		public boolean onAction(int extra, LMPlayerServer owner)
		{
			new ServerInfoFile(owner).displayGuide(owner.getPlayer());
			return false;
		}
	};
	
	public static final ClientAction REQUEST_SELF_UPDATE = new ClientAction()
	{
		@Override
		public boolean onAction(int extra, LMPlayerServer owner)
		{
			new MessageLMPlayerUpdate(owner, true).sendTo(owner.getPlayer());
			return false;
		}
	};
	
	public static final ClientAction BUTTON_RENDER_BADGE = new ClientAction()
	{
		@Override
		public boolean onAction(int extra, LMPlayerServer owner)
		{
			owner.renderBadge = extra == 1;
			return true;
		}
	};
	
	public static final ClientAction BUTTON_CHAT_LINKS = new ClientAction()
	{
		@Override
		public boolean onAction(int extra, LMPlayerServer owner)
		{
			owner.getSettings().set(PersonalSettings.CHAT_LINKS, extra == 1);
			return true;
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
		//register(7, REQUEST_BADGE);
		register(8, BUTTON_RENDER_BADGE);
		register(9, BUTTON_CHAT_LINKS);
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
