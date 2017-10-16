package com.feed_the_beast.ftbu.util;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.icon.ImageIcon;
import com.feed_the_beast.ftbl.lib.util.CommonUtils;
import com.feed_the_beast.ftbl.lib.util.JsonUtils;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.FTBUConfig;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.FTBUtilitiesAPI;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class Badges
{
	private static final String BADGE_BASE_URL = "http://api.latmod.com/badges/get?id=";

	public static final Map<UUID, String> BADGE_CACHE = new HashMap<>();
	public static final Map<UUID, String> LOCAL_BADGES = new HashMap<>();

	public static void update(UUID playerId)
	{
		BADGE_CACHE.remove(playerId);
	}

	public static String get(UUID playerId)
	{
		String b = BADGE_CACHE.get(playerId);

		if (b != null)
		{
			return b;
		}

		b = getRaw(playerId);
		BADGE_CACHE.put(playerId, b);
		return b;
	}

	private static String getRaw(UUID playerId)
	{
		IForgePlayer player = FTBLibAPI.API.getUniverse().getPlayer(playerId);

		if (player == null || player.isFake())
		{
			return "";
		}

		FTBUPlayerData data = FTBUPlayerData.get(player);

		if (!data.renderBadge.getBoolean())
		{
			return "";
		}
		else if (FTBUConfig.login.enable_global_badges && !data.disableGlobalBadge.getBoolean())
		{
			try
			{
				String s = StringUtils.readString(new URL(BADGE_BASE_URL + StringUtils.fromUUID(playerId)).openStream());

				if (!s.isEmpty())
				{
					return s;
				}
			}
			catch (Exception ex)
			{
				return ImageIcon.MISSING_IMAGE.toString();
			}
		}

		String s = LOCAL_BADGES.get(playerId);
		return (s == null || s.isEmpty()) ? FTBUtilitiesAPI.API.getRankConfig(player.getProfile(), FTBUPermissions.BADGE).getString() : s;
	}

	public static boolean reloadServerBadges()
	{
		try
		{
			BADGE_CACHE.clear();
			LOCAL_BADGES.clear();
			File file = new File(CommonUtils.folderLocal, "ftbutilities/server_badges.json");

			if (!file.exists())
			{
				JsonObject o = new JsonObject();
				o.addProperty("uuid", "url_to.png");
				o.addProperty("uuid2", "url2_to.png");
				JsonUtils.toJson(file, o);
			}
			else
			{
				for (Map.Entry<String, JsonElement> entry : JsonUtils.fromJson(file).getAsJsonObject().entrySet())
				{
					UUID id = StringUtils.fromString(entry.getKey());

					if (id != null)
					{
						LOCAL_BADGES.put(id, entry.getValue().getAsString());
					}
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
}