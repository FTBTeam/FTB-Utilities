package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbl.api.config.IConfigValue;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by LatvianModder on 27.09.2016.
 */
public interface IRank extends IStringSerializable, IJsonSerializable
{
    IRank getParent();

    void setParent(IRank r);

    Event.Result hasPermission(String permission);

    IConfigValue getConfig(String id);

    default String getPrefix()
    {
        return "<";
    }

    default String getSuffix()
    {
        return "> ";
    }
}