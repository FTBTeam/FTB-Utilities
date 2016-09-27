package com.feed_the_beast.ftbu.ranks;

import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.api.rankconfig.IRankConfig;
import com.feed_the_beast.ftbu.api.IRank;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;

/**
 * Created by LatvianModder on 27.09.2016.
 */
public enum DefaultPlayerRank implements IRank
{
    INSTANCE;

    @Override
    public String getName()
    {
        return "player";
    }

    @Nullable
    @Override
    public IRank getParent()
    {
        return null;
    }

    @Override
    public Event.Result hasPermission(String permission)
    {
        return Event.Result.DEFAULT;
    }

    @Override
    public IConfigValue getConfig(IRankConfig id)
    {
        return id.getDefaultOPValue();
    }

    @Override
    public void fromJson(JsonElement json)
    {
    }

    @Override
    public JsonElement getSerializableElement()
    {
        //FIXME: Generate example player rank
        return JsonNull.INSTANCE;
    }
}
