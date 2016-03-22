package ftb.utils.api.guide;

import ftb.lib.FTBLib;
import ftb.lib.api.*;
import ftb.utils.mod.FTBU;
import ftb.utils.mod.client.gui.GuiGuide;
import latmod.lib.LMFileUtils;
import net.minecraft.util.ChatComponentTranslation;

import java.io.File;
import java.util.Arrays;

public class ClientGuideFile extends GuidePage
{
	public static final ClientGuideFile instance = new ClientGuideFile("ClientConfig");
	
	public ClientGuideFile(String id)
	{
		super(id);
		setTitle(new ChatComponentTranslation("player_action.ftbu.guide"));
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
		GuiGuide.clientGuideGui = null;
	}
}