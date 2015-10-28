package latmod.ftbu.mod.client.gui.friends;

import ftb.lib.client.*;
import ftb.lib.mod.FTBLibFinals;
import latmod.ftbu.api.client.EventPlayerAction;
import latmod.ftbu.mod.client.gui.GuiClientConfig;
import latmod.ftbu.mod.client.gui.minimap.GuiMinimap;
import latmod.ftbu.net.ClientAction;
import latmod.ftbu.util.client.FTBULang;
import latmod.ftbu.util.gui.GuiIcons;
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
	
	public static final PlayerAction server_info = new PlayerAction(GuiIcons.info)
	{
		public void onClicked(LMPlayerClient p)
		{ ClientAction.ACTION_REQUEST_SERVER_INFO.send(p.playerID); }
		
		public String getTitle()
		{ return FTBULang.Guis.button_server_info(); }
	};
	
	public static final PlayerAction minimap = new PlayerAction(GuiIcons.map)
	{
		public void onClicked(LMPlayerClient p)
		{ FTBLibClient.mc.displayGuiScreen(new GuiMinimap()); }
		
		public String getTitle()
		{ return FTBULang.Guis.claimed_chunks(); }
	};
	
	public static final PlayerAction notes = new PlayerAction(GuiIcons.notes)
	{
		public void onClicked(LMPlayerClient p)
		{  }
		
		public String getTitle()
		{ return FTBULang.Guis.notes(); }
	};
	
	// Other players //
	
	public static final PlayerAction friend_add = new PlayerAction(GuiIcons.add)
	{
		public void onClicked(LMPlayerClient p)
		{ ClientAction.ACTION_ADD_FRIEND.send(p.playerID); }
		
		public String getTitle()
		{ return FTBULang.Guis.button_add_friend(); }
	};
	
	public static final PlayerAction friend_remove = new PlayerAction(GuiIcons.remove)
	{
		public void onClicked(LMPlayerClient p)
		{ ClientAction.ACTION_REM_FRIEND.send(p.playerID); }
		
		public String getTitle()
		{ return FTBULang.Guis.button_rem_friend(); }
	};
	
	public static final PlayerAction friend_deny = new PlayerAction(GuiIcons.remove)
	{
		public void onClicked(LMPlayerClient p)
		{ ClientAction.ACTION_DENY_FRIEND.send(p.playerID); }
		
		public String getTitle()
		{ return FTBULang.Guis.button_deny_friend(); }
	};
	
	public static final PlayerAction mail = new PlayerAction(GuiIcons.feather)
	{
		public void onClicked(LMPlayerClient p)
		{  }
		
		public String getTitle()
		{ return FTBULang.Guis.mail(); }
	};
	
	public static final PlayerAction trade = new PlayerAction(GuiIcons.moneybag)
	{
		public void onClicked(LMPlayerClient p)
		{  }
		
		public String getTitle()
		{ return FTBULang.Guis.trade(); }
	};

	public static FastList<PlayerAction> getActionsFor(LMPlayerClient p)
	{
		LMPlayerClient o = LMWorldClient.inst.clientPlayer;
		FastList<PlayerAction> list = new FastList<PlayerAction>();
		
		if(p.equalsPlayer(o))
		{
			list.add(settings);
			list.add(server_info);
			list.add(minimap);
			
			if(FTBLibFinals.DEV)
			{
				//list.add(notes);
			}
		}
		else
		{
			boolean isFriend = o.isFriendRaw(p);
			if(!isFriend) list.add(friend_add);
			
			if(FTBLibFinals.DEV)
			{
				list.add(mail);
				//list.add(trade);
			}
			
			if(isFriend) list.add(friend_remove);
			else if(p.isFriendRaw(o))
				list.add(friend_deny);
		}
		
		new EventPlayerAction(list, p).post();
		return list;
	}
}