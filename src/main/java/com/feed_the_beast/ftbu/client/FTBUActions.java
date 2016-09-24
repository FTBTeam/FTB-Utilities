package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.gui.GuiHelper;
import com.feed_the_beast.ftbl.api.gui.GuiIcons;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.api.gui.ISidebarButton;
import com.feed_the_beast.ftbl.api.gui.SidebarButton;
import com.feed_the_beast.ftbl.api_impl.SidebarButtonInst;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.gui.GuiClaimChunks;
import com.feed_the_beast.ftbu.gui.guide.local.InfoPageLocalGuideRepoList;
import com.feed_the_beast.ftbu.net.MessageRequestChunkData;
import com.feed_the_beast.ftbu.net.MessageRequestServerInfo;
import com.latmod.lib.EnumEnabled;
import com.latmod.lib.math.MathHelperLM;
import com.latmod.lib.util.LMUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class FTBUActions
{
    @SidebarButton
    public static final ISidebarButton GUIDE = new SidebarButtonInst(new ResourceLocation(FTBUFinals.MOD_ID, "guide"), 0, GuiIcons.BOOK, EnumEnabled.ENABLED)
    {
        @Override
        public void onClicked(IMouseButton button)
        {
            GuiHelper.playClickSound();
            InfoPageLocalGuideRepoList.openGui();
        }
    };

    @SidebarButton
    public static final ISidebarButton SERVER_INFO = new SidebarButtonInst(new ResourceLocation(FTBUFinals.MOD_ID, "server_info"), 0, GuiIcons.BOOK_RED, EnumEnabled.ENABLED)
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
    public static final ISidebarButton CLAIMED_CHUNKS = new SidebarButtonInst(new ResourceLocation(FTBUFinals.MOD_ID, "claimed_chunks"), 0, GuiIcons.MAP, EnumEnabled.ENABLED)
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
    public static final ISidebarButton TRASH_CAN = new SidebarButtonInst(new ResourceLocation(FTBUFinals.MOD_ID, "trash_can"), 0, GuiIcons.BIN, EnumEnabled.ENABLED)
    {
        @Override
        public void onClicked(IMouseButton button)
        {
            FTBLibClient.execClientCommand("/ftb trash_can", false);
        }
    };

    @SidebarButton
    public static final ISidebarButton SHOP = new SidebarButtonInst(new ResourceLocation(FTBUFinals.MOD_ID, "shop"), 0, GuiIcons.MONEY_BAG, EnumEnabled.ENABLED)
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
}