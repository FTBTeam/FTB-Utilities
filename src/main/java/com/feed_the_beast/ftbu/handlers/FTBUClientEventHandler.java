package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.api.IFTBLibClientRegistry;
import com.feed_the_beast.ftbl.api.events.FTBLibClientRegistryEvent;
import com.feed_the_beast.ftbl.api.events.RegisterFTBClientCommandsEvent;
import com.feed_the_beast.ftbl.lib.client.ClientUtils;
import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.client.FTBUClient;
import com.feed_the_beast.ftbu.client.FTBUClientConfig;
import com.feed_the_beast.ftbu.gui.GuiWarps;
import com.feed_the_beast.ftbu.gui.Guides;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
@EventHandler(Side.CLIENT)
public class FTBUClientEventHandler
{
	@SubscribeEvent
	public static void registerClient(FTBLibClientRegistryEvent event)
	{
		IFTBLibClientRegistry reg = event.getRegistry();
		reg.addClientConfig(FTBUFinals.MOD_ID, "render_badges", FTBUClientConfig.RENDER_BADGES);
		reg.addClientConfig(FTBUFinals.MOD_ID, "journeymap_overlay", FTBUClientConfig.JOURNEYMAP_OVERLAY);
	}

	@SubscribeEvent
	public static void registerFTBClientCommands(RegisterFTBClientCommandsEvent event)
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
				ClientUtils.execClientCommand("/gamemode " + (ClientUtils.MC.player.capabilities.isCreativeMode ? "survival" : "creative"));
			}
		});
	}
	
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
		if (FTBUClient.KEY_GUIDE.isPressed())
		{
			Guides.openGui();
		}

		if (FTBUClient.KEY_WARP.isPressed())
		{
			GuiWarps.INSTANCE = new GuiWarps();
			GuiWarps.INSTANCE.openGui();
			ClientUtils.execClientCommand("/ftb warp gui");
		}
	}
}