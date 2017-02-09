package com.feed_the_beast.ftbu.ranks;

import com.feed_the_beast.ftbl.api.IRankConfig;
import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.lib.config.PropertyNull;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.api.IRank;

/**
 * Created by LatvianModder on 27.09.2016.
 */
public class DefaultPlayerRank extends DefaultRank
{
    public static final DefaultPlayerRank INSTANCE = new DefaultPlayerRank();

    private DefaultPlayerRank()
    {
        super("builtin_player");
    }

    @Override
    public IRank getParent()
    {
        return this;
    }

    @Override
    public IConfigValue getConfig(String id)
    {
        IRankConfig config = FTBLibIntegration.API.getRankConfigRegistry().get(id);
        return config == null ? PropertyNull.INSTANCE : config.getDefValue();
    }
}