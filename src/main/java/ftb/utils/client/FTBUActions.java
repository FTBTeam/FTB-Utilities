package ftb.utils.client;

import ftb.lib.FTBLib;
import ftb.lib.api.ForgePlayer;
import ftb.lib.api.PlayerAction;
import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.gui.GuiIcons;
import ftb.lib.api.gui.GuiScreenRegistry;
import ftb.lib.api.gui.PlayerActionRegistry;
import ftb.utils.api.guide.ClientGuideFile;
import ftb.utils.client.gui.claims.GuiClaimChunks;
import ftb.utils.net.MessageRequestServerInfo;
import ftb.utils.world.FTBUWorldDataSP;
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
	
	public static final PlayerAction guide = new PlayerAction(PlayerAction.Type.SELF, "ftbu.guide", 0, GuiIcons.book)
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
	
	public static final PlayerAction info = new PlayerAction(PlayerAction.Type.SELF, "ftbu.server_info", 0, GuiIcons.book_red)
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
	
	public static final PlayerAction claims = new PlayerAction(PlayerAction.Type.SELF, "ftbu.claimed_chunks", 0, GuiIcons.map)
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
	
	public static final PlayerAction trade = new PlayerAction(PlayerAction.Type.SELF, "ftbu.trade", 0, GuiIcons.money_bag)
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
	
	public static final PlayerAction mail = new PlayerAction(PlayerAction.Type.OTHER, "ftbu.mail", 0, GuiIcons.feather)
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