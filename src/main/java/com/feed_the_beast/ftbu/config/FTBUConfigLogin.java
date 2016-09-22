package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.config.ConfigValue;
import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.api.item.ItemStackSerializer;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.latmod.lib.config.PropertyBool;
import com.latmod.lib.config.PropertyCustom;
import com.latmod.lib.util.LMJsonUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.ITextComponent;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FTBUConfigLogin
{
    @ConfigValue(id = "login.enable_motd", file = FTBUFinals.MOD_ID)
    public static final PropertyBool ENABLE_MOTD = new PropertyBool(true);

    @ConfigValue(id = "login.enable_starting_items", file = FTBUFinals.MOD_ID)
    public static final PropertyBool ENABLE_STARTING_ITEMS = new PropertyBool(true);

    @ConfigValue(id = "login.motd", file = FTBUFinals.MOD_ID, info = "Message of the day. This will be displayed when player joins the server")
    public static final ConfigEntryChatComponentList MOTD = new ConfigEntryChatComponentList(new ArrayList<>());

    @ConfigValue(id = "login.starting_items", file = FTBUFinals.MOD_ID, info = "Items to give player when he first joins the server\nFormat: \"StringID Size Metadata\" or {nbt}")
    public static final ConfigEntryItemStackList STARTING_ITEMS = new ConfigEntryItemStackList(new ArrayList<>());

    public static class ConfigEntryChatComponentList extends PropertyCustom
    {
        public final List<ITextComponent> components;

        public ConfigEntryChatComponentList(List<ITextComponent> l)
        {
            components = l;
        }

        @Override
        public void fromJson(JsonElement o)
        {
            components.clear();

            if(o.isJsonArray())
            {
                for(JsonElement e : o.getAsJsonArray())
                {
                    ITextComponent c = LMJsonUtils.deserializeTextComponent(e);

                    if(c != null)
                    {
                        components.add(c);
                    }
                }
            }
        }

        @Override
        public JsonElement getSerializableElement()
        {
            JsonArray a = new JsonArray();

            for(ITextComponent c : components)
            {
                a.add(LMJsonUtils.serializeTextComponent(c));
            }

            return a;
        }

        @Override
        public IConfigValue copy()
        {
            return new ConfigEntryChatComponentList(new ArrayList<>(components));
        }

        @Override
        public NBTBase serializeNBT()
        {
            NBTTagList list = new NBTTagList();

            for(ITextComponent c : components)
            {
                list.appendTag(new NBTTagString(ITextComponent.Serializer.componentToJson(c)));
            }

            return list;
        }

        @Override
        public void deserializeNBT(NBTBase nbt)
        {
            components.clear();
            NBTTagList list = (NBTTagList) nbt;

            for(int i = 0; i < list.tagCount(); i++)
            {
                components.add(ITextComponent.Serializer.jsonToComponent(list.getStringTagAt(i)));
            }
        }
    }

    public static class ConfigEntryItemStackList extends PropertyCustom
    {
        private final List<Map.Entry<ItemStack, JsonElement>> items;

        public ConfigEntryItemStackList(List<Map.Entry<ItemStack, JsonElement>> l)
        {
            items = l;
        }

        @Override
        public void fromJson(JsonElement o)
        {
            items.clear();

            if(o.isJsonArray())
            {
                for(JsonElement e : o.getAsJsonArray())
                {
                    ItemStack is = ItemStackSerializer.deserialize(e);

                    if(is != null)
                    {
                        items.add(new AbstractMap.SimpleEntry<>(is, e));
                    }
                }
            }
        }

        @Override
        public JsonElement getSerializableElement()
        {
            JsonArray a = new JsonArray();

            for(Map.Entry<ItemStack, JsonElement> e : items)
            {
                a.add(e.getValue());
            }

            return a;
        }

        public List<ItemStack> getItems()
        {
            List<ItemStack> list = new ArrayList<>();

            for(Map.Entry<ItemStack, JsonElement> e : items)
            {
                list.add(e.getKey());
            }

            return list;
        }

        @Override
        public IConfigValue copy()
        {
            return new ConfigEntryItemStackList(new ArrayList<>(items));
        }

        @Override
        public NBTBase serializeNBT()
        {
            NBTTagList list = new NBTTagList();

            for(Map.Entry<ItemStack, JsonElement> entry : items)
            {
                list.appendTag(entry.getKey().serializeNBT());
            }

            return list;
        }

        @Override
        public void deserializeNBT(NBTBase nbt)
        {
            items.clear();
            NBTTagList list = (NBTTagList) nbt;

            for(int i = 0; i < list.tagCount(); i++)
            {
                ItemStack is = ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i));
                items.add(new AbstractMap.SimpleEntry<>(is, JsonNull.INSTANCE)); //FIXME?
            }
        }
    }
}