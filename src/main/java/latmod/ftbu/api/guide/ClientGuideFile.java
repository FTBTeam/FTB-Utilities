package latmod.ftbu.api.guide;

import ftb.lib.FTBLib;
import ftb.lib.api.*;
import ftb.lib.mod.FTBLibFinals;
import latmod.ftbu.mod.*;
import latmod.ftbu.mod.client.gui.guide.GuiGuide;
import latmod.lib.LMFileUtils;
import net.minecraft.util.*;

import java.io.File;
import java.util.Arrays;

public class ClientGuideFile extends GuideFile
{
	public static final ClientGuideFile instance = new ClientGuideFile(new ChatComponentTranslation(FTBUFinals.ASSETS + "button.guide"));
	
	public ClientGuideFile(IChatComponent title)
	{ super(title); }
	
	public void reload(EventFTBReload e)
	{
		if(FTBLibFinals.DEV) FTBU.mod.logger.info("Guide reloaded @ " + e.world.side);
		
		main.clear();
		
		File file = GameModes.getGameModes().commonMode.getFile("guide/");
		if(file.exists() && file.isDirectory())
		{
			File[] f = file.listFiles();
			if(f != null && f.length > 0)
			{
				Arrays.sort(f, LMFileUtils.fileComparator);
				for(int i = 0; i < f.length; i++)
					loadFromFiles(main, f[i]);
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
					loadFromFiles(main, f[i]);
			}
		}
		
		file = e.world.getMode().getFile("guide_intro.txt");
		if(file.exists() && file.isFile())
		{
			try
			{
				String text = LMFileUtils.loadAsText(file);
				if(text != null && !text.isEmpty())
					main.println(text.replace("\r", ""));
			}
			catch(Exception ex)
			{ ex.printStackTrace(); }
		}
		
		loadLinksFromFile(new File(FTBLib.folderModpack, "guide_links.json"));
		new EventFTBUClientGuide(this).post();
		
		main.cleanup();
		GuiGuide.clientGuideGui = null;
	}
}