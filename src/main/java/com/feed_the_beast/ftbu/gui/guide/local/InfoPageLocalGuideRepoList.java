package com.feed_the_beast.ftbu.gui.guide.local;

import com.feed_the_beast.ftbl.api.gui.GuiIcons;
import com.feed_the_beast.ftbl.api.gui.GuiLM;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.api.gui.widgets.ButtonLM;
import com.feed_the_beast.ftbl.api.info.impl.InfoPage;
import com.feed_the_beast.ftbl.gui.GuiInfo;
import com.feed_the_beast.ftbu.gui.guide.online.InfoPageOnlineGuideRepoList;
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

    public static GuiInfo getGui()
    {
        if(cachedGui == null)
        {
            cachedGui = new GuiInfo(new InfoPageLocalGuideRepoList());
        }

        return cachedGui;
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
                new GuiInfo(new InfoPageOnlineGuideRepoList()).openGui();
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

    @Override
    public void refreshGui(GuiInfo gui)
    {
        clear();
        LocalGuideRepoList.INSTANCE.reload(this, true);
    }
}