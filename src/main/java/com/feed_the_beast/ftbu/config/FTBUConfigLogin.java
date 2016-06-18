package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.config.ConfigEntryCustom;
import com.feed_the_beast.ftbl.api.item.ItemStackSerializer;
import com.feed_the_beast.ftbl.util.JsonHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.latmod.lib.annotations.Info;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FTBUConfigLogin
{
    @Info("Message of the day. This will be displayed when player joins the server")
    public static final ConfigEntryChatComponentList motd = new ConfigEntryChatComponentList();

    @Info({"Items to give player when he first joins the server", "Format: \"StringID Size Metadata\" or {nbt}"})
    public static final ConfigEntryItemStackList starting_items = new ConfigEntryItemStackList();

    public static class ConfigEntryChatComponentList extends ConfigEntryCustom
    {
        public final List<ITextComponent> components;

        public ConfigEntryChatComponentList()
        {
            components = new ArrayList<>();
        }

        @Override
        public void fromJson(@Nonnull JsonElement o)
        {
            components.clear();

            if(o.isJsonArray())
            {
                for(JsonElement e : o.getAsJsonArray())
                {
                    ITextComponent c = JsonHelper.deserializeICC(e);

                    if(c != null)
                    {
                        components.add(c);
                    }
                }
            }
        }

        @Nonnull
        @Override
        public JsonElement getSerializableElement()
        {
            JsonArray a = new JsonArray();

            for(ITextComponent c : components)
            {
                a.add(JsonHelper.serializeICC(c));
            }

            return a;
        }
    }

    public static class ConfigEntryItemStackList extends ConfigEntryCustom
    {
        private final List<Map.Entry<ItemStack, JsonElement>> items;

        public ConfigEntryItemStackList()
        {
            items = new ArrayList<>();
        }

        @Override
        public void fromJson(@Nonnull JsonElement o)
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

        @Nonnull
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


            return list;
        }
    }
}