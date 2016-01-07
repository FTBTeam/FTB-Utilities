package latmod.ftbu.badges;

import com.google.gson.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.config.FTBUConfigLogin;
import latmod.lib.*;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class Badges
{
	public static final String DEF_BADGES = "http://latvianmodder.github.io/images/badges/global_badges.json";
	private static final HashMap<Integer, Badge> badgeCache = new HashMap<>();
	private static final HashMap<String, Badge> map = new HashMap<>();
	private static final HashMap<UUID, Badge> uuidBadges = new HashMap<>();
	private static final HashMap<String, Badge> rankBadges = new HashMap<>();

	public static void reload()
	{
		badgeCache.clear();
		map.clear();
		uuidBadges.clear();
		rankBadges.clear();

		Thread thread = new Thread("FTB_Badges")
		{
			public void run()
			{
				long msStarted = LMUtils.millis();
				FTBU.mod.logger.info("Loading badges...");

				loadBages(DEF_BADGES);

				if(!FTBUConfigLogin.custom_badges.get().isEmpty())
					loadBages(FTBUConfigLogin.custom_badges.get());

				FTBU.mod.logger.info("Loaded " + map.size() + " badges in " + (LMUtils.millis() - msStarted) + " ms!");
			}
		};

		thread.setDaemon(true);
		thread.start();
	}

	private static void loadBages(String url)
	{
		try
		{
			InputStreamReader reader = new InputStreamReader(new URL(url).openStream());
			JsonObject o = LMJsonUtils.getJsonElement(reader).getAsJsonObject();
			reader.close();

			if(o.has("badges"))
			{
				JsonObject o1 = o.get("badges").getAsJsonObject();

				for(Map.Entry<String, JsonElement> e : o1.entrySet())
				{
					UUID id = LMStringUtils.fromString(e.getKey());

					if(id != null)
					{
						Badge b = map.get(e.getValue().getAsString());
						if(b != null) uuidBadges.put(id, b);
					}
				}
			}

			if(o.has("players"))
			{
				JsonObject o1 = o.get("players").getAsJsonObject();
			}

			/*
			for(String k : list.badges.keySet())
				map.put(k, new Badge(list.badges.get(k)));

			for(UUID id : list.players.keySet())
			{
				Badge b = urlBadges.get(list.players.get(id));
				if(id != null && b != null)
					uuidBadges.put(id, b);
			}*/
		}
		catch(Exception e) { e.printStackTrace(); }
	}

	public static Badge get(int playerID)
	{
		Badge b = uuidBadges.get(Integer.valueOf(playerID));
		return (b == null) ? rankBadges.get(Integer.valueOf(playerID)) : b;
	}
}