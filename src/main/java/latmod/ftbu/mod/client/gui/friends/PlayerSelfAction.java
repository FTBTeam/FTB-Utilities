package latmod.ftbu.mod.client.gui.friends;

import ftb.lib.api.gui.GuiIcons;
import ftb.lib.client.*;
import latmod.ftbu.api.guide.ClientGuideFile;
import latmod.ftbu.mod.client.FTBUGuiEventHandler;
import latmod.ftbu.mod.client.gui.GuiClientConfig;
import latmod.ftbu.mod.client.gui.guide.GuiGuide;
import latmod.ftbu.mod.client.gui.minimap.GuiMinimap;
import latmod.ftbu.net.ClientAction;
import latmod.ftbu.util.client.FTBULang;
import latmod.ftbu.world.LMPlayerClient;

public abstract class PlayerSelfAction extends PlayerAction
{
	public final int ID;
	
	public PlayerSelfAction(TextureCoords tex)
	{
		super(tex);
		ID = FTBUGuiEventHandler.getNextButtonID();
	}
	
	public int hashCode()
	{ return ID; }
	
	public boolean equals(Object o)
	{ return o == this || o.hashCode() == hashCode(); }
	
	// Static //
	
	public static final PlayerSelfAction settings = new PlayerSelfAction(GuiIcons.settings)
	{
		public void onClicked(LMPlayerClient p)
		{ FTBLibClient.mc.displayGuiScreen(new GuiClientConfig(FTBLibClient.mc.currentScreen)); }
		
		public String getTitle()
		{ return FTBULang.client_config(); }
	};
	
	public static final PlayerSelfAction guide = new PlayerSelfAction(GuiIcons.guide)
	{
		public void onClicked(LMPlayerClient p)
		{
			GuiGuide g = new GuiGuide(null, ClientGuideFile.instance.main);
			g.playClickSound();
			FTBLibClient.mc.displayGuiScreen(g);
		}
		
		public String getTitle()
		{ return ClientGuideFile.instance.main.getTitleComponent().getFormattedText(); }
	};
	
	public static final PlayerSelfAction info = new PlayerSelfAction(GuiIcons.guide_server)
	{
		public void onClicked(LMPlayerClient p)
		{ ClientAction.ACTION_REQUEST_SERVER_INFO.send(0); }
		
		public String getTitle()
		{ return FTBULang.Guis.button_server_info(); }
	};
	
	public static final PlayerSelfAction claims = new PlayerSelfAction(GuiIcons.map)
	{
		public void onClicked(LMPlayerClient p)
		{ FTBLibClient.mc.displayGuiScreen(new GuiMinimap()); }
		
		public String getTitle()
		{ return FTBULang.Guis.claimed_chunks(); }
	};
	
	public static final PlayerSelfAction notes = new PlayerSelfAction(GuiIcons.notes)
	{
		public void onClicked(LMPlayerClient p)
		{  }
		
		public String getTitle()
		{ return FTBULang.Guis.notes(); }
	};
}