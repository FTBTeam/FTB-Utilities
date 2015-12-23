package latmod.ftbu.mod.client;

import ftb.lib.api.*;
import ftb.lib.api.gui.GuiIcons;
import ftb.lib.client.*;
import ftb.lib.mod.FTBLibFinals;
import ftb.lib.mod.client.FTBLibGuiEventHandler;
import latmod.ftbu.api.client.FTBULang;
import latmod.ftbu.api.guide.ClientGuideFile;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.gui.claims.GuiClaimChunks;
import latmod.ftbu.mod.client.gui.friends.GuiFriends;
import latmod.ftbu.mod.client.gui.guide.GuiGuide;
import latmod.ftbu.net.ClientAction;
import latmod.ftbu.world.*;
import latmod.lib.FastList;

public class FTBUActions
{
	// FriendsGUI //
	
	public static final PlayerAction friends_gui = new PlayerAction(TextureCoords.getSquareIcon(FTBU.mod.getLocation("textures/gui/friendsbutton.png"), 256))
	{
		public void onClicked(int playerID)
		{ FTBLibClient.mc.displayGuiScreen(new GuiFriends(FTBLibClient.mc.currentScreen)); }
		
		public String getTitle()
		{ return "FriendsGUI"; }
	};
	
	// Other players //
	
	public static final PlayerAction friend_add = new PlayerAction(GuiIcons.add)
	{
		public void onClicked(int playerID)
		{ ClientAction.ACTION_ADD_FRIEND.send(playerID); }
		
		public String getTitle()
		{ return FTBULang.button_add_friend(); }
	};
	
	public static final PlayerAction friend_remove = new PlayerAction(GuiIcons.remove)
	{
		public void onClicked(int playerID)
		{ ClientAction.ACTION_REM_FRIEND.send(playerID); }
		
		public String getTitle()
		{ return FTBULang.button_rem_friend(); }
	};
	
	public static final PlayerAction friend_deny = new PlayerAction(GuiIcons.remove)
	{
		public void onClicked(int playerID)
		{ ClientAction.ACTION_DENY_FRIEND.send(playerID); }
		
		public String getTitle()
		{ return FTBULang.button_deny_friend(); }
	};
	
	public static final PlayerAction mail = new PlayerAction(GuiIcons.feather)
	{
		public void onClicked(int playerID)
		{  }
		
		public String getTitle()
		{ return FTBULang.mail(); }
	};
	
	public static final PlayerAction trade = new PlayerAction(GuiIcons.moneybag)
	{
		public void onClicked(int playerID)
		{  }
		
		public String getTitle()
		{ return FTBULang.trade(); }
	};
	
	// Self actions //
	
	public static final PlayerAction guide = new PlayerAction(GuiIcons.guide)
	{
		public void onClicked(int playerID)
		{
			FTBLibClient.playClickSound();
			GuiGuide.openClientGui();
		}
		
		public String getTitle()
		{ return ClientGuideFile.instance.main.getTitleComponent().getFormattedText(); }
	};
	
	public static final PlayerAction info = new PlayerAction(GuiIcons.guide_server)
	{
		public void onClicked(int playerID)
		{ ClientAction.ACTION_REQUEST_SERVER_INFO.send(0); }
		
		public String getTitle()
		{ return FTBULang.button_server_info(); }
	};
	
	public static final PlayerAction claims = new PlayerAction(GuiIcons.map)
	{
		public void onClicked(int playerID)
		{ FTBLibClient.mc.displayGuiScreen(new GuiClaimChunks(0L)); }
		
		public String getTitle()
		{ return FTBULang.claimed_chunks(); }
	};
	
	public static final PlayerAction slack = new PlayerAction(TextureCoords.getSquareIcon(FTBU.mod.getLocation("textures/gui/slack.png"), 32))
	{
		public void onClicked(int playerID)
		{ /* TODO: Slack */ }
		
		public String getTitle()
		{ return "Slack"; }
	};
	
	public static final PlayerAction trello = new PlayerAction(TextureCoords.getSquareIcon(FTBU.mod.getLocation("textures/gui/trello.png"), 32))
	{
		public void onClicked(int playerID)
		{ /* TODO: Trello */ }
		
		public String getTitle()
		{ return "Trello"; }
	};
	
	public static FastList<PlayerAction> getActionsFor(LMPlayerClient p)
	{
		LMPlayerClient o = LMWorldClient.inst.getClientPlayer();
		boolean self = o.equalsPlayer(p);
		
		FastList<PlayerAction> list = new FastList<PlayerAction>();
		
		if(self)
		{
			list.add(FTBLibGuiEventHandler.settings);
			list.add(FTBLibGuiEventHandler.notifications);
		}
		
		if(self)
		{
			if(FTBLibFinals.DEV)
			{
				//list.add(PlayerSelfAction.mail);
				list.add(slack);
				list.add(trello);
			}
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
			else if(p.isFriendRaw(o))
				list.add(friend_deny);
		}
		
		EventPlayerActionButtons event = new EventPlayerActionButtons(p.playerID, self, true);
		event.post();
		list.addAll(event.actions);
		list.remove(friends_gui);
		
		return list;
	}
}