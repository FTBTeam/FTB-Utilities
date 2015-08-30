package latmod.ftbu.mod.client.gui.friends;

import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.util.FastList;
import latmod.ftbu.core.world.LMWorldClient;
import latmod.ftbu.mod.client.gui.*;

public abstract class PlayerAction
{
	public final TextureCoords icon;
	
	public PlayerAction(TextureCoords c)
	{ icon = c; }
	
	public abstract void onClicked(GuiFriends g);
	
	public TextureCoords getIcon(GuiFriends g)
	{ return icon; }
	
	public void addMouseOverText(FastList<String> l) { }
	
	// Self //
	
	public static final PlayerAction settings = new PlayerAction(GuiIcons.settings)
	{
		public void onClicked(GuiFriends g)
		{ g.mc.displayGuiScreen(new GuiClientConfig(g)); }
	};
	
	public static final PlayerAction waypoints = new PlayerAction(GuiIcons.compass)
	{
		public void onClicked(GuiFriends g)
		{ g.mc.displayGuiScreen(new GuiWaypoints()); }
	};
	
	public static final PlayerAction minimap = new PlayerAction(GuiIcons.map)
	{
		public void onClicked(GuiFriends g)
		{ g.mc.displayGuiScreen(new GuiMinimap()); }
	};
	
	public static final PlayerAction notes = new PlayerAction(GuiIcons.notes)
	{
		public void onClicked(GuiFriends g)
		{ /*WindowNotes.open();*/ }
	};
	
	public static final PlayerAction notifications = new PlayerAction(GuiIcons.comment)
	{
		public void onClicked(GuiFriends g)
		{
			//FIXME
			g.playClickSound();
			g.refreshWidgets();
		}
	};
	
	// Other players //
	
	public static final PlayerAction friend_toggle = new PlayerAction(GuiIcons.add)
	{
		public void onClicked(GuiFriends g)
		{
			if(LMWorldClient.inst.clientPlayer.isFriendRaw(GuiFriends.selectedPlayer.playerLM))
				LMNetHelper.sendToServer(new MessageClientGuiAction(MessageClientGuiAction.ACTION_REM_FRIEND, GuiFriends.selectedPlayer.playerLM.playerID));
			else
				LMNetHelper.sendToServer(new MessageClientGuiAction(MessageClientGuiAction.ACTION_ADD_FRIEND, GuiFriends.selectedPlayer.playerLM.playerID));
			g.refreshPlayers();
		}
		
		public TextureCoords getIcon(GuiFriends g)
		{ return LMWorldClient.inst.clientPlayer.isFriendRaw(GuiFriends.selectedPlayer.playerLM) ? GuiIcons.remove : GuiIcons.add; }
	};
	
	public static final PlayerAction friend_deny = new PlayerAction(GuiIcons.remove)
	{
		public void onClicked(GuiFriends g)
		{
			LMNetHelper.sendToServer(new MessageClientGuiAction(MessageClientGuiAction.ACTION_DENY_FRIEND, GuiFriends.selectedPlayer.playerLM.playerID));
			g.refreshPlayers();
		}
	};
}