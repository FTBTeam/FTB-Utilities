package com.feed_the_beast.ftbu.gui.guide;

import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.api.info.ISpecialInfoButton;
import com.feed_the_beast.ftbl.gui.GuiInfo;
import com.feed_the_beast.ftbl.lib.gui.GuiHelper;
import com.feed_the_beast.ftbl.lib.gui.GuiIcons;
import com.feed_the_beast.ftbl.lib.gui.GuiLang;
import com.feed_the_beast.ftbl.lib.info.InfoPage;
import com.feed_the_beast.ftbl.lib.info.SpecialInfoButton;

public class InfoPageGuides extends InfoPage
{
    InfoPageGuides()
    {
        super("guides");
    }

    @Override
    public ISpecialInfoButton createSpecialButton(GuiInfo gui)
    {
        return new SpecialInfoButton(GuiLang.BUTTON_REFRESH.translate(), GuiIcons.REFRESH)
        {
            @Override
            public void onClicked(IMouseButton b)
            {
                GuiHelper.playClickSound();
                Guides.setShouldReload();
                Guides.openGui();
            }
        };
    }
}