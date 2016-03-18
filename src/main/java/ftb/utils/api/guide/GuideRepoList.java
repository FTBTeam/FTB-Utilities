package ftb.utils.api.guide;

import com.google.gson.*;
import ftb.lib.FTBLib;
import ftb.lib.api.GameMode;
import ftb.utils.FTBU;
import ftb.utils.client.gui.GuiGuide;
import latmod.lib.github.GitHubAPI;
import latmod.lib.net.*;
import net.minecraft.util.ChatComponentTranslation;

import java.io.File;
import java.util.*;

/**
 * Created by LatvianModder on 18.03.2016.
 */
public class GuideRepoList
{
	public static final String REPOSITORIES_JSON = GitHubAPI.RAW_CONTENT + "Slowpoke101/FTBGuides/master/repositories.json";
	public static final List<GuideRepo> repos = new ArrayList<>();
	private static Thread thread = null;
	public static final GuideFile clientGuideFile = new GuideFile("guide", new ChatComponentTranslation("player_action.ftbu.guide"));
	
	public static File getFolder()
	{
		File f = new File(FTBLib.folderLocal, "guidepacks/");
		if(!f.exists()) f.mkdirs();
		return f;
	}
	
	public static void refresh()
	{
		if(thread != null) return;
		
		repos.clear();
		
		thread = new Thread("FTBU_Guides")
		{
			public void run()
			{
				FTBU.logger.info("Connecting to " + REPOSITORIES_JSON);
				
				List<GuideRepo> repos1 = new ArrayList<>();
				
				try
				{
					JsonArray a = new LMURLConnection(RequestMethod.SIMPLE_GET, REPOSITORIES_JSON).connect().asJson().getAsJsonArray();
					
					for(JsonElement e : a)
					{
						try
						{
							String[] s1 = e.getAsString().split("/");
							GuideRepo r = new GuideRepo(s1[0], s1[1], s1[2]);
							repos1.add(r);
						}
						catch(Exception ex)
						{
							ex.printStackTrace();
						}
					}
				}
				catch(Exception ex)
				{
					FTBU.logger.warn("Failed to refresh Guides: " + ex.toString());
				}
				
				repos.addAll(repos1);
				FTBU.logger.info("Repos: " + repos);
				thread = null;
			}
		};
		
		thread.start();
	}
	
	public static void reloadFromFolder(GameMode mode)
	{
		clientGuideFile.main.clear();
		/*
		
		File file = GameModes.getGameModes().commonMode.getFile("guide/");
		if(file.exists() && file.isDirectory())
		{
			File[] f = file.listFiles();
			if(f != null && f.length > 0)
			{
				Arrays.sort(f, LMFileUtils.fileComparator);
				for(File aF : f) clientGuideFile.loadFromFiles(clientGuideFile.main, aF);
			}
		}
		
		file = mode.getFile("guide/");
		if(file.exists() && file.isDirectory())
		{
			File[] f = file.listFiles();
			if(f != null && f.length > 0)
			{
				Arrays.sort(f, LMFileUtils.fileComparator);
				for(File aF : f) clientGuideFile.loadFromFiles(clientGuideFile.main, aF);
			}
		}
		
		file = mode.getFile("guide_intro.txt");
		if(file.exists() && file.isFile())
		{
			try
			{
				String text = LMFileUtils.loadAsText(file);
				if(text != null && !text.isEmpty()) clientGuideFile.main.printlnText(text.replace("\r", ""));
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		clientGuideFile.links.clear();
		clientGuideFile.links.putAll(clientGuideFile.loadLinksFromFile(new File(FTBLib.folderModpack, "guide_links.json")));
		*/
		
		clientGuideFile.main.cleanup();
		GuiGuide.clientGuideGui = null;
	}
}
