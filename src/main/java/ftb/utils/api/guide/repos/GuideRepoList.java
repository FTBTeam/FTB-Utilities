package ftb.utils.api.guide.repos;

import com.google.gson.JsonElement;
import ftb.lib.FTBLib;
import latmod.lib.LMUtils;
import latmod.lib.net.*;

import java.util.*;

/**
 * Created by LatvianModder on 03.04.2016.
 */
public class GuideRepoList
{
	public static final Map<String, GuideOnlineRepo> onlineRepos = new HashMap<>();
	
	public static void refreshOnlineRepos()
	{
		onlineRepos.clear();
		long ms = LMUtils.millis();
		
		try
		{
			for(Map.Entry<String, JsonElement> e : new LMURLConnection(RequestMethod.SIMPLE_GET, "https://raw.githubusercontent.com/Slowpoke101/FTBGuides/master/repositories.json").connect().asJson().getAsJsonObject().entrySet())
			{
				try
				{
					GuideOnlineRepo repo = new GuideOnlineRepo(e.getKey(), e.getValue().getAsString());
					onlineRepos.put(repo.getID(), repo);
				}
				catch(Exception ex2)
				{
					System.err.println("Failed to load online repo " + e.getKey());
					//ex2.printStackTrace();
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		FTBLib.dev_logger.info("Loaded " + onlineRepos.size() + " online repos after " + (LMUtils.millis() - ms) + " ms: " + onlineRepos.values());
	}
}