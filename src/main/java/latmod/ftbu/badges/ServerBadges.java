package latmod.ftbu.badges;

import com.google.gson.*;
import ftb.lib.FTBLib;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.net.MessageUpdateBadges;
import latmod.ftbu.world.LMPlayerServer;
import latmod.lib.*;
import latmod.lib.json.UUIDTypeAdapterLM;
import latmod.lib.net.*;
import latmod.lib.util.Phase;
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
		
		JsonElement global = null, local = null;
		
		try
		{
			LMURLConnection connection = new LMURLConnection(RequestMethod.SIMPLE_GET, "http://latvianmodder.github.io/images/badges/global_badges.json");
			global = connection.connect().asJson();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		try
		{
			File file = LMFileUtils.newFile(new File(FTBLib.folderLocal, "badges.json"));
			local = LMJsonUtils.getJsonElement(file);
			
			if(local.isJsonNull())
			{
				local = new JsonObject();
				((JsonObject) local).add("badges", new JsonObject());
				((JsonObject) local).add("players", new JsonObject());
				LMJsonUtils.toJsonFile(file, local);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		loadBadges(global, Phase.PRE);
		loadBadges(local, Phase.PRE);
		
		loadBadges(global, Phase.POST);
		loadBadges(local, Phase.POST);
		
		sendToPlayer(null);
		FTBU.mod.logger.info("Loaded " + map.size() + " badges in " + (LMUtils.millis() - msStarted) + " ms!");
	}
	
	public static void sendToPlayer(EntityPlayerMP ep)
	{ new MessageUpdateBadges(map.values()).sendTo(ep); }
	
	private static void loadBadges(JsonElement e, Phase p)
	{
		if(e == null || !e.isJsonObject()) return;
		
		JsonObject o = e.getAsJsonObject();
		
		if(p == Phase.PRE)
		{
			if(o.has("badges"))
			{
				JsonObject o1 = o.get("badges").getAsJsonObject();
				
				for(Map.Entry<String, JsonElement> entry : o1.entrySet())
				{
					Badge b = new Badge(entry.getKey(), entry.getValue().getAsString());
					map.put(b.ID, b);
				}
			}
		}
		else
		{
			if(o.has("players"))
			{
				JsonObject o1 = o.get("players").getAsJsonObject();
				
				for(Map.Entry<String, JsonElement> entry : o1.entrySet())
				{
					UUID id = UUIDTypeAdapterLM.getUUID(entry.getKey());
					if(id != null)
					{
						Badge b = map.get(entry.getValue().getAsString());
						if(b != null) uuid.put(id, b);
					}
				}
			}
		}
	}
	
	public static Badge getServerBadge(LMPlayerServer p)
	{
		if(p == null) return Badge.emptyBadge;
		
		Badge b = uuid.get(p.getProfile().getId());
		if(b != null) return b;
		
		if(b == null)
		{
			String rank = p.getRank().config.badge.get();
			if(!rank.isEmpty())
			{
				b = map.get(rank);
				if(b != null) return b;
			}
		}
		
		return Badge.emptyBadge;
	}
}