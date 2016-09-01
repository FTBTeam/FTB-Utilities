package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.config.ConfigEntryBool;
import com.feed_the_beast.ftbl.api.config.ConfigEntryCustom;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.latmod.lib.annotations.Info;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;

import java.util.Collection;
import java.util.HashSet;

public class FTBUConfigWorld
{
    public static final ConfigEntryBool chunk_claiming = new ConfigEntryBool(true);
    public static final ConfigEntryBool chunk_loading = new ConfigEntryBool(true);

    @Info("If set to true, explosions and hostile mobs in spawn area will be disabled, players won't be able to attack each other in spawn area")
    public static final ConfigEntryBool safe_spawn = new ConfigEntryBool(false);

    @Info("Entity IDs that are banned from world. They will not spawn and existing ones will be destroyed")
    public static final ConfigEntryBannedEntityList blocked_entities = new ConfigEntryBannedEntityList();

    @Info("Enable spawn area in singleplayer")
    public static final ConfigEntryBool spawn_area_in_sp = new ConfigEntryBool(false);

    public static class ConfigEntryBannedEntityList extends ConfigEntryCustom
    {
        public final Collection<Class<?>> list;

        public ConfigEntryBannedEntityList()
        {
            list = new HashSet<>();
        }

        @Override
        public void fromJson(JsonElement o)
        {
            list.clear();

            if(o.isJsonArray())
            {
                for(JsonElement e : o.getAsJsonArray())
                {
                    Class<?> c = EntityList.NAME_TO_CLASS.get(e.getAsString());
                    if(c != null && Entity.class.isAssignableFrom(c))
                    {
                        list.add(c);
                    }
                }
            }
        }

        @Override
        public JsonElement getSerializableElement()
        {
            JsonArray a = new JsonArray();

            for(Class<?> c1 : list)
            {
                String s = EntityList.CLASS_TO_NAME.get(c1);

                if(s != null)
                {
                    a.add(new JsonPrimitive(s));
                }
            }

            return a;
        }

        public boolean isEntityBanned(Class<?> c)
        {
            for(Class<?> c1 : list)
            {
                if(c1.isAssignableFrom(c))
                {
                    return true;
                }
            }

            return false;
        }
    }
}