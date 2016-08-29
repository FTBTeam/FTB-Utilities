package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.gui.GuiIcons;
import com.feed_the_beast.ftbl.api.gui.GuiLM;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.api_impl.SidebarButton;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.gui.GuiClaimChunks;
import com.feed_the_beast.ftbu.gui.guide.local.InfoPageLocalGuideRepoList;
import com.feed_the_beast.ftbu.net.MessageRequestServerInfo;
import com.latmod.lib.EnumEnabled;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FTBUActions
{
    @SideOnly(Side.CLIENT)
    public static void init()
    {
        FTBLibAPI.get().getRegistries().sidebarButtons().register(new ResourceLocation(FTBUFinals.MOD_ID, "guide"), new SidebarButton(0, GuiIcons.BOOK, EnumEnabled.ENABLED)
        {
            @Override
            @SideOnly(Side.CLIENT)
            public void onClicked(IMouseButton button)
            {
                GuiLM.playClickSound();
                InfoPageLocalGuideRepoList.openGui();
            }
        });

        FTBLibAPI.get().getRegistries().sidebarButtons().register(new ResourceLocation(FTBUFinals.MOD_ID, "server_info"), new SidebarButton(0, GuiIcons.BOOK_RED, EnumEnabled.ENABLED)
        {
            @Override
            @SideOnly(Side.CLIENT)
            public void onClicked(IMouseButton button)
            {
                Minecraft.getMinecraft().displayGuiScreen(new MessageRequestServerInfo().openGui());
            }

            @Override
            @SideOnly(Side.CLIENT)
            public boolean isVisible()
            {
                //FIXME: return FTBUWorldData.isLoadedW(ForgeWorldSP.inst);
                return true;
            }
        });

        FTBLibAPI.get().getRegistries().sidebarButtons().register(new ResourceLocation(FTBUFinals.MOD_ID, "claimed_chunks"), new SidebarButton(0, GuiIcons.MAP, EnumEnabled.ENABLED)
        {
            @Override
            @SideOnly(Side.CLIENT)
            public void onClicked(IMouseButton button)
            {
                new GuiClaimChunks().openGui();
            }

            @Override
            @SideOnly(Side.CLIENT)
            public boolean isVisible()
            {
                //FIXME: return FTBUWorldData.isLoadedW(ForgeWorldSP.inst);
                return true;
            }
        });

        FTBLibAPI.get().getRegistries().sidebarButtons().register(new ResourceLocation(FTBUFinals.MOD_ID, "trash_can"), new SidebarButton(0, GuiIcons.BIN, EnumEnabled.ENABLED)
        {
            @Override
            @SideOnly(Side.CLIENT)
            public void onClicked(IMouseButton button)
            {
                FTBLibClient.execClientCommand("/ftb trash_can", false);
            }
        });

        FTBLibAPI.get().getRegistries().sidebarButtons().register(new ResourceLocation(FTBUFinals.MOD_ID, "shop"), new SidebarButton(0, GuiIcons.MONEY_BAG, EnumEnabled.ENABLED)
        {
            @Override
            @SideOnly(Side.CLIENT)
            public void onClicked(IMouseButton button)
            {
                FTBLibClient.execClientCommand("/ftb shop", false);
            }

            @Override
            @SideOnly(Side.CLIENT)
            public boolean isVisible()
            {
                return FTBLib.DEV_ENV;
            }
        });

        /*FTBLibAPI.get().getRegistries().sidebarButtons().register(new ResourceLocation(FTBUFinals.MOD_ID, "mail"), new PlayerAction(0, GuiIcons.feather)
        {
            @Override
            @SideOnly(Side.CLIENT)
            public void onClicked(ForgePlayerSP player)
            {
            }

            @Override
            @SideOnly(Side.CLIENT)
            public boolean isVisibleFor(ForgePlayerSP player)
            {
                return FTBLib.DEV_ENV && super.isVisibleFor(player);
            }
        });*/
    }
}