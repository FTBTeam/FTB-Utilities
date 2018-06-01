package com.feed_the_beast.ftbutilities.data;

import com.feed_the_beast.ftblib.FTBLibConfig;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.io.DataReader;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.feed_the_beast.ftblib.lib.util.FileUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.google.gson.JsonElement;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class Badges
{
	private static final String BADGE_URL = "https://badges.latmod.com/get?id=";

	public static final Map<UUID, String> BADGE_CACHE = new HashMap<>();
	public static final Map<UUID, String> LOCAL_BADGES = new HashMap<>();

	public static void update(UUID playerId)
	{
		BADGE_CACHE.remove(playerId);
	}

	public static String get(Universe universe, UUID playerId)
	{
		String badge = BADGE_CACHE.get(playerId);

		if (badge != null)
		{
			return badge;
		}

		badge = getRaw(universe, playerId);
		BADGE_CACHE.put(playerId, badge);
		return badge;
	}

	private static String getRaw(Universe universe, UUID playerId)
	{
		ForgePlayer player = universe.getPlayer(playerId);

		if (player == null || player.isFake())
		{
			return "";
		}

		FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(player);

		if (!data.renderBadge())
		{
			return "";
		}
		else if (FTBUtilitiesConfig.login.enable_global_badges && !data.disableGlobalBadge())
		{
			try
			{
				String badge = DataReader.get(new URL(BADGE_URL + StringUtils.fromUUID(playerId)), DataReader.TEXT, universe.server.getServerProxy()).string(32);

				if (!badge.isEmpty())// && (FTBUtilitiesConfig.login.enable_event_badges || !response.getHeaderField("Event-Badge").equals("true")))
				{
					return badge;
				}
			}
			catch (Exception ex)
			{
				if (FTBLibConfig.debugging.print_more_errors)
				{
					FTBUtilities.LOGGER.warn("Badge API errored! " + ex);
				}
			}
		}

		String badge = LOCAL_BADGES.get(playerId);
		return (badge == null || badge.isEmpty()) ? player.getRankConfig(FTBUtilitiesPermissions.BADGE).getString() : badge;
	}

	public static boolean reloadServerBadges(Universe universe)
	{
		try
		{
			BADGE_CACHE.clear();
			LOCAL_BADGES.clear();
			File file = new File(CommonUtils.folderLocal, "ftbutilities/server_badges.json");

			if (!file.exists())
			{
				file = new File(CommonUtils.folderLocal, "ftbutilities/server_badges.txt");

				if (!file.exists())
				{
					List<String> list = new ArrayList<>();
					list.add("// For more info see https://guides.latmod.com/ftbutilities/ranks/badges/");
					list.add("");
					list.add("uuid: url_to.png");
					list.add("username: url2_to.png");
					FileUtils.saveSafe(file, list);
				}
				else
				{
					for (String s : DataReader.get(file).safeStringList())
					{
						if (s.isEmpty() || s.startsWith("//"))
						{
							continue;
						}

						String[] s1 = s.trim().split(":");

						if (s1.length == 2)
						{
							ForgePlayer player = universe.getPlayer(s1[0].trim());

							if (player != null)
							{
								LOCAL_BADGES.put(player.getId(), s1[1].trim());
							}
						}
					}
				}
			}
			else
			{
				for (Map.Entry<String, JsonElement> entry : DataReader.get(file).json().getAsJsonObject().entrySet())
				{
					ForgePlayer player = universe.getPlayer(entry.getKey());

					if (player != null)
					{
						LOCAL_BADGES.put(player.getId(), entry.getValue().getAsString());
					}
				}

				FileUtils.delete(file);
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