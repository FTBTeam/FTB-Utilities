package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbl.api.EnumSelf;
import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.PlayerAction;
import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.gui.GuiIcons;
import com.feed_the_beast.ftbl.api.gui.GuiScreenRegistry;
import com.feed_the_beast.ftbl.api.gui.PlayerActionRegistry;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbu.api.guide.ClientGuideFile;
import com.feed_the_beast.ftbu.client.gui.claims.GuiClaimChunks;
import com.feed_the_beast.ftbu.net.MessageRequestServerInfo;
import com.feed_the_beast.ftbu.world.FTBUWorldDataSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
	
	public static final PlayerAction guide = new PlayerAction(EnumSelf.SELF, "ftbu.guide", 0, GuiIcons.book)
	{
		@Override
		public void onClicked(ForgePlayer self, ForgePlayer other)
		{
			FTBLibClient.playClickSound();
			ClientGuideFile.openClientGui(true);
		}
		
		@Override
		public boolean isVisibleFor(ForgePlayer self, ForgePlayer other)
		{ return FTBUWorldDataSP.get().isLoaded(); }
		
		@Override
		public Boolean configDefault()
		{ return Boolean.TRUE; }
	};
	
	public static final PlayerAction info = new PlayerAction(EnumSelf.SELF, "ftbu.server_info", 0, GuiIcons.book_red)
	{
		@Override
		public void onClicked(ForgePlayer self, ForgePlayer other)
		{ new MessageRequestServerInfo().sendToServer(); }
		
		@Override
		public boolean isVisibleFor(ForgePlayer self, ForgePlayer other)
		{ return FTBUWorldDataSP.get().isLoaded(); }
		
		@Override
		public Boolean configDefault()
		{ return Boolean.TRUE; }
	};
	
	public static final PlayerAction claims = new PlayerAction(EnumSelf.SELF, "ftbu.claimed_chunks", 0, GuiIcons.map)
	{
		@Override
		public void onClicked(ForgePlayer self, ForgePlayer other)
		{ FTBLibClient.openGui(new GuiClaimChunks(0L)); }
		
		@Override
		public boolean isVisibleFor(ForgePlayer self, ForgePlayer other)
		{ return FTBUWorldDataSP.get().isLoaded(); }
		
		@Override
		public Boolean configDefault()
		{ return Boolean.TRUE; }
	};
	
	public static final PlayerAction trade = new PlayerAction(EnumSelf.SELF, "ftbu.trade", 0, GuiIcons.money_bag)
	{
		@Override
		public void onClicked(ForgePlayer owner, ForgePlayer player)
		{
		}
		
		@Override
		public boolean isVisibleFor(ForgePlayer self, ForgePlayer other)
		{ return FTBLib.DEV_ENV; }
		
		@Override
		public Boolean configDefault()
		{ return Boolean.TRUE; }
	};
	
	// Other //
	
	public static final PlayerAction mail = new PlayerAction(EnumSelf.OTHER, "ftbu.mail", 0, GuiIcons.feather)
	{
		@Override
		public void onClicked(ForgePlayer self, ForgePlayer other)
		{
		}
		
		@Override
		public boolean isVisibleFor(ForgePlayer self, ForgePlayer other)
		{ return FTBLib.DEV_ENV; }
	};
}