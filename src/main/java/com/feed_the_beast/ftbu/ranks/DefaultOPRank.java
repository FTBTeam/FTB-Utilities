package com.feed_the_beast.ftbu.ranks;

import com.feed_the_beast.ftbl.api.IRankConfig;
import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbu.api.IRank;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by LatvianModder on 27.09.2016.
 */
public class DefaultOPRank extends AbstractDefaultRank
{
    public static final DefaultOPRank INSTANCE = new DefaultOPRank();

    @Override
    public String getName()
    {
        return "op";
    }

    @Override
    public IRank getParent()
    {
        return DefaultPlayerRank.INSTANCE;
    }

    @Override
    public Event.Result hasPermission(String permission)
    {
        return Event.Result.ALLOW;
    }

    @Override
    public String getDisplayName()
    {
        return "OP";
    }

    @Override
    public TextFormatting getColor()
    {
        return TextFormatting.DARK_GREEN;
    }

    @Override
    IConfigValue createValue(IRankConfig config)
    {
        return config.getDefOPValue().copy();
    }
}