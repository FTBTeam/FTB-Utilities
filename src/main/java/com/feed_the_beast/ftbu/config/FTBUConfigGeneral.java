package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.config.ConfigEntryBool;
import com.feed_the_beast.ftbl.api.config.ConfigEntryCustom;
import com.feed_the_beast.ftbl.api.config.ConfigEntryDouble;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.latmod.lib.annotations.Info;
import com.latmod.lib.annotations.NumberBounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashSet;

public class FTBUConfigGeneral
{
    @NumberBounds(min = 0, max = 720)
    @Info({"Server will automatically shut down after X hours", "0 - Disabled", "0.5 - 30 minutes", "1 - 1 Hour", "24 - 1 Day", "168 - 1 Week", "720 - 1 Month"})
    public static final ConfigEntryDouble restart_timer = new ConfigEntryDouble(0D);

    @Info("If set to true, explosions and hostile mobs in spawn area will be disabled, players won't be able to attack each other in spawn area")
    public static final ConfigEntryBool safe_spawn = new ConfigEntryBool(false);

    @Info("Entity IDs that are banned from world. They will not spawn and existing ones will be destroyed")
    public static final ConfigEntryBannedEntityList blocked_entities = new ConfigEntryBannedEntityList();

    public static final ConfigEntryBool ranks_enabled = new ConfigEntryBool(false);
    //public static final ConfigEntryBool ranks_override_chat = new ConfigEntryBool(true);

    //public static final ConfigEntryBool ranks_override_commands = new ConfigEntryBool(true);

    @Info("Enable spawn area in singleplayer")
    public static final ConfigEntryBool spawn_area_in_sp = new ConfigEntryBool(false);

    public static final ConfigEntryBool server_info_difficulty = new ConfigEntryBool(true);
    public static final ConfigEntryBool server_info_mode = new ConfigEntryBool(true);

    public static class ConfigEntryBannedEntityList extends ConfigEntryCustom
    {
        public final Collection<Class<?>> list;

        public ConfigEntryBannedEntityList()
        {
            list = new HashSet<>();
        }

        @Override
        public void fromJson(@Nonnull JsonElement o)
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

        @Nonnull
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