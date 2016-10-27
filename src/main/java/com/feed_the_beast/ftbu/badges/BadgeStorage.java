package com.feed_the_beast.ftbu.badges;

import com.feed_the_beast.ftbl.lib.util.LMStringUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by LatvianModder on 24.05.2016.
 */
public class BadgeStorage
{
    public final Map<UUID, String> map;

    public BadgeStorage()
    {
        map = new HashMap<>();
    }

    public void clear()
    {
        map.clear();
    }

    public void loadBadges(JsonElement e)
    {
        if(!e.isJsonObject())
        {
            return;
        }

        e.getAsJsonObject().entrySet().forEach(entry ->
        {
            if(entry.getValue().isJsonPrimitive())
            {
                UUID id = LMStringUtils.fromString(entry.getKey());

                if(id != null)
                {
                    map.put(id, entry.getValue().getAsString());
                }
            }
            else
            {
                JsonObject o = entry.getValue().getAsJsonObject();
                String badge = o.get("badge").getAsString();

                if(o.has("players"))
                {
                    o.get("players").getAsJsonObject().entrySet().forEach(entry2 ->
                    {
                        UUID id = LMStringUtils.fromString(entry2.getKey());

                        if(id != null)
                        {
                            map.put(id, badge);
                        }
                    });
                }
                else
                {
                    UUID id = LMStringUtils.fromString(entry.getKey());

                    if(id != null)
                    {
                        map.put(id, badge);
                    }
                }
            }
        });
    }

    public void copyFrom(BadgeStorage storage)
    {
        map.putAll(storage.map);
    }
}