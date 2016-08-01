package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbl.api.ForgePlayerSP;
import com.feed_the_beast.ftbl.api.ForgeWorldSP;
import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.client.gui.GuiIcons;
import com.feed_the_beast.ftbl.api.client.gui.GuiLM;
import com.feed_the_beast.ftbl.api.client.gui.GuiScreenRegistry;
import com.feed_the_beast.ftbl.api.client.gui.guibuttons.ActionButtonRegistry;
import com.feed_the_beast.ftbl.api.client.gui.guibuttons.SidebarButton;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.gui.GuiClaimChunks;
import com.feed_the_beast.ftbu.gui.guide.ClientGuideFile;
import com.feed_the_beast.ftbu.net.MessageRequestServerInfo;
import com.feed_the_beast.ftbu.world.data.FTBUWorldData;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FTBUActions
{
    @SideOnly(Side.CLIENT)
    public static void init()
    {
        ActionButtonRegistry.add(new ResourceLocation(FTBUFinals.MOD_ID, "guide"), new SidebarButton(0, GuiIcons.book, true)
        {
            @Override
            @SideOnly(Side.CLIENT)
            public void onClicked(ForgePlayerSP player)
            {
                GuiLM.playClickSound();
                ClientGuideFile.openClientGui(true);
            }

            @Override
            @SideOnly(Side.CLIENT)
            public boolean isVisibleFor(ForgePlayerSP player)
            {
                return super.isVisibleFor(player);
            }
        });

        ActionButtonRegistry.add(new ResourceLocation(FTBUFinals.MOD_ID, "server_info"), new SidebarButton(0, GuiIcons.book_red, true)
        {
            @Override
            @SideOnly(Side.CLIENT)
            public void onClicked(ForgePlayerSP player)
            {
                Minecraft.getMinecraft().displayGuiScreen(new MessageRequestServerInfo().openGui());
            }

            @Override
            @SideOnly(Side.CLIENT)
            public boolean isVisibleFor(ForgePlayerSP player)
            {
                return super.isVisibleFor(player) && FTBUWorldData.isLoadedW(ForgeWorldSP.inst);
            }
        });

        ActionButtonRegistry.add(new ResourceLocation(FTBUFinals.MOD_ID, "claimed_chunks"), new SidebarButton(0, GuiIcons.map, true)
        {
            @Override
            @SideOnly(Side.CLIENT)
            public void onClicked(ForgePlayerSP player)
            {
                new GuiClaimChunks().openGui();
            }

            @Override
            @SideOnly(Side.CLIENT)
            public boolean isVisibleFor(ForgePlayerSP player)
            {
                return super.isVisibleFor(player) && FTBUWorldData.isLoadedW(ForgeWorldSP.inst);
            }
        });

        ActionButtonRegistry.add(new ResourceLocation(FTBUFinals.MOD_ID, "trash_can"), new SidebarButton(0, GuiIcons.bin, true)
        {
            @Override
            @SideOnly(Side.CLIENT)
            public void onClicked(ForgePlayerSP player)
            {
                FTBLibClient.execClientCommand("/ftb trash_can", false);
            }
        });

        ActionButtonRegistry.add(new ResourceLocation(FTBUFinals.MOD_ID, "shop"), new SidebarButton(0, GuiIcons.money_bag, true)
        {
            @Override
            @SideOnly(Side.CLIENT)
            public void onClicked(ForgePlayerSP player)
            {
                FTBLibClient.execClientCommand("/ftb shop", false);
            }

            @Override
            @SideOnly(Side.CLIENT)
            public boolean isVisibleFor(ForgePlayerSP player)
            {
                return FTBLib.DEV_ENV && super.isVisibleFor(player);
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