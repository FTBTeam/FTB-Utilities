package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbl.lib.client.FTBLibClient;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.net.MessageRequestBadge;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public class CachedClientData
{
    public static final ResourceLocation NO_BADGE = new ResourceLocation(FTBUFinals.MOD_ID, "textures/no_badge.png");
    private static final ResourceLocation FAILED_BADGE = new ResourceLocation(FTBUFinals.MOD_ID, "textures/failed_badge.png");
    private static final Map<UUID, ResourceLocation> BADGE_CACHE = new HashMap<>();
    private static final Map<UUID, String> LOCAL_BADGES = new HashMap<>();

    public static void clear()
    {
        BADGE_CACHE.clear();
        LOCAL_BADGES.clear();
    }

    public static ResourceLocation getBadgeTexture(UUID id)
    {
        ResourceLocation tex = BADGE_CACHE.get(id);

        if(tex == null)
        {
            tex = NO_BADGE;
            BADGE_CACHE.put(id, tex);
            new MessageRequestBadge(id).sendToServer();
        }
        else if(tex.equals(NO_BADGE))
        {
            String url = LOCAL_BADGES.get(id);

            if(url != null)
            {
                if(!url.isEmpty())
                {
                    tex = new ResourceLocation(FTBUFinals.MOD_ID, "badges/" + url.replace(':', '.'));
                    FTBLibClient.getDownloadImage(tex, url, FAILED_BADGE, null);
                }

                BADGE_CACHE.put(id, tex);
            }
        }

        return tex;
    }

    public static void setBadge(UUID id, String url)
    {
        LOCAL_BADGES.put(id, url);
        BADGE_CACHE.put(id, NO_BADGE);
    }
}