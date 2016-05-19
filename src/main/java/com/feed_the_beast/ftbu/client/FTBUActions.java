package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbl.api.EnumSelf;
import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.PlayerAction;
import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.gui.GuiIcons;
import com.feed_the_beast.ftbl.api.gui.GuiScreenRegistry;
import com.feed_the_beast.ftbl.api.gui.PlayerActionRegistry;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.api.guide.ClientGuideFile;
import com.feed_the_beast.ftbu.client.gui.claims.GuiClaimChunks;
import com.feed_the_beast.ftbu.net.MessageRequestServerInfo;
import com.feed_the_beast.ftbu.world.FTBUWorldDataSP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FTBUActions
{
    public static final PlayerAction GUIDE = PlayerActionRegistry.add(new PlayerAction(EnumSelf.SELF, new ResourceLocation(FTBUFinals.MOD_ID, "guide"), 0, GuiIcons.book)
    {
        @Override
        public void onClicked(ForgePlayer self, ForgePlayer other)
        {
            FTBLibClient.playClickSound();
            ClientGuideFile.openClientGui(true);
        }

        @Override
        public boolean isVisibleFor(ForgePlayer self, ForgePlayer other)
        { return FTBUWorldDataSP.isLoaded(); }

        @Override
        public Boolean configDefault()
        { return Boolean.TRUE; }
    });

    // Self //
    public static final PlayerAction SERVER_INFO = PlayerActionRegistry.add(new PlayerAction(EnumSelf.SELF, new ResourceLocation(FTBUFinals.MOD_ID, "server_info"), 0, GuiIcons.book_red)
    {
        @Override
        public void onClicked(ForgePlayer self, ForgePlayer other)
        { new MessageRequestServerInfo().sendToServer(); }

        @Override
        public boolean isVisibleFor(ForgePlayer self, ForgePlayer other)
        { return FTBUWorldDataSP.isLoaded(); }

        @Override
        public Boolean configDefault()
        { return Boolean.TRUE; }
    });
    public static final PlayerAction CLAIMS = PlayerActionRegistry.add(new PlayerAction(EnumSelf.SELF, new ResourceLocation(FTBUFinals.MOD_ID, "claimed_chunks"), 0, GuiIcons.map)
    {
        @Override
        public void onClicked(ForgePlayer self, ForgePlayer other)
        { FTBLibClient.openGui(new GuiClaimChunks(0L)); }

        @Override
        public boolean isVisibleFor(ForgePlayer self, ForgePlayer other)
        { return FTBUWorldDataSP.isLoaded(); }

        @Override
        public Boolean configDefault()
        { return Boolean.TRUE; }
    });
    public static final PlayerAction TRADE = PlayerActionRegistry.add(new PlayerAction(EnumSelf.SELF, new ResourceLocation(FTBUFinals.MOD_ID, "trade"), 0, GuiIcons.money_bag)
    {
        @Override
        public void onClicked(ForgePlayer owner, ForgePlayer player)
        {
        }

        @Override
        public boolean isVisibleFor(ForgePlayer self, ForgePlayer other)
        { return FTBLib.DEV_ENV; }

        @Override
        public Boolean configDefault()
        { return Boolean.TRUE; }
    });
    public static final PlayerAction MAIL = PlayerActionRegistry.add(new PlayerAction(EnumSelf.OTHER, new ResourceLocation(FTBUFinals.MOD_ID, "mail"), 0, GuiIcons.feather)
    {
        @Override
        public void onClicked(ForgePlayer self, ForgePlayer other)
        {
        }

        @Override
        public boolean isVisibleFor(ForgePlayer self, ForgePlayer other)
        { return FTBLib.DEV_ENV; }
    });

    // Other //

    @SideOnly(Side.CLIENT)
    public static void init()
    {
        GuiScreenRegistry.register(new ResourceLocation(FTBUFinals.MOD_ID, "claimed_chunks"), ep -> new GuiClaimChunks(0L));
        GuiScreenRegistry.register(new ResourceLocation(FTBUFinals.MOD_ID, "guide"), ep -> ClientGuideFile.openClientGui(false));
        GuiScreenRegistry.register(new ResourceLocation(FTBUFinals.MOD_ID, "server_info"), ep -> {
            new MessageRequestServerInfo().sendToServer();
            return null;
        });

        //GuiScreenRegistry.register(new ResourceLocation("ftbu", "trade"), ep -> FTBLibClient.mc.currentScreen);
    }
}