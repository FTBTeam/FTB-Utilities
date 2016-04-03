package ftb.utils.api.guide.repos;

import com.google.gson.JsonElement;
import latmod.lib.LMUtils;
import latmod.lib.net.*;

import java.util.*;

/**
 * Created by LatvianModder on 03.04.2016.
 */
public class GuideRepoList
{
	public static final Map<String, GuideOnlineRepo> repos = new HashMap<>();
	
	public static void refresh()
	{
		repos.clear();
		long ms = LMUtils.millis();
		
		try
		{
			for(JsonElement e : new LMURLConnection(RequestMethod.SIMPLE_GET, "https://raw.githubusercontent.com/Slowpoke101/FTBGuides/master/repositories.json").connect().asJson().getAsJsonArray())
			{
				try
				{
					GuideOnlineRepo repo = new GuideOnlineRepo(e.getAsString());
					repos.put(repo.getID(), repo);
					System.out.println("Loaded repo " + repo.getID());
				}
				catch(Exception ex2)
				{
					System.err.println("Failed to load repo " + e.getAsString());
					//ex2.printStackTrace();
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		System.out.println("Loaded " + repos.size() + " repos after " + (LMUtils.millis() - ms) + " ms: " + repos.values());
	}
}