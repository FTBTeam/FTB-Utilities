package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.config.ConfigKey;
import com.feed_the_beast.ftbl.api.config.impl.PropertyBool;

/**
 * Created by LatvianModder on 13.09.2016.
 */
public class FTBUClientConfig
{
    private static final PropertyBool RENDER_BADGES = new PropertyBool(true);
    private static final PropertyBool LIGHT_VALUE_TEXTURE_X = new PropertyBool(true);

    public static void init()
    {
        FTBLibAPI.get().getRegistries().clientConfig().add(new ConfigKey("ftbu.badges", RENDER_BADGES.copy(), null), RENDER_BADGES);
        FTBLibAPI.get().getRegistries().clientConfig().add(new ConfigKey("ftbu.light_value_texture_x", LIGHT_VALUE_TEXTURE_X.copy(), null), LIGHT_VALUE_TEXTURE_X);
    }

    public static boolean renderBadges()
    {
        return RENDER_BADGES.getBoolean();
    }

    public static boolean lightValueTextureX()
    {
        return LIGHT_VALUE_TEXTURE_X.getBoolean();
    }
}
