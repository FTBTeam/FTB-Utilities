package com.feed_the_beast.ftbu.ranks;

import com.feed_the_beast.ftbl.api.IRankConfig;
import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.lib.config.PropertyNull;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.api.IRank;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by LatvianModder on 01.12.2016.
 */
public enum EmptyRank implements IRank
{
    INSTANCE;

    @Override
    public String getName()
    {
        return "";
    }

    @Override
    public IRank getParent()
    {
        return this;
    }

    @Override
    public Event.Result hasPermission(String permission)
    {
        return Event.Result.DEFAULT;
    }

    @Override
    public IConfigValue getConfig(String id)
    {
        IRankConfig config = FTBLibIntegration.API.getRankConfigRegistry().get(id);
        return config == null ? PropertyNull.INSTANCE : config.getDefValue();
    }

    @Override
    public TextFormatting getColor()
    {
        return TextFormatting.WHITE;
    }

    @Override
    public void fromJson(JsonElement json)
    {
    }

    @Override
    public JsonElement getSerializableElement()
    {
        return JsonNull.INSTANCE;
    }
}