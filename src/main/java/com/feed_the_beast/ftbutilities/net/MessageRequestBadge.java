package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.util.Badges;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class MessageRequestBadge extends MessageToServer<MessageRequestBadge>
{
	private UUID playerId;

	private static class ThreadBadge extends Thread
	{
		private final UUID playerId;
		private final EntityPlayer player;

		private ThreadBadge(UUID id, EntityPlayer p)
		{
			super("Badge_" + StringUtils.fromUUID(id));
			playerId = id;
			player = p;
			setDaemon(true);
		}

		@Override
		public void run()
		{
			new MessageSendBadge(playerId, Badges.get(playerId)).sendTo(player);
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
		return FTBUNetHandler.BADGES;
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
	public void onMessage(MessageRequestBadge m, EntityPlayer player)
	{
		new ThreadBadge(m.playerId, player).start();
	}
}