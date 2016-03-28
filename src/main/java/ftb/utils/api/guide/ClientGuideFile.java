package ftb.utils.api.guide;

import ftb.lib.FTBLib;
import ftb.lib.api.GameModes;
import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.events.ReloadEvent;
import ftb.lib.api.info.InfoPage;
import ftb.lib.mod.client.gui.info.GuiInfo;
import ftb.utils.FTBU;
import latmod.lib.LMFileUtils;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.*;

import java.io.File;
import java.util.Arrays;

@SideOnly(Side.CLIENT)
public class ClientGuideFile extends InfoPage
{
	public static final ClientGuideFile instance = new ClientGuideFile("ClientConfig");
	
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
	
	public void reload(ReloadEvent e)
	{
		if(FTBLib.DEV_ENV) FTBU.logger.info("Guide reloaded @ " + e.world.side + " as " + e.world.getMode());
		
		clear();
		
		File file = GameModes.instance().commonMode.getFile("guide/");
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
		
		MinecraftForge.EVENT_BUS.post(new EventFTBUClientGuide(this));
		
		cleanup();
		clientGuideGui = null;
	}
	
	@SideOnly(Side.CLIENT)
	public void initGUI(GuiInfo gui)
	{
		clientGuideGui = gui;
	}
}