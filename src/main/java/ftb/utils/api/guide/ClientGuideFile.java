package ftb.utils.api.guide;

import ftb.lib.FTBLib;
import ftb.lib.api.*;
import ftb.lib.mod.FTBLibFinals;
import ftb.utils.mod.FTBU;
import ftb.utils.mod.client.gui.guide.GuiGuide;
import latmod.lib.LMFileUtils;
import net.minecraft.util.*;

import java.io.File;
import java.util.Arrays;

public class ClientGuideFile extends GuideFile
{
	public static final ClientGuideFile instance = new ClientGuideFile(new ChatComponentTranslation("player_action.ftbu.guide"));
	
	public ClientGuideFile(IChatComponent title)
	{ super(title); }
	
	public void reload(EventFTBReload e)
	{
		if(FTBLibFinals.DEV) FTBU.logger.info("Guide reloaded @ " + e.world.side + " as " + e.world.getMode());
		
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
				if(text != null && !text.isEmpty()) main.println(text.replace("\r", ""));
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		links.clear();
		links.putAll(loadLinksFromFile(new File(FTBLib.folderModpack, "guide_links.json")));
		new EventFTBUClientGuide(this).post();
		
		main.cleanup();
		GuiGuide.clientGuideGui = null;
	}
}