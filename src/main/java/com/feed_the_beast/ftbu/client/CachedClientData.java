package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbl.lib.io.LMConnection;
import com.feed_the_beast.ftbl.lib.io.RequestMethod;
import com.feed_the_beast.ftbu.badges.BadgeStorage;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public class CachedClientData
{
    public static final BadgeStorage GLOBAL_BADGES = new BadgeStorage();
    public static final BadgeStorage LOCAL_BADGES = new BadgeStorage();

    public static void reloadGlobalBadges()
    {
        GLOBAL_BADGES.clear();

        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    LMConnection connection = new LMConnection(RequestMethod.GET, "http://badges.latmod.com/badges.json");
                    GLOBAL_BADGES.loadBadges(connection.connect().asJson());
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        };

        thread.setDaemon(true);
        thread.start();
    }
}