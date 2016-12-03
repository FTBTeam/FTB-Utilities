package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbl.lib.client.FTBLibClient;
import com.feed_the_beast.ftbl.lib.util.LMStringUtils;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.net.MessageRequestBadge;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public class CachedClientData
{
    private static final String BASE_URL = "http://api.latmod.com/badges/get?id=";
    public static final ResourceLocation NO_BADGE = new ResourceLocation(FTBUFinals.MOD_ID, "textures/no_badge.png");
    private static final ResourceLocation FAILED_BADGE = new ResourceLocation(FTBUFinals.MOD_ID, "textures/failed_badge.png");
    private static final Map<UUID, ResourceLocation> BADGE_CACHE = new HashMap<>();
    private static final Map<UUID, String> BADGES = new HashMap<>();
    public static final Map<UUID, Integer> FLAGS = new HashMap<>();

    private static class ThreadGetBadge extends Thread
    {
        private UUID playerID;

        public ThreadGetBadge(UUID id)
        {
            playerID = id;
            setDaemon(true);
        }

        @Override
        public void run()
        {
            int flags = FLAGS.containsKey(playerID) ? FLAGS.get(playerID) : 0;

            if((flags & FTBUPlayerData.FLAG_RENDER_BADGE) == 0)
            {
                return;
            }

            if((flags & FTBUPlayerData.FLAG_DISABLE_GLOBAL_BADGE) == 0)
            {
                try
                {
                    String s = LMStringUtils.readString(new URL(BASE_URL + LMStringUtils.fromUUID(playerID)).openStream());

                    if(!s.isEmpty())
                    {
                        BADGES.put(playerID, s);
                        return;
                    }
                }
                catch(Exception ex)
                {
                }
            }

            String badge = BADGES.get(playerID);

            if(badge == null)
            {
                new MessageRequestBadge(playerID).sendToServer();
            }
            else
            {
                final String url = badge;
                Minecraft.getMinecraft().addScheduledTask(() -> setBadge(playerID, url));
            }
        }
    }

    public static void clear()
    {
        BADGES.clear();
        BADGE_CACHE.clear();
        FLAGS.clear();
    }

    public static ResourceLocation getBadgeTexture(UUID id)
    {
        ResourceLocation tex = BADGE_CACHE.get(id);

        if(tex == null)
        {
            tex = NO_BADGE;
            BADGE_CACHE.put(id, tex);
            new ThreadGetBadge(id).start();
        }
        else if(tex.equals(NO_BADGE))
        {
            String url = BADGES.get(id);

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
        BADGES.put(id, url);
        BADGE_CACHE.put(id, NO_BADGE);
    }

    public static void setFlags(UUID playerID, int flags)
    {
        FLAGS.put(playerID, flags);
    }
}