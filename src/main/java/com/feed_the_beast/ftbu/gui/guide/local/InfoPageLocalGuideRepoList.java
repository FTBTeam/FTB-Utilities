package com.feed_the_beast.ftbu.gui.guide.local;

import com.feed_the_beast.ftbl.api.gui.GuiIcons;
import com.feed_the_beast.ftbl.api.gui.GuiLM;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.api.gui.widgets.ButtonLM;
import com.feed_the_beast.ftbl.api.info.impl.InfoPage;
import com.feed_the_beast.ftbl.gui.GuiInfo;
import com.feed_the_beast.ftbl.gui.GuiLoading;
import com.feed_the_beast.ftbu.gui.guide.online.InfoPageOnlineGuideRepoList;
import com.feed_the_beast.ftbu.gui.guide.online.OnlineGuideRepoList;
import net.minecraft.client.renderer.GlStateManager;
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
            GuiLM.playClickSound();
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
    public ButtonLM createSpecialButton(GuiInfo gui)
    {
        return new ButtonLM(0, 0, 16, 16, "Browse Guides") //TODO: Lang
        {
            @Override
            public void onClicked(GuiLM gui, IMouseButton b)
            {
                GuiLM.playClickSound();
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

            @Override
            public void renderWidget(GuiLM gui)
            {
                GlStateManager.enableTexture2D();
                GlStateManager.color(1F, 1F, 1F, 1F);
                render(GuiIcons.GLOBE);
            }
        };
    }
}