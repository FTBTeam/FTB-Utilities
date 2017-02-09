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
import com.feed_the_beast.ftbu.gui.Guides;
import com.feed_the_beast.ftbu.net.MessageRequestServerInfo;
import net.minecraft.client.Minecraft;

public class FTBUActions
{
    public static final ISidebarButton GUIDE = new SidebarButton(FTBUFinals.get("guide"), GuiIcons.BOOK, new PropertyBool(true), "after:ftbu.claimed_chunks")
    {
        @Override
        public void onClicked(IMouseButton button)
        {
            GuiHelper.playClickSound();
            Guides.openGui();
        }
    };

    public static final ISidebarButton SERVER_INFO = new SidebarButton(FTBUFinals.get("server_info"), GuiIcons.BOOK_RED, new PropertyBool(true), "after:ftbu.guide")
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

    public static final ISidebarButton CLAIMED_CHUNKS = new SidebarButton(FTBUFinals.get("claimed_chunks"), GuiIcons.MAP, new PropertyBool(true), "after:ftbu.toggle.night")
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

    public static final ISidebarButton TRASH_CAN = new SidebarButton(FTBUFinals.get("trash_can"), GuiIcons.BIN, new PropertyBool(true), "after:ftbu.server_info")
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

    public static final ISidebarButton SHOP = new SidebarButton(FTBUFinals.get("shop"), GuiIcons.MONEY_BAG, new PropertyBool(true), "after:ftbu.trash_can")
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

    public static final ISidebarButton HEAL = new SidebarButton(FTBUFinals.get("heal"), GuiIcons.HEART, new PropertyBool(true), "after:ftbl.my_server_settings")
    {
        @Override
        public void onClicked(IMouseButton button)
        {
            FTBLibClient.execClientCommand("/heal", false);
        }

        @Override
        public boolean isVisible()
        {
            return FTBLibIntegration.API.getClientData().isClientOP() && FTBLibIntegration.API.getClientData().hasOptionalServerMod(FTBUFinals.MOD_ID);
        }
    };

    public static final ISidebarButton TOGGLE_GAMEMODE = new SidebarButton(FTBUFinals.get("toggle.gamemode"), GuiIcons.TOGGLE_GAMEMODE, new PropertyBool(true), "after:ftbu.heal")
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

    public static final ISidebarButton TOGGLE_RAIN = new SidebarButton(FTBUFinals.get("toggle.rain"), GuiIcons.TOGGLE_RAIN, new PropertyBool(true), "after:ftbu.toggle.gamemode")
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

    public static final ISidebarButton TOGGLE_DAY = new SidebarButton(FTBUFinals.get("toggle.day"), GuiIcons.TOGGLE_DAY, new PropertyBool(true), "after:ftbu.toggle.rain")
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

    public static final ISidebarButton TOGGLE_NIGHT = new SidebarButton(FTBUFinals.get("toggle.night"), GuiIcons.TOGGLE_NIGHT, new PropertyBool(true), "after:ftbu.toggle.day")
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