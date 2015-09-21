package latmod.ftbu.mod.client.gui.friends;

import latmod.ftbu.core.client.FTBULang;
import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.util.FastList;
import latmod.ftbu.mod.client.gui.*;

public abstract class PlayerAction
{
	public final TextureCoords icon;
	
	public PlayerAction(TextureCoords c)
	{ icon = c; }
	
	public abstract void onClicked(GuiFriends g);
	public abstract String getTitle();
	
	public void addMouseOverText(FastList<String> l) { }
	
	// Self //
	
	public static final PlayerAction settings = new PlayerAction(GuiIcons.settings)
	{
		public void onClicked(GuiFriends g)
		{ g.mc.displayGuiScreen(new GuiClientConfig(g)); }
		
		public String getTitle()
		{ return FTBULang.client_config(); }
	};
	
	public static final PlayerAction minimap = new PlayerAction(GuiIcons.map)
	{
		public void onClicked(GuiFriends g)
		{ g.mc.displayGuiScreen(new GuiMinimap()); }
		
		public String getTitle()
		{ return FTBULang.Friends.claimed_chunks(); }
	};
	
	public static final PlayerAction notes = new PlayerAction(GuiIcons.notes)
	{
		public void onClicked(GuiFriends g)
		{  }
		
		public String getTitle()
		{ return FTBULang.Friends.notes(); }
	};
	
	// Other players //
	
	public static final PlayerAction friend_add = new PlayerAction(GuiIcons.add)
	{
		public void onClicked(GuiFriends g)
		{ LMNetHelper.sendToServer(new MessageClientGuiAction(MessageClientGuiAction.ACTION_ADD_FRIEND, g.panelPlayerView.selectedPlayer.playerLM.playerID)); }
		
		public String getTitle()
		{ return FTBULang.Friends.button_add_friend(); }
	};
	
	public static final PlayerAction friend_remove = new PlayerAction(GuiIcons.remove)
	{
		public void onClicked(GuiFriends g)
		{ LMNetHelper.sendToServer(new MessageClientGuiAction(MessageClientGuiAction.ACTION_REM_FRIEND, g.panelPlayerView.selectedPlayer.playerLM.playerID)); }
		
		public String getTitle()
		{ return FTBULang.Friends.button_rem_friend(); }
	};
	
	public static final PlayerAction friend_deny = new PlayerAction(GuiIcons.remove)
	{
		public void onClicked(GuiFriends g)
		{ LMNetHelper.sendToServer(new MessageClientGuiAction(MessageClientGuiAction.ACTION_DENY_FRIEND, g.panelPlayerView.selectedPlayer.playerLM.playerID)); }
		
		public String getTitle()
		{ return FTBULang.Friends.button_deny_friend(); }
	};
	
	public static final PlayerAction mail = new PlayerAction(GuiIcons.feather)
	{
		public void onClicked(GuiFriends g)
		{  }
		
		public String getTitle()
		{ return FTBULang.Friends.mail(); }
	};
	
	public static final PlayerAction trade = new PlayerAction(GuiIcons.moneybag)
	{
		public void onClicked(GuiFriends g)
		{  }
		
		public String getTitle()
		{ return FTBULang.Friends.trade(); }
	};
}