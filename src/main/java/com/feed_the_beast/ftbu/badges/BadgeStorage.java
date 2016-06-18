package com.feed_the_beast.ftbu.badges;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.latmod.lib.util.LMUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by LatvianModder on 24.05.2016.
 */
public class BadgeStorage
{
    public final Map<String, Badge> badgeMap;
    public final Map<UUID, Badge> badgePlayerMap;

    public BadgeStorage()
    {
        badgeMap = new HashMap<>();
        badgePlayerMap = new HashMap<>();
    }

    public void clear()
    {
        badgeMap.clear();
        badgePlayerMap.clear();
    }

    public void loadBadges(JsonElement e)
    {
        if(e == null || !e.isJsonObject())
        {
            return;
        }

        JsonObject o = e.getAsJsonObject();

        if(o.has("badges") && o.has("players"))
        {
            JsonObject o1 = o.get("badges").getAsJsonObject();

            for(Map.Entry<String, JsonElement> entry : o1.entrySet())
            {
                Badge b = new Badge(entry.getKey(), entry.getValue().getAsString());
                badgeMap.put(b.getID(), b);
            }

            o1 = o.get("players").getAsJsonObject();

            for(Map.Entry<String, JsonElement> entry : o1.entrySet())
            {
                UUID id = LMUtils.fromString(entry.getKey());
                if(id != null)
                {
                    Badge b = badgeMap.get(entry.getValue().getAsString());
                    if(b != null)
                    {
                        badgePlayerMap.put(id, b);
                    }
                }
            }
        }
    }

    public void copyFrom(BadgeStorage storage)
    {
        badgeMap.putAll(storage.badgeMap);
        badgePlayerMap.putAll(storage.badgePlayerMap);
    }
}