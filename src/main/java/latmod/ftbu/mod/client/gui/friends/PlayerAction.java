package latmod.ftbu.mod.client.gui.friends;

import ftb.lib.client.FTBLibClient;
import ftb.lib.mod.FTBLibFinals;
import latmod.ftbu.api.client.EventPlayerAction;
import latmod.ftbu.mod.client.gui.GuiClientConfig;
import latmod.ftbu.mod.client.gui.minimap.GuiMinimap;
import latmod.ftbu.net.ClientAction;
import latmod.ftbu.util.client.FTBULang;
import latmod.ftbu.util.gui.*;
import latmod.ftbu.world.*;
import latmod.lib.FastList;

public abstract class PlayerAction
{
	public final TextureCoords icon;
	
	public PlayerAction(TextureCoords c)
	{ icon = c; }
	
	public abstract void onClicked(LMPlayerClient p);
	public abstract String getTitle();
	
	public void addMouseOverText(FastList<String> l) { }
	
	// Self //
	
	public static final PlayerAction settings = new PlayerAction(GuiIcons.settings)
	{
		public void onClicked(LMPlayerClient p)
		{ FTBLibClient.mc.displayGuiScreen(new GuiClientConfig(null)); }
		
		public String getTitle()
		{ return FTBULang.client_config(); }
	};
	
	public static final PlayerAction minimap = new PlayerAction(GuiIcons.map)
	{
		public void onClicked(LMPlayerClient p)
		{ FTBLibClient.mc.displayGuiScreen(new GuiMinimap()); }
		
		public String getTitle()
		{ return FTBULang.Friends.claimed_chunks(); }
	};
	
	public static final PlayerAction notes = new PlayerAction(GuiIcons.notes)
	{
		public void onClicked(LMPlayerClient p)
		{  }
		
		public String getTitle()
		{ return FTBULang.Friends.notes(); }
	};
	
	// Other players //
	
	public static final PlayerAction friend_add = new PlayerAction(GuiIcons.add)
	{
		public void onClicked(LMPlayerClient p)
		{ ClientAction.ACTION_ADD_FRIEND.send(p.playerID); }
		
		public String getTitle()
		{ return FTBULang.Friends.button_add_friend(); }
	};
	
	public static final PlayerAction friend_remove = new PlayerAction(GuiIcons.remove)
	{
		public void onClicked(LMPlayerClient p)
		{ ClientAction.ACTION_REM_FRIEND.send(p.playerID); }
		
		public String getTitle()
		{ return FTBULang.Friends.button_rem_friend(); }
	};
	
	public static final PlayerAction friend_deny = new PlayerAction(GuiIcons.remove)
	{
		public void onClicked(LMPlayerClient p)
		{ ClientAction.ACTION_DENY_FRIEND.send(p.playerID); }
		
		public String getTitle()
		{ return FTBULang.Friends.button_deny_friend(); }
	};
	
	public static final PlayerAction mail = new PlayerAction(GuiIcons.feather)
	{
		public void onClicked(LMPlayerClient p)
		{  }
		
		public String getTitle()
		{ return FTBULang.Friends.mail(); }
	};
	
	public static final PlayerAction trade = new PlayerAction(GuiIcons.moneybag)
	{
		public void onClicked(LMPlayerClient p)
		{  }
		
		public String getTitle()
		{ return FTBULang.Friends.trade(); }
	};

	public static FastList<PlayerAction> getActionsFor(LMPlayerClient p)
	{
		LMPlayerClient o = LMWorldClient.inst.clientPlayer;
		FastList<PlayerAction> list = new FastList<PlayerAction>();
		
		if(p.equalsPlayer(o))
		{
			list.add(PlayerAction.settings);
			list.add(PlayerAction.minimap);
			
			if(FTBLibFinals.DEV)
			{
				list.add(PlayerAction.notes);
			}
		}
		else
		{
			boolean isFriend = o.isFriendRaw(p);
			if(!isFriend) list.add(PlayerAction.friend_add);
			
			if(FTBLibFinals.DEV)
			{
				list.add(PlayerAction.mail);
				list.add(PlayerAction.trade);
			}
			
			if(isFriend) list.add(PlayerAction.friend_remove);
			else if(p.isFriendRaw(o))
				list.add(PlayerAction.friend_deny);
		}
		
		new EventPlayerAction(list, p).post();
		return list;
	}
}