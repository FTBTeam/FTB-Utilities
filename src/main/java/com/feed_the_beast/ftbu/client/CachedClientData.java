package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbl.lib.icon.Icon;
import com.feed_the_beast.ftbu.net.MessageRequestBadge;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class CachedClientData
{
	private static final Map<UUID, Icon> BADGE_CACHE = new HashMap<>();

	public static void clear()
	{
		BADGE_CACHE.clear();
	}

	public static Icon getBadge(UUID id)
	{
		Icon tex = BADGE_CACHE.get(id);

		if (tex == null)
		{
			tex = Icon.EMPTY;
			BADGE_CACHE.put(id, tex);
			new MessageRequestBadge(id).sendToServer();
		}

		return tex;
	}

	public static void setBadge(UUID id, String url)
	{
		BADGE_CACHE.put(id, Icon.getIcon(url));
	}
}