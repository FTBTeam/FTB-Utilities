package com.feed_the_beast.ftbu.gui.guide;

import com.feed_the_beast.ftbl.api.info.impl.InfoPage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 17.07.2016.
 */
@SideOnly(Side.CLIENT)
public abstract class GuideRepoList
{
    private final List<Guide> list = new ArrayList<>();
    private Thread thread;

    public List<Guide> getList()
    {
        return list;
    }

    public final void reload(final InfoPage infoPage, boolean useThread)
    {
        list.clear();

        thread = null;

        if(useThread)
        {
            thread = new Thread()
            {
                @Override
                public void run()
                {
                    try
                    {
                        onReload(infoPage, list);
                        thread = null;
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
        else
        {
            try
            {
                onReload(infoPage, list);
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    protected abstract void onReload(InfoPage infoPage, List<Guide> guides) throws Exception;
}
