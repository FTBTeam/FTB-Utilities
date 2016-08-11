package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.gui.GuiIcons;
import com.feed_the_beast.ftbl.api.gui.GuiLM;
import com.feed_the_beast.ftbl.api.gui.GuiScreenRegistry;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.api.gui.guibuttons.SidebarButton;
import com.feed_the_beast.ftbl.api.gui.guibuttons.SidebarButtonRegistry;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.gui.GuiClaimChunks;
import com.feed_the_beast.ftbu.gui.guide.ClientGuideFile;
import com.feed_the_beast.ftbu.net.MessageRequestServerInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FTBUActions
{
    @SideOnly(Side.CLIENT)
    public static void init()
    {
        SidebarButtonRegistry.add(new ResourceLocation(FTBUFinals.MOD_ID, "guide"), new SidebarButton(0, GuiIcons.book, true)
        {
            @Override
            @SideOnly(Side.CLIENT)
            public void onClicked(IMouseButton button)
            {
                GuiLM.playClickSound();
                ClientGuideFile.openClientGui(true);
            }
        });

        SidebarButtonRegistry.add(new ResourceLocation(FTBUFinals.MOD_ID, "server_info"), new SidebarButton(0, GuiIcons.book_red, true)
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

        SidebarButtonRegistry.add(new ResourceLocation(FTBUFinals.MOD_ID, "claimed_chunks"), new SidebarButton(0, GuiIcons.map, true)
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

        SidebarButtonRegistry.add(new ResourceLocation(FTBUFinals.MOD_ID, "trash_can"), new SidebarButton(0, GuiIcons.bin, true)
        {
            @Override
            @SideOnly(Side.CLIENT)
            public void onClicked(IMouseButton button)
            {
                FTBLibClient.execClientCommand("/ftb trash_can", false);
            }
        });

        SidebarButtonRegistry.add(new ResourceLocation(FTBUFinals.MOD_ID, "shop"), new SidebarButton(0, GuiIcons.money_bag, true)
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

        /*ActionButtonRegistry.add(new ResourceLocation(FTBUFinals.MOD_ID, "mail"), new PlayerAction(0, GuiIcons.feather)
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

        GuiScreenRegistry.register(new ResourceLocation(FTBUFinals.MOD_ID, "claimed_chunks"), () -> new GuiClaimChunks().getWrapper());
        GuiScreenRegistry.register(new ResourceLocation(FTBUFinals.MOD_ID, "guide"), () -> ClientGuideFile.openClientGui(false));
        GuiScreenRegistry.register(new ResourceLocation(FTBUFinals.MOD_ID, "server_info"), () -> new MessageRequestServerInfo().openGui());
        //GuiScreenRegistry.register(new ResourceLocation(FTBUFinals.MOD_ID, "shop"), () -> new GuiShop());
    }
}