package ftb.utils.mod.client;

import cpw.mods.fml.relauncher.*;
import ftb.lib.*;
import ftb.lib.api.PlayerAction;
import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.friends.ILMPlayer;
import ftb.lib.api.gui.*;
import ftb.lib.mod.client.gui.info.GuiInfo;
import ftb.utils.api.guide.ClientGuideFile;
import ftb.utils.mod.client.gui.claims.GuiClaimChunks;
import ftb.utils.mod.client.gui.friends.InfoFriendsGUI;
import ftb.utils.net.ClientAction;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class FTBUActions
{
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
		
		if(FTBLib.DEV_ENV)
		{
			PlayerActionRegistry.add(mail);
			PlayerActionRegistry.add(trade);
		}
		
		GuiScreenRegistry.register("friends_gui", new GuiScreenRegistry.Entry()
		{
			public GuiScreen openGui(EntityPlayer ep)
			{ return new GuiInfo(null, new InfoFriendsGUI()); }
		});
		
		GuiScreenRegistry.register("claimed_chunks", new GuiScreenRegistry.Entry()
		{
			public GuiScreen openGui(EntityPlayer ep)
			{ return new GuiClaimChunks(0L); }
		});
		
		GuiScreenRegistry.register("guide", new GuiScreenRegistry.Entry()
		{
			public GuiScreen openGui(EntityPlayer ep)
			{ return ClientGuideFile.openClientGui(false); }
		});
		
		GuiScreenRegistry.register("server_info", new GuiScreenRegistry.Entry()
		{
			public GuiScreen openGui(EntityPlayer ep)
			{
				ClientAction.REQUEST_SERVER_INFO.send(0);
				return null;
			}
		});
		
		/*
		GuiScreenRegistry.register("trade", new GuiScreenRegistry.Entry()
		{
			public GuiScreen openGui(EntityPlayer ep)
			{
				return FTBLibClient.mc.currentScreen;
			}
		});
		*/
	}
	
	// Self //
	
	public static final PlayerAction friends_gui = new PlayerAction(PlayerAction.Type.SELF, "ftbu.friends_gui", 950, TextureCoords.getSquareIcon(new ResourceLocation("ftbu", "textures/gui/friendsbutton.png"), 256))
	{
		public void onClicked(ILMPlayer self, ILMPlayer other)
		{ FTBLibClient.openGui(new GuiInfo(null, new InfoFriendsGUI())); }
		
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return FTBLibClient.isIngameWithFTBU(); }
		
		public String getDisplayName()
		{ return "FriendsGUI"; }
	};
	
	public static final PlayerAction guide = new PlayerAction(PlayerAction.Type.SELF, "ftbu.guide", 0, GuiIcons.book)
	{
		public void onClicked(ILMPlayer self, ILMPlayer other)
		{
			FTBLibClient.playClickSound();
			ClientGuideFile.openClientGui(true);
		}
		
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return FTBLibClient.isIngameWithFTBU(); }
		
		public Boolean configDefault()
		{ return Boolean.TRUE; }
	};
	
	public static final PlayerAction info = new PlayerAction(PlayerAction.Type.SELF, "ftbu.server_info", 0, GuiIcons.book_red)
	{
		public void onClicked(ILMPlayer self, ILMPlayer other)
		{ ClientAction.REQUEST_SERVER_INFO.send(0); }
		
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return FTBLibClient.isIngameWithFTBU(); }
		
		public Boolean configDefault()
		{ return Boolean.TRUE; }
	};
	
	public static final PlayerAction claims = new PlayerAction(PlayerAction.Type.SELF, "ftbu.claimed_chunks", 0, GuiIcons.map)
	{
		public void onClicked(ILMPlayer self, ILMPlayer other)
		{ FTBLibClient.openGui(new GuiClaimChunks(0L)); }
		
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return FTBLibClient.isIngameWithFTBU(); }
		
		public Boolean configDefault()
		{ return Boolean.TRUE; }
	};
	
	public static final PlayerAction trade = new PlayerAction(PlayerAction.Type.SELF, "ftbu.trade", 0, GuiIcons.money_bag)
	{
		public void onClicked(ILMPlayer owner, ILMPlayer player)
		{
		}
		
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return FTBLib.DEV_ENV; }
		
		public Boolean configDefault()
		{ return Boolean.TRUE; }
	};
	
	// Other //
	
	public static final PlayerAction friend_add = new PlayerAction(PlayerAction.Type.OTHER, "ftbu.add_friend", 1, GuiIcons.add)
	{
		public void onClicked(ILMPlayer self, ILMPlayer other)
		{ ClientAction.ADD_FRIEND.send(other.getPlayerID()); }
		
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return FTBLibClient.isIngameWithFTBU() && !self.isFriendRaw(other); }
	};
	
	public static final PlayerAction friend_remove = new PlayerAction(PlayerAction.Type.OTHER, "ftbu.rem_friend", -1, GuiIcons.remove)
	{
		public void onClicked(ILMPlayer self, ILMPlayer other)
		{ ClientAction.REM_FRIEND.send(other.getPlayerID()); }
		
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return FTBLibClient.isIngameWithFTBU() && self.isFriendRaw(other); }
	};
	
	public static final PlayerAction friend_deny = new PlayerAction(PlayerAction.Type.OTHER, "ftbu.deny_friend", -1, GuiIcons.remove)
	{
		public void onClicked(ILMPlayer self, ILMPlayer other)
		{ ClientAction.DENY_FRIEND.send(other.getPlayerID()); }
		
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return FTBLibClient.isIngameWithFTBU() && !self.isFriendRaw(other) && other.isFriendRaw(self); }
	};
	
	public static final PlayerAction mail = new PlayerAction(PlayerAction.Type.OTHER, "ftbu.mail", 0, GuiIcons.feather)
	{
		public void onClicked(ILMPlayer self, ILMPlayer other)
		{
		}
		
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return FTBLib.DEV_ENV; }
	};
}