package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.events.RegisterFTBClientCommandsEvent;
import com.feed_the_beast.ftbl.lib.client.FTBLibClient;
import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbu.client.FTBUClient;
import com.feed_the_beast.ftbu.gui.GuiWarps;
import com.feed_the_beast.ftbu.gui.Guides;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class FTBUClientEventHandler
{
    /*
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent e)
    {
        if(FTBUConfigGeneral.isItemBanned(item, e.itemStack.getItemDamage()))
        {
            e.toolTip.add(EnumChatFormatting.RED + "Banned item");
        }
    }
    */

    @SubscribeEvent
    public static void onKeyEvent(InputEvent.KeyInputEvent event)
    {
        if(FTBUClient.KEY_GUIDE.isPressed())
        {
            Guides.openGui();
        }

        if(FTBUClient.KEY_WARP.isPressed())
        {
            GuiWarps.INSTANCE = new GuiWarps();
            GuiWarps.INSTANCE.openGui();
            FTBLibClient.execClientCommand("/ftb warps gui");
        }
    }

    @SubscribeEvent
    public static void onClientCommand(RegisterFTBClientCommandsEvent event)
    {
        event.add(new CmdBase("refresh_guide", CmdBase.Level.ALL)
        {
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
            {
                Guides.refresh();
            }
        });

        event.add(new CmdBase("open_guide", CmdBase.Level.ALL)
        {
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
            {
                Guides.openGui();
            }
        });

        event.add(new CmdBase("toggle_gamemode", CmdBase.Level.ALL)
        {
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
            {
                FTBLibClient.execClientCommand("/gamemode " + (Minecraft.getMinecraft().player.capabilities.isCreativeMode ? 0 : 1));
            }
        });
    }
}