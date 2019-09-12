package com.feed_the_beast.ftbutilities.command.client;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.CPacketPing;
import net.minecraft.network.status.client.CPacketServerQuery;
import net.minecraft.network.status.server.SPacketPong;
import net.minecraft.network.status.server.SPacketServerInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CommandPing extends CmdBase
{
	private final static Executor EXECUTOR = Executors.newScheduledThreadPool(1);

	public CommandPing()
	{
		super("ping", Level.ALL);
	}

	@Override
	public void execute(MinecraftServer minecraftServer, ICommandSender iCommandSender, String[] strings) throws CommandException
	{
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.getIntegratedServer() != null)
		{
			iCommandSender.sendMessage(FTBUtilities.lang(iCommandSender, "commands.ping.integrated_server"));
			return;
		}
		ServerData data = mc.getCurrentServerData();
		if (data != null)
		{
			EXECUTOR.execute(() -> {
				ServerAddress address = ServerAddress.fromString(data.serverIP);
				try
				{
					NetworkManager networkManager = NetworkManager.createNetworkManagerAndConnect(InetAddress.getByName(address.getIP()), address.getPort(), false);
					networkManager.setNetHandler(new INetHandlerStatusClient()
					{
						private long latency = -1L;
						private long sendAt;

						@Override
						public void handleServerInfo(SPacketServerInfo sPacketServerInfo)
						{
							sendAt = Minecraft.getSystemTime();
							networkManager.sendPacket(new CPacketPing(sendAt));
						}

						@Override
						public void handlePong(SPacketPong sPacketPong)
						{
							latency = Minecraft.getSystemTime() - sendAt;
							networkManager.closeChannel(new TextComponentString("Finished"));
							iCommandSender.sendMessage(FTBUtilities.lang(iCommandSender, "commands.ping.ping", StringUtils.getTimeString(latency)));
						}

						@Override
						public void onDisconnect(ITextComponent iTextComponent)
						{
							if (latency == -1L)
							{
								iCommandSender.sendMessage(FTBUtilities.lang(iCommandSender, "commands.ping.unknown"));
							}
						}
					});
					networkManager.sendPacket(new C00Handshake(address.getIP(), address.getPort(), EnumConnectionState.STATUS));
					networkManager.sendPacket(new CPacketServerQuery());
				}
				catch (UnknownHostException e)
				{
					e.printStackTrace();
					iCommandSender.sendMessage(FTBUtilities.lang(iCommandSender, "commands.ping.unknown"));
				}
			});
			return;
		}
		iCommandSender.sendMessage(FTBUtilities.lang(iCommandSender, "commands.ping.unknown"));
	}
}
