package com.feed_the_beast.ftbu.gui.guide;

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

    public final void reload(boolean useThread)
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
                        onReload(list);
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
                onReload(list);
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    protected abstract void onReload(List<Guide> guides) throws Exception;
}
