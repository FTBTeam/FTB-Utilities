package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.api.gui.ISidebarButton;
import com.feed_the_beast.ftbl.lib.SidebarButton;
import com.feed_the_beast.ftbl.lib.client.FTBLibClient;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.gui.GuiHelper;
import com.feed_the_beast.ftbl.lib.gui.GuiIcons;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiLoading;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.gui.GuiClaimedChunks;
import com.feed_the_beast.ftbu.gui.guide.Guides;
import com.feed_the_beast.ftbu.net.MessageRequestServerInfo;
import net.minecraft.client.Minecraft;

public class FTBUActions
{
    public static final ISidebarButton GUIDE = new SidebarButton(0, GuiIcons.BOOK, new PropertyBool(true))
    {
        @Override
        public void onClicked(IMouseButton button)
        {
            GuiHelper.playClickSound();
            Guides.openGui();
        }
    };

    public static final ISidebarButton SERVER_INFO = new SidebarButton(0, GuiIcons.BOOK_RED, new PropertyBool(true))
    {
        @Override
        public void onClicked(IMouseButton button)
        {
            new GuiLoading().openGui();
            new MessageRequestServerInfo().sendToServer();
        }

        @Override
        public boolean isVisible()
        {
            return FTBLibIntegration.API.getClientData().hasOptionalServerMod(FTBUFinals.MOD_ID);
        }
    };

    public static final ISidebarButton CLAIMED_CHUNKS = new SidebarButton(0, GuiIcons.MAP, new PropertyBool(true))
    {
        @Override
        public void onClicked(IMouseButton button)
        {
            GuiClaimedChunks.instance = new GuiClaimedChunks();
            GuiClaimedChunks.instance.openGui();
        }

        @Override
        public boolean isVisible()
        {
            return FTBLibIntegration.API.getClientData().hasOptionalServerMod(FTBUFinals.MOD_ID);
        }
    };

    public static final ISidebarButton TRASH_CAN = new SidebarButton(0, GuiIcons.BIN, new PropertyBool(true))
    {
        @Override
        public void onClicked(IMouseButton button)
        {
            FTBLibClient.execClientCommand("/ftb trash_can", false);
        }

        @Override
        public boolean isVisible()
        {
            return FTBLibIntegration.API.getClientData().hasOptionalServerMod(FTBUFinals.MOD_ID);
        }
    };

    public static final ISidebarButton SHOP = new SidebarButton(0, GuiIcons.MONEY_BAG, new PropertyBool(true))
    {
        @Override
        public void onClicked(IMouseButton button)
        {
            FTBLibClient.execClientCommand("/ftb shop", false);
        }

        @Override
        public boolean isVisible()
        {
            return LMUtils.DEV_ENV && FTBLibIntegration.API.getClientData().hasOptionalServerMod(FTBUFinals.MOD_ID);
        }
    };

    public static final ISidebarButton HEAL = new SidebarButton(200, GuiIcons.HEART, new PropertyBool(true))
    {
        @Override
        public void onClicked(IMouseButton button)
        {
            FTBLibClient.execClientCommand("/ftb heal", false);
        }

        @Override
        public boolean isVisible()
        {
            return FTBLibIntegration.API.getClientData().isClientOP() && FTBLibIntegration.API.getClientData().hasOptionalServerMod(FTBUFinals.MOD_ID);
        }
    };

    public static final ISidebarButton TOGGLE_GAMEMODE = new SidebarButton(195, GuiIcons.TOGGLE_GAMEMODE, new PropertyBool(true))
    {
        @Override
        public void onClicked(IMouseButton button)
        {
            int i = Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode ? 0 : 1;
            FTBLibClient.execClientCommand("/gamemode " + i, false);
        }

        @Override
        public boolean isVisible()
        {
            return FTBLibIntegration.API.getClientData().isClientOP();
        }
    };

    public static final ISidebarButton TOGGLE_RAIN = new SidebarButton(190, GuiIcons.TOGGLE_RAIN, new PropertyBool(true))
    {
        @Override
        public void onClicked(IMouseButton button)
        {
            if(button.isLeft())
            {
                FTBLibClient.execClientCommand("/toggledownfall", false);
            }
            else
            {
                FTBLibClient.execClientCommand("/weather clear 1000000", false);
            }
        }

        @Override
        public boolean isVisible()
        {
            return FTBLibIntegration.API.getClientData().isClientOP();
        }
    };

    public static final ISidebarButton TOGGLE_DAY = new SidebarButton(185, GuiIcons.TOGGLE_DAY, new PropertyBool(true))
    {
        @Override
        public void onClicked(IMouseButton button)
        {
            FTBLibClient.execClientCommand("/time set 6000", false);
        }

        @Override
        public boolean isVisible()
        {
            return FTBLibIntegration.API.getClientData().isClientOP();
        }
    };

    public static final ISidebarButton TOGGLE_NIGHT = new SidebarButton(180, GuiIcons.TOGGLE_NIGHT, new PropertyBool(true))
    {
        @Override
        public void onClicked(IMouseButton button)
        {
            FTBLibClient.execClientCommand("/time set 18000", false);
        }

        @Override
        public boolean isVisible()
        {
            return FTBLibIntegration.API.getClientData().isClientOP();
        }
    };
}