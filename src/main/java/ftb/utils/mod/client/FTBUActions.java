package ftb.utils.mod.client;

import com.google.gson.JsonPrimitive;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.FTBLib;
import ftb.lib.PrivacyLevel;
import ftb.lib.TextureCoords;
import ftb.lib.api.PlayerAction;
import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.friends.ILMPlayer;
import ftb.lib.api.gui.GuiIcons;
import ftb.lib.api.gui.GuiScreenRegistry;
import ftb.lib.api.gui.PlayerActionRegistry;
import ftb.lib.api.info.InfoPage;
import ftb.lib.api.info.lines.InfoExtendedTextLine;
import ftb.lib.api.notification.ClickAction;
import ftb.lib.api.notification.ClickActionType;
import ftb.lib.mod.client.gui.info.GuiInfo;
import ftb.utils.api.guide.ClientGuideFile;
import ftb.utils.mod.client.gui.claims.GuiClaimChunks;
import ftb.utils.mod.client.gui.friends.InfoFriendsGUI;
import ftb.utils.net.ClientAction;
import ftb.utils.world.LMWorldClient;
import ftb.utils.world.PersonalSettings;
import latmod.lib.LMColor;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class FTBUActions
{
	public static void init()
	{
		PlayerActionRegistry.add(friends_gui);
		PlayerActionRegistry.add(guide);
		PlayerActionRegistry.add(info);
		PlayerActionRegistry.add(claims);
		PlayerActionRegistry.add(my_server_settings);
		
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
			@Override
			public GuiScreen openGui(EntityPlayer ep)
			{ return new GuiInfo(null, new InfoFriendsGUI()); }
		});
		
		GuiScreenRegistry.register("claimed_chunks", new GuiScreenRegistry.Entry()
		{
			@Override
			public GuiScreen openGui(EntityPlayer ep)
			{ return new GuiClaimChunks(0L); }
		});
		
		GuiScreenRegistry.register("guide", new GuiScreenRegistry.Entry()
		{
			@Override
			public GuiScreen openGui(EntityPlayer ep)
			{ return ClientGuideFile.openClientGui(false); }
		});
		
		GuiScreenRegistry.register("server_info", new GuiScreenRegistry.Entry()
		{
			@Override
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
		@Override
		public void onClicked(ILMPlayer self, ILMPlayer other)
		{ FTBLibClient.openGui(new GuiInfo(null, new InfoFriendsGUI())); }
		
		@Override
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return FTBLibClient.isIngameWithFTBU(); }
		
		@Override
		public String getDisplayName()
		{ return "FriendsGUI"; }
	};
	
	public static final PlayerAction guide = new PlayerAction(PlayerAction.Type.SELF, "ftbu.guide", 0, GuiIcons.book)
	{
		@Override
		public void onClicked(ILMPlayer self, ILMPlayer other)
		{
			FTBLibClient.playClickSound();
			ClientGuideFile.openClientGui(true);
		}
		
		@Override
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return FTBLibClient.isIngameWithFTBU(); }
		
		@Override
		public Boolean configDefault()
		{ return Boolean.TRUE; }
	};
	
	public static final PlayerAction info = new PlayerAction(PlayerAction.Type.SELF, "ftbu.server_info", 0, GuiIcons.book_red)
	{
		@Override
		public void onClicked(ILMPlayer self, ILMPlayer other)
		{ ClientAction.REQUEST_SERVER_INFO.send(0); }
		
		@Override
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return FTBLibClient.isIngameWithFTBU(); }
		
		@Override
		public Boolean configDefault()
		{ return Boolean.TRUE; }
	};
	
	public static final PlayerAction claims = new PlayerAction(PlayerAction.Type.SELF, "ftbu.claimed_chunks", 0, GuiIcons.map)
	{
		@Override
		public void onClicked(ILMPlayer self, ILMPlayer other)
		{ FTBLibClient.openGui(new GuiClaimChunks(0L)); }
		
		@Override
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return FTBLibClient.isIngameWithFTBU(); }
		
		@Override
		public Boolean configDefault()
		{ return Boolean.TRUE; }
	};
	
	public static final PlayerAction trade = new PlayerAction(PlayerAction.Type.SELF, "ftbu.trade", 0, GuiIcons.money_bag)
	{
		@Override
		public void onClicked(ILMPlayer owner, ILMPlayer player)
		{
		}
		
		@Override
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return FTBLib.DEV_ENV; }
		
		@Override
		public Boolean configDefault()
		{ return Boolean.TRUE; }
	};
	
	public static final PlayerAction my_server_settings = new PlayerAction(PlayerAction.Type.SELF, "ftbu.my_server_settings", -1000, GuiIcons.settings)
	{
		@Override
		public void onClicked(ILMPlayer self, ILMPlayer other)
		{
			InfoPage page = new InfoPage("my_server_settings")
			{
				@Override
				public void refreshGui(GuiInfo gui)
				{
					clear();
					PersonalSettings ps = LMWorldClient.inst.clientPlayer.getSettings();
					
					booleanCommand("chat_links", ps.get(PersonalSettings.CHAT_LINKS));
					booleanCommand("render_badge", LMWorldClient.inst.clientPlayer.renderBadge);
					booleanCommand("explosions", ps.get(PersonalSettings.EXPLOSIONS));
					booleanCommand("fake_players", ps.get(PersonalSettings.FAKE_PLAYERS));
					
					IChatComponent text1 = ps.blocks.lang.chatComponent();
					text1.getChatStyle().setColor(ps.blocks == PrivacyLevel.FRIENDS ? EnumChatFormatting.BLUE : (ps.blocks == PrivacyLevel.PUBLIC ? EnumChatFormatting.GREEN : EnumChatFormatting.RED));
					InfoExtendedTextLine line = new InfoExtendedTextLine(this, new ChatComponentTranslation("ftbu.player_setting.security_level").appendText(": ").appendSibling(text1));
					line.setClickAction(new ClickAction(ClickActionType.CMD, new JsonPrimitive("lmplayer_settings block_security toggle")));
					text.add(line);
				}
				
				private void booleanCommand(String s, boolean current)
				{
					ChatComponentText text1 = new ChatComponentText(Boolean.toString(current));
					text1.getChatStyle().setColor(current ? EnumChatFormatting.GREEN : EnumChatFormatting.RED);
					InfoExtendedTextLine line = new InfoExtendedTextLine(this, new ChatComponentTranslation("ftbu.player_setting." + s).appendText(": ").appendSibling(text1));
					line.setClickAction(new ClickAction(ClickActionType.CMD, new JsonPrimitive("lmplayer_settings " + s + " toggle")));
					text.add(line);
				}
			};
			
			page.setTitle(new ChatComponentTranslation("player_action.ftbu.my_server_settings"));
			page.backgroundColor = new LMColor.RGB(30, 30, 30);
			page.textColor = new LMColor.RGB(200, 200, 200);
			page.useUnicodeFont = Boolean.FALSE;
			FTBLibClient.openGui(new GuiInfo(null, page));
		}
		
		@Override
		public Boolean configDefault()
		{ return Boolean.FALSE; }
	};
	
	// Other //
	
	public static final PlayerAction friend_add = new PlayerAction(PlayerAction.Type.OTHER, "ftbu.add_friend", 1, GuiIcons.add)
	{
		@Override
		public void onClicked(ILMPlayer self, ILMPlayer other)
		{ ClientAction.ADD_FRIEND.send(other.getPlayerID()); }
		
		@Override
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return FTBLibClient.isIngameWithFTBU() && !self.isFriendRaw(other); }
	};
	
	public static final PlayerAction friend_remove = new PlayerAction(PlayerAction.Type.OTHER, "ftbu.rem_friend", -1, GuiIcons.remove)
	{
		@Override
		public void onClicked(ILMPlayer self, ILMPlayer other)
		{ ClientAction.REM_FRIEND.send(other.getPlayerID()); }
		
		@Override
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return FTBLibClient.isIngameWithFTBU() && self.isFriendRaw(other); }
	};
	
	public static final PlayerAction friend_deny = new PlayerAction(PlayerAction.Type.OTHER, "ftbu.deny_friend", -1, GuiIcons.remove)
	{
		@Override
		public void onClicked(ILMPlayer self, ILMPlayer other)
		{ ClientAction.DENY_FRIEND.send(other.getPlayerID()); }
		
		@Override
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return FTBLibClient.isIngameWithFTBU() && !self.isFriendRaw(other) && other.isFriendRaw(self); }
	};
	
	public static final PlayerAction mail = new PlayerAction(PlayerAction.Type.OTHER, "ftbu.mail", 0, GuiIcons.feather)
	{
		@Override
		public void onClicked(ILMPlayer self, ILMPlayer other)
		{
		}
		
		@Override
		public boolean isVisibleFor(ILMPlayer self, ILMPlayer other)
		{ return FTBLib.DEV_ENV; }
	};
}