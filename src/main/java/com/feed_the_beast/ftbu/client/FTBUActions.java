package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.api.gui.ISidebarButton;
import com.feed_the_beast.ftbl.api.gui.SidebarButton;
import com.feed_the_beast.ftbl.lib.SidebarButtonInst;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.gui.GuiHelper;
import com.feed_the_beast.ftbl.lib.gui.GuiIcons;
import com.feed_the_beast.ftbl.lib.math.MathHelperLM;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.gui.GuiClaimChunks;
import com.feed_the_beast.ftbu.gui.guide.local.InfoPageLocalGuideRepoList;
import com.feed_the_beast.ftbu.net.MessageRequestChunkData;
import com.feed_the_beast.ftbu.net.MessageRequestServerInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class FTBUActions
{
    @SidebarButton
    public static final ISidebarButton GUIDE = new SidebarButtonInst(new ResourceLocation(FTBUFinals.MOD_ID, "guide"), 0, GuiIcons.BOOK, new PropertyBool(true))
    {
        @Override
        public void onClicked(IMouseButton button)
        {
            GuiHelper.playClickSound();
            InfoPageLocalGuideRepoList.openGui();
        }
    };

    @SidebarButton
    public static final ISidebarButton SERVER_INFO = new SidebarButtonInst(new ResourceLocation(FTBUFinals.MOD_ID, "server_info"), 0, GuiIcons.BOOK_RED, new PropertyBool(true))
    {
        @Override
        public void onClicked(IMouseButton button)
        {
            Minecraft.getMinecraft().displayGuiScreen(new MessageRequestServerInfo().openGui());
        }

        @Override
        public boolean isVisible()
        {
            //FIXME: return FTBUWorldData.isLoadedW(ForgeWorldSP.inst);
            return true;
        }
    };

    @SidebarButton
    public static final ISidebarButton CLAIMED_CHUNKS = new SidebarButtonInst(new ResourceLocation(FTBUFinals.MOD_ID, "claimed_chunks"), 0, GuiIcons.MAP, new PropertyBool(true))
    {
        @Override
        public void onClicked(IMouseButton button)
        {
            EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
            new MessageRequestChunkData(MathHelperLM.chunk(ep.posX) - 7, MathHelperLM.chunk(ep.posZ) - 7, 15, 15);
            new GuiClaimChunks().openGui();
        }

        @Override
        public boolean isVisible()
        {
            //FIXME: return FTBUWorldData.isLoadedW(ForgeWorldSP.inst);
            return true;
        }
    };

    @SidebarButton
    public static final ISidebarButton TRASH_CAN = new SidebarButtonInst(new ResourceLocation(FTBUFinals.MOD_ID, "trash_can"), 0, GuiIcons.BIN, new PropertyBool(true))
    {
        @Override
        public void onClicked(IMouseButton button)
        {
            FTBLibClient.execClientCommand("/ftb trash_can", false);
        }
    };

    @SidebarButton
    public static final ISidebarButton SHOP = new SidebarButtonInst(new ResourceLocation(FTBUFinals.MOD_ID, "shop"), 0, GuiIcons.MONEY_BAG, new PropertyBool(true))
    {
        @Override
        public void onClicked(IMouseButton button)
        {
            FTBLibClient.execClientCommand("/ftb shop", false);
        }

        @Override
        public boolean isVisible()
        {
            return LMUtils.DEV_ENV;
        }
    };

    @SidebarButton
    public static final ISidebarButton HEAL = new SidebarButtonInst(new ResourceLocation(FTBUFinals.MOD_ID, "heal"), 200, GuiIcons.HEART, new PropertyBool(true))
    {
        @Override
        public void onClicked(IMouseButton button)
        {
            FTBLibClient.execClientCommand("/ftb heal", false);
        }
    };

    @SidebarButton
    public static final ISidebarButton TOGGLE_GAMEMODE = new SidebarButtonInst(new ResourceLocation(FTBUFinals.MOD_ID, "toggle.gamemode"), 195, GuiIcons.TOGGLE_GAMEMODE, new PropertyBool(true))
    {
        @Override
        public void onClicked(IMouseButton button)
        {
            int i = Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode ? 0 : 1;
            FTBLibClient.execClientCommand("/gamemode " + i, false);
        }
    };

    @SidebarButton
    public static final ISidebarButton TOGGLE_RAIN = new SidebarButtonInst(new ResourceLocation(FTBUFinals.MOD_ID, "toggle.rain"), 190, GuiIcons.TOGGLE_RAIN, new PropertyBool(true))
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
    };

    @SidebarButton
    public static final ISidebarButton TOGGLE_DAY = new SidebarButtonInst(new ResourceLocation(FTBUFinals.MOD_ID, "toggle.day"), 185, GuiIcons.TOGGLE_DAY, new PropertyBool(true))
    {
        @Override
        public void onClicked(IMouseButton button)
        {
            FTBLibClient.execClientCommand("/time set 6000", false);
        }
    };

    @SidebarButton
    public static final ISidebarButton TOGGLE_NIGHT = new SidebarButtonInst(new ResourceLocation(FTBUFinals.MOD_ID, "toggle.night"), 180, GuiIcons.TOGGLE_NIGHT, new PropertyBool(true))
    {
        @Override
        public void onClicked(IMouseButton button)
        {
            FTBLibClient.execClientCommand("/time set 18000", false);
        }
    };
}