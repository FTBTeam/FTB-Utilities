package latmod.ftbu.badges;

import com.google.gson.*;
import ftb.lib.FTBLib;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.net.MessageUpdateBadges;
import latmod.ftbu.world.LMPlayerServer;
import latmod.lib.*;
import latmod.lib.net.*;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.File;
import java.util.*;

public class ServerBadges
{
	private static final HashMap<String, Badge> map = new HashMap<>();
	private static final HashMap<UUID, Badge> uuid = new HashMap<>();

	public static void reload()
	{
		Thread thread = new Thread()
		{
			public void run()
			{ reload0(); }
		};

		thread.setDaemon(true);
		thread.start();
	}

	private static void reload0()
	{
		long msStarted = LMUtils.millis();

		map.clear();
		uuid.clear();

		try
		{
			LMURLConnection connection = new LMURLConnection(RequestMethod.SIMPLE_GET, "http://latvianmodder.github.io/images/badges/global_badges.json");
			loadBadges(connection.connect().asJson());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		try
		{
			File file = LMFileUtils.newFile(new File(FTBLib.folderLocal, "badges.json"));
			JsonElement e = LMJsonUtils.getJsonElement(file);

			if(e.isJsonNull())
			{
				e = new JsonObject();
				((JsonObject) e).add("badges", new JsonObject());
				((JsonObject) e).add("players", new JsonObject());
				LMJsonUtils.toJsonFile(file, e);
			}

			loadBadges(e);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		sendToPlayer(null);
		FTBU.mod.logger.info("Loaded " + map.size() + " badges in " + (LMUtils.millis() - msStarted) + " ms!");
	}

	public static void sendToPlayer(EntityPlayerMP ep)
	{ new MessageUpdateBadges(map.values()).sendTo(ep); }

	private static void loadBadges(JsonElement e)
	{
		if(e == null || !e.isJsonObject()) return;

		JsonObject o = e.getAsJsonObject();

		if(o.has("badges"))
		{
			JsonObject o1 = o.get("badges").getAsJsonObject();

			for(Map.Entry<String, JsonElement> entry : o1.entrySet())
			{
				Badge b = new Badge(entry.getKey(), entry.getValue().getAsString());
				map.put(b.ID, b);
			}
		}

		if(o.has("players"))
		{
			JsonObject o1 = o.get("players").getAsJsonObject();

			for(Map.Entry<String, JsonElement> entry : o1.entrySet())
			{
				UUID id = LMStringUtils.fromString(entry.getKey());

				if(id != null)
				{
					Badge b = map.get(entry.getValue().getAsString());
					if(b != null) uuid.put(id, b);
				}
			}
		}
	}

	public static Badge getServerBadge(LMPlayerServer p)
	{
		if(p == null) return Badge.emptyBadge;

		Badge b = uuid.get(p.getUUID());

		if(b == null)
		{
			String rank = p.getRank().config.badge.get();
			if(!rank.isEmpty()) b = map.get(rank);
		}

		return (b == null) ? Badge.emptyBadge : b;
	}
}