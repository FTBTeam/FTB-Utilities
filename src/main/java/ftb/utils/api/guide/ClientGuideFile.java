package ftb.utils.api.guide;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.FTBLib;
import ftb.lib.FTBWorld;
import ftb.lib.api.GameMode;
import ftb.lib.api.GameModes;
import ftb.lib.api.MouseButton;
import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.gui.GuiIcons;
import ftb.lib.api.gui.widgets.ButtonLM;
import ftb.lib.api.info.InfoPage;
import ftb.lib.mod.client.gui.info.GuiInfo;
import ftb.utils.api.guide.repos.GuideLocalRepo;
import ftb.utils.mod.FTBU;
import ftb.utils.mod.client.gui.guide.ReposPage;
import ftb.utils.mod.config.FTBUConfigGeneral;
import latmod.lib.LMUtils;
import net.minecraft.util.ChatComponentTranslation;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ClientGuideFile extends InfoPage
{
	public static final ClientGuideFile instance = new ClientGuideFile("client_config");
	public final Map<String, GuideLocalRepo> localRepos;
	
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
		localRepos = new HashMap<>();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ButtonLM createSpecialButton(GuiInfo gui)
	{
		ButtonLM button = new ButtonLM(gui, 0, 0, 16, 16)
		{
			@Override
			public void onClicked(MouseButton button)
			{
				FTBLibClient.openGui(new GuiInfo(null, new ReposPage()));
			}
			
			@Override
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
		
		localRepos.clear();
		long ms = LMUtils.millis();
		
		try
		{
			FTBU.logger.info("Reloading guides from " + FTBUConfigGeneral.guidepacksFolderFile);
			
			File[] folders = FTBUConfigGeneral.guidepacksFolderFile.listFiles();
			
			if(folders != null && folders.length > 0)
			{
				for(File f : folders)
				{
					if(f.isDirectory())
					{
						try
						{
							GuideLocalRepo repo = new GuideLocalRepo(f);
							localRepos.put(repo.getID(), repo);
						}
						catch(Exception ex2)
						{
							FTBU.logger.error("Failed to load local repo " + f.getName());
							//ex2.printStackTrace();
						}
					}
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		FTBLib.dev_logger.info("Loaded " + localRepos.size() + " local repos after " + (LMUtils.millis() - ms) + " ms: " + localRepos.values());
		
		for(GuideLocalRepo r : localRepos.values())
		{
			addSub(r.getInfoPage(mode.getID()));
		}
		
		cleanup();
		clientGuideGui = null;
	}
}