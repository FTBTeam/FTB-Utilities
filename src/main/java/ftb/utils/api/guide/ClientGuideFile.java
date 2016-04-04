package ftb.utils.api.guide;

import cpw.mods.fml.relauncher.*;
import ftb.lib.FTBLib;
import ftb.lib.api.*;
import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.gui.GuiIcons;
import ftb.lib.api.gui.widgets.ButtonLM;
import ftb.lib.api.info.InfoPage;
import ftb.lib.mod.client.gui.info.GuiInfo;
import ftb.utils.mod.FTBU;
import ftb.utils.mod.client.gui.guide.ReposPage;
import latmod.lib.LMFileUtils;
import net.minecraft.util.ChatComponentTranslation;

import java.io.File;
import java.util.Arrays;

public class ClientGuideFile extends InfoPage
{
	public static final ClientGuideFile instance = new ClientGuideFile("client_config");
	
	public static GuiInfo clientGuideGui = null;
	
	public static GuiInfo openClientGui(boolean open)
	{
		if(clientGuideGui == null) clientGuideGui = new GuiInfo(null, ClientGuideFile.instance);
		if(open) FTBLibClient.openGui(clientGuideGui);
		return clientGuideGui;
	}
	
	public ClientGuideFile(String id)
	{
		super(id);
		setTitle(new ChatComponentTranslation("player_action.ftbu.guide"));
	}
	
	@SideOnly(Side.CLIENT)
	public ButtonLM createSpecialButton(GuiInfo gui)
	{
		ButtonLM button = new ButtonLM(gui, 0, 0, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				FTBLibClient.openGui(new GuiInfo(null, new ReposPage()));
			}
			
			public void renderWidget()
			{
				render(GuiIcons.globe);
			}
		};
		
		button.title = "Manage Guides";
		return button;
	}
	
	public void reload(EventFTBReload e)
	{
		if(FTBLib.DEV_ENV) FTBU.logger.info("Guide reloaded @ " + e.world.side + " as " + e.world.getMode());
		
		clear();
		
		File file = GameModes.getGameModes().commonMode.getFile("guide/");
		if(file.exists() && file.isDirectory())
		{
			File[] f = file.listFiles();
			if(f != null && f.length > 0)
			{
				Arrays.sort(f, LMFileUtils.fileComparator);
				for(int i = 0; i < f.length; i++)
					loadFromFiles(this, f[i]);
			}
		}
		
		file = e.world.getMode().getFile("guide/");
		if(file.exists() && file.isDirectory())
		{
			File[] f = file.listFiles();
			if(f != null && f.length > 0)
			{
				Arrays.sort(f, LMFileUtils.fileComparator);
				for(int i = 0; i < f.length; i++)
					loadFromFiles(this, f[i]);
			}
		}
		
		file = e.world.getMode().getFile("guide_intro.txt");
		if(file.exists() && file.isFile())
		{
			try
			{
				String text = LMFileUtils.loadAsText(file);
				if(text != null && !text.isEmpty()) printlnText(text.replace("\r", ""));
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		new EventFTBUClientGuide(this).post();
		
		cleanup();
		clientGuideGui = null;
	}
}