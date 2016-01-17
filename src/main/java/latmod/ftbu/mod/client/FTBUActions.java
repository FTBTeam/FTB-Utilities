package latmod.ftbu.mod.client;

import ftb.lib.*;
import ftb.lib.api.PlayerAction;
import ftb.lib.api.friends.ILMPlayer;
import ftb.lib.api.gui.*;
import ftb.lib.mod.client.FTBLibActions;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.gui.claims.GuiClaimChunks;
import latmod.ftbu.mod.client.gui.friends.GuiFriends;
import latmod.ftbu.mod.client.gui.guide.GuiGuide;
import latmod.ftbu.net.ClientAction;
import latmod.lib.config.ConfigEntryBool;
import net.minecraftforge.fml.relauncher.*;

public class FTBUActions
{
	private static final ConfigEntryBool button_guide = new ConfigEntryBool("guide", true);
	private static final ConfigEntryBool button_info = new ConfigEntryBool("info", true);
	private static final ConfigEntryBool button_claims = new ConfigEntryBool("claims", true);
	
	@SideOnly(Side.CLIENT)
	public static void init()
	{
		PlayerActionRegistry.add(friends_gui);
		PlayerActionRegistry.add(guide);
		PlayerActionRegistry.add(info);
		PlayerActionRegistry.add(claims);
		
		PlayerActionRegistry.add(friend_add);
		PlayerActionRegistry.add(friend_remove);
		PlayerActionRegistry.add(friend_deny);
		PlayerActionRegistry.add(mail);
		PlayerActionRegistry.add(trade);
		
		FTBLibActions.sidebar_buttons_config.addAll(FTBUActions.class, null, false);
	}
	
	// Self //
	
	public static final PlayerAction friends_gui = new PlayerAction(PlayerAction.Type.SELF, "ftbu:button.friends_gui", 950, TextureCoords.getSquareIcon(FTBU.mod.getLocation("textures/gui/friendsbutton.png"), 256))
	{
		public void onClicked(ILMPlayer self, ILMPlayer other)
		{ FTBLibClient.mc.displayGuiScreen(new GuiFriends(FTBLibClient.mc.currentScreen)); }
		
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return FTBLibClient.isIngameWithFTBU(); }
		
		public String getDisplayName()
		{ return "FriendsGUI"; }
	};
	
	public static final PlayerAction guide = new PlayerAction(PlayerAction.Type.SELF, "ftbu:button.guide", 0, GuiIcons.book)
	{
		public void onClicked(ILMPlayer self, ILMPlayer other)
		{
			FTBLibClient.playClickSound();
			GuiGuide.openClientGui();
		}
		
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return FTBLibClient.isIngameWithFTBU() && button_guide.get(); }
	};
	
	public static final PlayerAction info = new PlayerAction(PlayerAction.Type.SELF, "ftbu:button.server_info", 0, GuiIcons.book_red)
	{
		public void onClicked(ILMPlayer self, ILMPlayer other)
		{ ClientAction.REQUEST_SERVER_INFO.send(0); }
		
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return FTBLibClient.isIngameWithFTBU() && button_info.get(); }
	};
	
	public static final PlayerAction claims = new PlayerAction(PlayerAction.Type.SELF, "ftbu:button.claimed_chunks", 0, GuiIcons.map)
	{
		public void onClicked(ILMPlayer self, ILMPlayer other)
		{ FTBLibClient.mc.displayGuiScreen(new GuiClaimChunks(0L)); }
		
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return FTBLibClient.isIngameWithFTBU() && button_claims.get(); }
	};
	
	// Other //
	
	public static final PlayerAction friend_add = new PlayerAction(PlayerAction.Type.OTHER, "ftbu:button.add_friend", 1, GuiIcons.add)
	{
		public void onClicked(ILMPlayer self, ILMPlayer other)
		{ ClientAction.ADD_FRIEND.send(other.getPlayerID()); }
		
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return !self.isFriendRaw(other); }
	};
	
	public static final PlayerAction friend_remove = new PlayerAction(PlayerAction.Type.OTHER, "ftbu:button.rem_friend", -1, GuiIcons.remove)
	{
		public void onClicked(ILMPlayer self, ILMPlayer other)
		{ ClientAction.REM_FRIEND.send(other.getPlayerID()); }
		
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return self.isFriendRaw(other); }
	};
	
	public static final PlayerAction friend_deny = new PlayerAction(PlayerAction.Type.OTHER, "ftbu:button.deny_friend", -1, GuiIcons.remove)
	{
		public void onClicked(ILMPlayer self, ILMPlayer other)
		{ ClientAction.DENY_FRIEND.send(other.getPlayerID()); }
		
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return !self.isFriendRaw(other) && other.isFriendRaw(self); }
	};
	
	public static final PlayerAction mail = new PlayerAction(PlayerAction.Type.OTHER, "ftbu:button.mail", 0, GuiIcons.feather)
	{
		public void onClicked(ILMPlayer self, ILMPlayer other)
		{
		}
		
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return DevConsole.enabled(); }
	};
	
	public static final PlayerAction trade = new PlayerAction(PlayerAction.Type.OTHER, "ftbu:button.trade", 0, GuiIcons.money_bag)
	{
		public void onClicked(ILMPlayer owner, ILMPlayer player)
		{
		}
		
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return DevConsole.enabled(); }
	};
}