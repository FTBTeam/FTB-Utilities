package com.feed_the_beast.ftbu.gui.guide.online;

import com.feed_the_beast.ftbl.api.info.impl.InfoPage;
import com.feed_the_beast.ftbl.gui.GuiInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by LatvianModder on 16.08.2016.
 */
@SideOnly(Side.CLIENT)
public class InfoPageOnlineGuideRepoList extends InfoPage
{
    private static GuiInfo cachedGui = null;

    private InfoPageOnlineGuideRepoList()
    {
        super("online_guide_repo_list");
    }

    public static GuiInfo getGui()
    {
        if(cachedGui == null)
        {
            cachedGui = new GuiInfo(new InfoPageOnlineGuideRepoList());
        }

        return cachedGui;
    }

    //FIXME: Infinite refreshes
    @Override
    public void refreshGui(@Nonnull GuiInfo gui)
    {
        OnlineGuideRepoList.INSTANCE.reload(true);
    }
}