package ftb.utils.api.guide;

import cpw.mods.fml.relauncher.*;
import ftb.lib.FTBWorld;
import ftb.lib.api.*;
import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.gui.GuiIcons;
import ftb.lib.api.gui.widgets.ButtonLM;
import ftb.lib.api.info.InfoPage;
import ftb.lib.mod.client.gui.info.GuiInfo;
import ftb.utils.api.guide.repos.*;
import ftb.utils.mod.client.gui.guide.ReposPage;
import net.minecraft.util.ChatComponentTranslation;

import java.io.File;

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
	
	public void reload()
	{
		clear();
		
		InfoPage depPage = getSub("dep_guide").setTitle(new ChatComponentTranslation("player_action.ftbu.guide"));
		
		File file = GameModes.getGameModes().commonMode.getFile("guide/");
		if(file.exists() && file.isDirectory())
		{
			ServerInfoFile.loadFromFiles(depPage, file);
		}
		
		GameMode mode = FTBWorld.client.getMode();
		
		file = mode.getFile("guide/");
		if(file.exists() && file.isDirectory())
		{
			ServerInfoFile.loadFromFiles(depPage, file);
		}
		
		file = mode.getFile("guide_intro.txt");
		if(file.exists() && file.isFile())
		{
			ServerInfoFile.loadFromFiles(depPage, file);
		}
		
		GuideRepoList.refreshLocalRepos();
		for(GuideLocalRepo r : GuideRepoList.localRepos.values())
		{
			addSub(r.getInfoPage(mode.getID()));
		}
		
		cleanup();
		clientGuideGui = null;
	}
}