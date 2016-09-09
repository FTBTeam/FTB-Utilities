package com.feed_the_beast.ftbu.gui.guide.local;

import com.feed_the_beast.ftbl.api.gui.GuiHelper;
import com.feed_the_beast.ftbl.api.gui.GuiIcons;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.api.info.ISpecialInfoButton;
import com.feed_the_beast.ftbl.api.info.impl.InfoPage;
import com.feed_the_beast.ftbl.api.info.impl.SpecialInfoButton;
import com.feed_the_beast.ftbl.gui.GuiInfo;
import com.feed_the_beast.ftbl.gui.GuiLoading;
import com.feed_the_beast.ftbu.gui.guide.online.InfoPageOnlineGuideRepoList;
import com.feed_the_beast.ftbu.gui.guide.online.OnlineGuideRepoList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class InfoPageLocalGuideRepoList extends InfoPage
{
    private static GuiInfo cachedGui = null;

    private InfoPageLocalGuideRepoList()
    {
        super("local_guide_repo_list");
    }

    public static void openGui()
    {
        cachedGui = null;

        if(cachedGui == null)
        {
            GuiHelper.playClickSound();
            new GuiLoading()
            {
                private InfoPageLocalGuideRepoList infoPage;

                @Override
                public void startLoading()
                {
                    infoPage = new InfoPageLocalGuideRepoList();
                    LocalGuideRepoList.INSTANCE.reload(infoPage, this);
                }

                @Override
                public void finishLoading()
                {
                    new GuiInfo(infoPage).openGui();
                }
            }.openGui();

            cachedGui = new GuiInfo(new InfoPageLocalGuideRepoList());
        }
        else
        {
            cachedGui.openGui();
        }
    }

    @Override
    public ISpecialInfoButton createSpecialButton(GuiInfo gui)
    {
        return new SpecialInfoButton("Browse Guides", GuiIcons.GLOBE) //TODO: Lang
        {
            @Override
            public void onClicked(IMouseButton b)
            {
                GuiHelper.playClickSound();
                new GuiLoading()
                {
                    private InfoPageOnlineGuideRepoList infoPage;

                    @Override
                    public void startLoading()
                    {
                        infoPage = new InfoPageOnlineGuideRepoList();
                        OnlineGuideRepoList.INSTANCE.reload(infoPage, this);
                    }

                    @Override
                    public void finishLoading()
                    {
                        new GuiInfo(infoPage).openGui();
                    }
                }.openGui();
            }
        };
    }
}