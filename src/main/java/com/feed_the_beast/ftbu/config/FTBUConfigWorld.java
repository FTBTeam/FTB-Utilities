package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.config.ConfigValue;
import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.latmod.lib.config.PropertyBool;
import com.latmod.lib.config.PropertyCustom;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTBase;

import java.util.ArrayList;
import java.util.Collection;

public class FTBUConfigWorld
{
    @ConfigValue(id = "world.chunk_claiming", file = FTBUFinals.MOD_ID)
    public static final PropertyBool CHUNK_CLAIMING = new PropertyBool(true);

    @ConfigValue(id = "world.chunk_loading", file = FTBUFinals.MOD_ID)
    public static final PropertyBool CHUNK_LOADING = new PropertyBool(true);

    @ConfigValue(id = "world.safe_spawn", file = FTBUFinals.MOD_ID, info = "If set to true, explosions and hostile mobs in spawn area will be disabled, players won't be able to attack each other in spawn area")
    public static final PropertyBool SAFE_SPAWN = new PropertyBool(false);

    @ConfigValue(id = "world.blocked_entities", file = FTBUFinals.MOD_ID, info = "Entity IDs that are banned from world. They will not spawn and existing ones will be destroyed")
    public static final ConfigEntryBannedEntityList BLOCKED_ENTITIES = new ConfigEntryBannedEntityList(new ArrayList<>());

    @ConfigValue(id = "world.spawn_area_in_sp", file = FTBUFinals.MOD_ID, info = "Enable spawn area in singleplayer")
    public static final PropertyBool SPAWN_AREA_IN_SP = new PropertyBool(false);

    public static class ConfigEntryBannedEntityList extends PropertyCustom
    {
        public final Collection<Class<?>> list;

        public ConfigEntryBannedEntityList(Collection<Class<?>> c)
        {
            list = c;
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

        @Override
        public IConfigValue copy()
        {
            return new ConfigEntryBannedEntityList(new ArrayList<>(list));
        }

        @Override
        public NBTBase serializeNBT()
        {
            return null;
        }

        @Override
        public void deserializeNBT(NBTBase nbt)
        {
        }

        public boolean isEntityBanned(Class<?> c)
        {
            for(Class<?> c1 : BLOCKED_ENTITIES.list)
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