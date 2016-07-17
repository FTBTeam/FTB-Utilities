package com.feed_the_beast.ftbu.gui.guide;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Created by PC on 17.07.2016.
 */
public abstract class GuideRepoList
{
    private final Map<GuideType, List<Guide>> map = new EnumMap<>(GuideType.class);
    private Thread thread;

    public List<Guide> getList(GuideType type)
    {
        List<Guide> l = map.get(type);
        return (l != null && !l.isEmpty()) ? l : Collections.emptyList();
    }

    public final void reload()
    {
        map.clear();

        thread = null;
        thread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    onReload(map);
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

    protected abstract void onReload(Map<GuideType, List<Guide>> m) throws Exception;
}
