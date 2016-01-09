package latmod.ftbu.mod.client;

import ftb.lib.api.*;
import ftb.lib.api.gui.GuiIcons;
import ftb.lib.client.*;
import ftb.lib.mod.FTBLibFinals;
import ftb.lib.mod.client.FTBLibGuiEventHandler;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.gui.claims.GuiClaimChunks;
import latmod.ftbu.mod.client.gui.friends.GuiFriends;
import latmod.ftbu.mod.client.gui.guide.GuiGuide;
import latmod.ftbu.net.ClientAction;
import latmod.ftbu.world.*;

import java.util.*;

public class FTBUActions
{
	// FriendsGUI //
	
	public static final PlayerAction friends_gui = new PlayerAction("ftbu:friends_gui", TextureCoords.getSquareIcon(FTBU.mod.getLocation("textures/gui/friendsbutton.png"), 256))
	{
		public void onClicked(int playerID)
		{ FTBLibClient.mc.displayGuiScreen(new GuiFriends(FTBLibClient.mc.currentScreen)); }
		
		public String getTitleKey()
		{ return ID; }
	};
	
	// Other playerMap //
	
	public static final PlayerAction friend_add = new PlayerAction("ftbu:button.add_friend", GuiIcons.add)
	{
		public void onClicked(int playerID)
		{ ClientAction.ADD_FRIEND.send(playerID); }
		
		public String getTitleKey()
		{ return ID; }
	};
	
	public static final PlayerAction friend_remove = new PlayerAction("ftbu:button.rem_friend", GuiIcons.remove)
	{
		public void onClicked(int playerID)
		{ ClientAction.REM_FRIEND.send(playerID); }
		
		public String getTitleKey()
		{ return ID; }
	};
	
	public static final PlayerAction friend_deny = new PlayerAction("ftbu:button.deny_friend", GuiIcons.remove)
	{
		public void onClicked(int playerID)
		{ ClientAction.DENY_FRIEND.send(playerID); }
		
		public String getTitleKey()
		{ return ID; }
	};
	
	public static final PlayerAction mail = new PlayerAction("ftbu:button.mail", GuiIcons.feather)
	{
		public void onClicked(int playerID)
		{ }
		
		public String getTitleKey()
		{ return ID; }
	};
	
	public static final PlayerAction trade = new PlayerAction("ftbu:button.trade", GuiIcons.moneybag)
	{
		public void onClicked(int playerID)
		{ }
		
		public String getTitleKey()
		{ return ID; }
	};
	
	// Self actions //
	
	public static final PlayerAction guide = new PlayerAction("ftbu:button.guide", GuiIcons.guide)
	{
		public void onClicked(int playerID)
		{
			FTBLibClient.playClickSound();
			GuiGuide.openClientGui();
		}
		
		public String getTitleKey()
		{ return ID; }
	};
	
	public static final PlayerAction info = new PlayerAction("ftbu:button.server_info", GuiIcons.guide_server)
	{
		public void onClicked(int playerID)
		{ ClientAction.REQUEST_SERVER_INFO.send(0); }
		
		public String getTitleKey()
		{ return ID; }
	};
	
	public static final PlayerAction claims = new PlayerAction("ftbu:button.claimed_chunks", GuiIcons.map)
	{
		public void onClicked(int playerID)
		{ FTBLibClient.mc.displayGuiScreen(new GuiClaimChunks(0L)); }
		
		public String getTitleKey()
		{ return ID; }
	};
	
	public static List<PlayerAction> getActionsFor(LMPlayerClient p)
	{
		LMPlayerClient o = LMWorldClient.inst.getClientPlayer();
		boolean self = o.equalsPlayer(p);

		ArrayList<PlayerAction> list = new ArrayList<>();
		
		if(self)
		{
			list.add(FTBLibGuiEventHandler.settings);
			list.add(FTBLibGuiEventHandler.notifications);
		}
		else
		{
			boolean isFriend = o.isFriendRaw(p);
			if(!isFriend) list.add(friend_add);
			
			if(FTBLibFinals.DEV)
			{
				//list.add(mail);
				//list.add(trade);
			}
			
			if(isFriend) list.add(friend_remove);
			else if(p.isFriendRaw(o)) list.add(friend_deny);
		}
		
		EventPlayerActionButtons event = new EventPlayerActionButtons(p.playerID, self, true);
		event.post();
		list.addAll(event.actions);
		list.remove(friends_gui);
		
		return list;
	}
}