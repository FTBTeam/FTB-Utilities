package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbl.api.gui.IDrawableObject;
import com.feed_the_beast.ftbl.lib.client.ImageProvider;
import com.feed_the_beast.ftbu.net.MessageRequestBadge;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class CachedClientData
{
	private static final Map<UUID, IDrawableObject> BADGE_CACHE = new HashMap<>();

	public static void clear()
	{
		BADGE_CACHE.clear();
	}

	public static IDrawableObject getBadge(UUID id)
	{
		IDrawableObject tex = BADGE_CACHE.get(id);

		if (tex == null)
		{
			tex = ImageProvider.NULL;
			BADGE_CACHE.put(id, tex);
			new MessageRequestBadge(id).sendToServer();
		}

		return tex;
	}

	public static void setBadge(UUID id, String url)
	{
		BADGE_CACHE.put(id, ImageProvider.get(url));
	}
}