package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbl.api.ForgePlayerSP;
import com.feed_the_beast.ftbl.api.ForgeWorldSP;
import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.client.gui.GuiIcons;
import com.feed_the_beast.ftbl.api.client.gui.GuiScreenRegistry;
import com.feed_the_beast.ftbl.api.client.gui.guibuttons.ActionButton;
import com.feed_the_beast.ftbl.api.client.gui.guibuttons.ActionButtonRegistry;
import com.feed_the_beast.ftbl.api.client.gui.guibuttons.PlayerAction;
import com.feed_the_beast.ftbl.api.client.gui.guibuttons.SidebarButton;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.gui.GuiClaimChunks;
import com.feed_the_beast.ftbu.gui.guide.ClientGuideFile;
import com.feed_the_beast.ftbu.net.MessageRequestServerInfo;
import com.feed_the_beast.ftbu.world.FTBUWorldData;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FTBUActions
{
    public static final ActionButton GUIDE = new SidebarButton(new ResourceLocation(FTBUFinals.MOD_ID, "guide"), 0, GuiIcons.book, true)
    {
        @Override
        @SideOnly(Side.CLIENT)
        public void onClicked(ForgePlayerSP player)
        {
            FTBLibClient.playClickSound();
            ClientGuideFile.openClientGui(true);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public boolean isVisibleFor(ForgePlayerSP player)
        {
            return super.isVisibleFor(player);
        }
    };

    // Self //
    public static final ActionButton SERVER_INFO = new SidebarButton(new ResourceLocation(FTBUFinals.MOD_ID, "server_info"), 0, GuiIcons.book_red, true)
    {
        @Override
        @SideOnly(Side.CLIENT)
        public void onClicked(ForgePlayerSP player)
        {
            FTBLibClient.mc().displayGuiScreen(new MessageRequestServerInfo().openGui());
        }

        @Override
        @SideOnly(Side.CLIENT)
        public boolean isVisibleFor(ForgePlayerSP player)
        {
            return super.isVisibleFor(player) && FTBUWorldData.isLoadedW(ForgeWorldSP.inst);
        }
    };

    public static final ActionButton CLAIMS = new SidebarButton(new ResourceLocation(FTBUFinals.MOD_ID, "claimed_chunks"), 0, GuiIcons.map, true)
    {
        @Override
        @SideOnly(Side.CLIENT)
        public void onClicked(ForgePlayerSP player)
        {
            new GuiClaimChunks(0L).openGui();
        }

        @Override
        @SideOnly(Side.CLIENT)
        public boolean isVisibleFor(ForgePlayerSP player)
        {
            return super.isVisibleFor(player) && FTBUWorldData.isLoadedW(ForgeWorldSP.inst);
        }
    };

    public static final ActionButton TRASH_CAN = new SidebarButton(new ResourceLocation(FTBUFinals.MOD_ID, "trash_can"), 0, GuiIcons.bin, true)
    {
        @Override
        @SideOnly(Side.CLIENT)
        public void onClicked(ForgePlayerSP player)
        {
            FTBLibClient.execClientCommand("/ftb trash_can", false);
        }
    };

    public static final ActionButton SHOP = new SidebarButton(new ResourceLocation(FTBUFinals.MOD_ID, "shop"), 0, GuiIcons.money_bag, true)
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
    };

    public static final ActionButton MAIL = new PlayerAction(new ResourceLocation(FTBUFinals.MOD_ID, "mail"), 0, GuiIcons.feather)
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
    };

    // Other //

    @SideOnly(Side.CLIENT)
    public static void init()
    {
        ActionButtonRegistry.add(GUIDE);
        ActionButtonRegistry.add(SERVER_INFO);
        ActionButtonRegistry.add(CLAIMS);
        ActionButtonRegistry.add(TRASH_CAN);
        ActionButtonRegistry.add(SHOP);

        GuiScreenRegistry.register(new ResourceLocation(FTBUFinals.MOD_ID, "claimed_chunks"), () -> new GuiClaimChunks(0L).getWrapper());
        GuiScreenRegistry.register(new ResourceLocation(FTBUFinals.MOD_ID, "guide"), () -> ClientGuideFile.openClientGui(false));
        GuiScreenRegistry.register(new ResourceLocation(FTBUFinals.MOD_ID, "server_info"), () -> new MessageRequestServerInfo().openGui());
        //GuiScreenRegistry.register(new ResourceLocation(FTBUFinals.MOD_ID, "shop"), () -> new GuiShop());
    }
}