package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbutilities.data.Badges;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.UUID;

public class MessageRequestBadge extends MessageToServer
{
	private UUID playerId;

	private static class ThreadBadge extends Thread
	{
		private final UUID playerId;
		private final EntityPlayerMP player;

		private ThreadBadge(UUID id, EntityPlayerMP p)
		{
			super("Badge_" + p.getName());
			playerId = id;
			player = p;
			setDaemon(true);
		}

		@Override
		public void run()
		{
			new MessageSendBadge(playerId, Badges.get(Universe.get(), playerId)).sendTo(player);
		}
	}

	public MessageRequestBadge()
	{
	}

	public MessageRequestBadge(UUID player)
	{
		playerId = player;
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUtilitiesNetHandler.GENERAL;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeUUID(playerId);
	}

	@Override
	public void readData(DataIn data)
	{
		playerId = data.readUUID();
	}

	@Override
	public void onMessage(EntityPlayerMP player)
	{
		new ThreadBadge(playerId, player).start();
	}
}