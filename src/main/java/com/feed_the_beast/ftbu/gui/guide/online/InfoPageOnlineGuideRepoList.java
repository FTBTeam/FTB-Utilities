package com.feed_the_beast.ftbu.gui.guide.online;

import com.feed_the_beast.ftbl.api.info.impl.InfoPage;
import com.feed_the_beast.ftbl.gui.GuiInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by LatvianModder on 16.08.2016.
 */
@SideOnly(Side.CLIENT)
public class InfoPageOnlineGuideRepoList extends InfoPage
{
    public InfoPageOnlineGuideRepoList()
    {
        super("online_guide_repo_list");
    }

    //FIXME: Infinite refreshes
    @Override
    public void refreshGui(GuiInfo gui)
    {
        clear();
        OnlineGuideRepoList.INSTANCE.reload(this, true);
    }
}