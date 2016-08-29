package com.feed_the_beast.ftbu.gui.guide;

import com.feed_the_beast.ftbl.api.info.impl.InfoPage;
import com.feed_the_beast.ftbl.gui.GuiLoading;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by PC on 17.07.2016.
 */
@SideOnly(Side.CLIENT)
public abstract class GuideRepoList
{
    private Thread thread;

    public final void reload(final InfoPage infoPage, final GuiLoading guiLoading)
    {
        thread = null;

        thread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    infoPage.clear();
                    onReload(infoPage);
                    thread = null;
                    guiLoading.setFinished();
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

    protected abstract void onReload(InfoPage infoPage) throws Exception;
}
