package com.feed_the_beast.ftbutilities.command.client;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.ServerPinger;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class CmdPing extends CmdBase
{
	public CmdPing() {
		super("ping", Level.ALL);
	}
	@Override
	public void execute(MinecraftServer minecraftServer, ICommandSender iCommandSender, String[] strings) throws CommandException
	{
		ITextComponent message = FTBUtilities.lang(iCommandSender,"commands.ping.unknown");
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.getIntegratedServer() != null) {
			message = FTBUtilities.lang(iCommandSender,"commands.ping.integrated_server");
		}
		ServerData data = mc.getCurrentServerData();
		if (data != null) {
			message = FTBUtilities.lang(iCommandSender,"commands.ping.ping", data.pingToServer);
		}
		iCommandSender.sendMessage(message);
	}
}
