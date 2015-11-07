package latmod.ftbu.api.guide;

import java.io.File;
import java.util.Map;

import ftb.lib.FTBLib;
import ftb.lib.api.EventFTBModeSet;
import ftb.lib.mod.FTBLibFinals;
import latmod.ftbu.mod.*;
import latmod.ftbu.mod.client.gui.guide.GuideLinkSerializer;
import latmod.lib.*;
import net.minecraft.util.*;

public class ClientGuideFile extends GuideFile
{
	public static final ClientGuideFile instance = new ClientGuideFile(new ChatComponentTranslation(FTBUFinals.ASSETS + "button.guide"));
	
	public ClientGuideFile(IChatComponent title)
	{ super(title); }
	
	public void reload(EventFTBModeSet e)
	{
		if(FTBLibFinals.DEV) FTBU.mod.logger.info("Guide reloaded @ " + e.side);
		
		main.clear();
		
		File file = e.getCommonFile("guide/");
		if(file.exists() && file.isDirectory())
		{
			File[] f = file.listFiles();
			if(f != null && f.length > 0)
				for(int i = 0; i < f.length; i++)
					loadFromFiles(main, f[i]);
		}
		
		file = e.getFile("guide/");
		if(file.exists() && file.isDirectory())
		{
			File[] f = file.listFiles();
			if(f != null && f.length > 0)
				for(int i = 0; i < f.length; i++)
					loadFromFiles(main, f[i]);
		}
		
		file = e.getFile("guide_intro.txt");
		if(file.exists() && file.isFile())
		{
			try
			{
				String text = LMFileUtils.loadAsText(file);
				main.println(text);
			}
			catch(Exception ex)
			{ ex.printStackTrace(); }
		}
		
		loadLinksFromFile(new File(FTBLib.folderModpack, "guide_links.json"));
		new EventFTBUClientGuide(this).post();
	}
	
	private static class LinksMap
	{
		public Map<String, GuideLink> links;
	}
	
	protected void loadFromFiles(GuideCategory c, File f)
	{
		if(f == null || !f.exists()) return;
		
		String name = f.getName();
		
		if(f.isDirectory())
		{
			File[] f1 = f.listFiles();
			
			if(f1 != null && f1.length > 0)
			{
				GuideCategory c1 = c.getSub(new ChatComponentText(name));
				for(File f2 : f1) loadFromFiles(c1, f2);
			}
		}
		else if(f.isFile())
		{
			if(name.endsWith(".txt"))
			{
				try
				{
					GuideCategory c1 = c.getSub(new ChatComponentText(name.substring(0, name.length() - 4)));
					String text = LMFileUtils.loadAsText(f);
					c1.println(text);
				}
				catch(Exception e)
				{ e.printStackTrace(); }
			}
		}
	}
	
	protected void loadLinksFromFile(File f)
	{
		if(f == null || !f.exists()) return;
		
		links.clear();
		LinksMap linksMap = LMJsonUtils.fromJsonFile(GuideLinkSerializer.gson, LMFileUtils.newFile(f), LinksMap.class);
		if(linksMap != null && linksMap.links != null) links.putAll(linksMap.links);
	}
}