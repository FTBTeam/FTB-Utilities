package ftb.utils.api.guide;

import ftb.lib.FTBLib;
import ftb.lib.api.*;
import ftb.utils.mod.FTBU;
import ftb.utils.mod.client.gui.guide.GuiGuide;
import latmod.lib.LMFileUtils;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.util.Arrays;

public class ClientGuideFile extends GuideFile
{
	public static final ClientGuideFile instance = new ClientGuideFile("guide");
	
	public ClientGuideFile(String id)
	{
		super(id);
		main.setTitle(new ChatComponentTranslation("player_action.ftbu.guide"));
	}
	
	public void reload(EventFTBReload e)
	{
		if(FTBLib.DEV_ENV) FTBU.logger.info("Guide reloaded @ " + e.world.side + " as " + e.world.getMode());
		
		main.clear();
		
		File file = GameModes.getGameModes().commonMode.getFile("guide/");
		if(file.exists() && file.isDirectory())
		{
			File[] f = file.listFiles();
			if(f != null && f.length > 0)
			{
				Arrays.sort(f, LMFileUtils.fileComparator);
				for(File aF : f) loadFromFiles(main, aF);
			}
		}
		
		file = e.world.getMode().getFile("guide/");
		if(file.exists() && file.isDirectory())
		{
			File[] f = file.listFiles();
			if(f != null && f.length > 0)
			{
				Arrays.sort(f, LMFileUtils.fileComparator);
				for(File aF : f) loadFromFiles(main, aF);
			}
		}
		
		file = e.world.getMode().getFile("guide_intro.txt");
		if(file.exists() && file.isFile())
		{
			try
			{
				String text = LMFileUtils.loadAsText(file);
				if(text != null && !text.isEmpty()) main.printlnText(text.replace("\r", ""));
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		links.clear();
		links.putAll(loadLinksFromFile(new File(FTBLib.folderModpack, "guide_links.json")));
		MinecraftForge.EVENT_BUS.post(new EventFTBUClientGuide(this));
		
		main.cleanup();
		GuiGuide.clientGuideGui = null;
	}
}