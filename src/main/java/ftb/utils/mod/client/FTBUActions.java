package ftb.utils.mod.client;

import ftb.lib.FTBLib;
import ftb.lib.api.PlayerAction;
import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.gui.*;
import ftb.lib.api.players.ForgePlayer;
import ftb.lib.mod.client.gui.friends.GuiFriends;
import ftb.utils.mod.client.gui.claims.GuiClaimChunks;
import ftb.utils.mod.client.gui.guide.GuiGuide;
import ftb.utils.net.MessageRequestServerInfo;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.*;

public class FTBUActions
{
	@SideOnly(Side.CLIENT)
	public static void init()
	{
		PlayerActionRegistry.add(guide);
		PlayerActionRegistry.add(info);
		PlayerActionRegistry.add(claims);
		
		//PlayerActionRegistry.add(mail);
		//PlayerActionRegistry.add(trade);
		
		GuiScreenRegistry.register("friends_gui", new GuiScreenRegistry.Entry()
		{
			public GuiScreen openGui(EntityPlayer ep)
			{ return new GuiFriends(FTBLibClient.mc.currentScreen); }
		});
		
		GuiScreenRegistry.register("claimed_chunks", new GuiScreenRegistry.Entry()
		{
			public GuiScreen openGui(EntityPlayer ep)
			{ return new GuiClaimChunks(0L); }
		});
		
		GuiScreenRegistry.register("guide", new GuiScreenRegistry.Entry()
		{
			public GuiScreen openGui(EntityPlayer ep)
			{ return GuiGuide.openClientGui(false); }
		});
		
		GuiScreenRegistry.register("server_info", new GuiScreenRegistry.Entry()
		{
			public GuiScreen openGui(EntityPlayer ep)
			{
				new MessageRequestServerInfo().sendToServer();
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
	
	public static final PlayerAction guide = new PlayerAction(PlayerAction.Type.SELF, "ftbu.guide", 0, GuiIcons.book)
	{
		public void onClicked(ForgePlayer self, ForgePlayer other)
		{
			FTBLibClient.playClickSound();
			GuiGuide.openClientGui(true);
		}
		
		public boolean isVisibleFor(ForgePlayer self, ForgePlayer other)
		{ return FTBUClient.hasServerMod(); }
		
		public Boolean configDefault()
		{ return Boolean.TRUE; }
	};
	
	public static final PlayerAction info = new PlayerAction(PlayerAction.Type.SELF, "ftbu.server_info", 0, GuiIcons.book_red)
	{
		public void onClicked(ForgePlayer self, ForgePlayer other)
		{ new MessageRequestServerInfo().sendToServer(); }
		
		public boolean isVisibleFor(ForgePlayer self, ForgePlayer other)
		{ return FTBUClient.hasServerMod(); }
		
		public Boolean configDefault()
		{ return Boolean.TRUE; }
	};
	
	public static final PlayerAction claims = new PlayerAction(PlayerAction.Type.SELF, "ftbu.claimed_chunks", 0, GuiIcons.map)
	{
		public void onClicked(ForgePlayer self, ForgePlayer other)
		{ FTBLibClient.openGui(new GuiClaimChunks(0L)); }
		
		public boolean isVisibleFor(ForgePlayer self, ForgePlayer other)
		{ return FTBUClient.hasServerMod(); }
		
		public Boolean configDefault()
		{ return Boolean.TRUE; }
	};
	
	public static final PlayerAction trade = new PlayerAction(PlayerAction.Type.SELF, "ftbu.trade", 0, GuiIcons.money_bag)
	{
		public void onClicked(ForgePlayer owner, ForgePlayer player)
		{
		}
		
		public boolean isVisibleFor(ForgePlayer self, ForgePlayer other)
		{ return FTBLib.DEV_ENV; }
		
		public Boolean configDefault()
		{ return Boolean.TRUE; }
	};
	
	// Other //
	
	public static final PlayerAction mail = new PlayerAction(PlayerAction.Type.OTHER, "ftbu.mail", 0, GuiIcons.feather)
	{
		public void onClicked(ForgePlayer self, ForgePlayer other)
		{
		}
		
		public boolean isVisibleFor(ForgePlayer self, ForgePlayer other)
		{ return FTBLib.DEV_ENV; }
	};
}