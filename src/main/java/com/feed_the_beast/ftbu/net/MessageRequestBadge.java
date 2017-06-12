package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.lib.net.MessageToServer;
import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbl.lib.util.NetUtils;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import io.netty.buffer.ByteBuf;
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
			super("Badge_" + id);
			playerId = id;
			player = p;
			setDaemon(true);
		}

		@Override
		public void run()
		{
			new MessageSendBadge(playerId, FTBUUniverseData.getBadge(playerId)).sendTo(player);
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
		return FTBUNetHandler.NET;
	}

	@Override
	public void fromBytes(ByteBuf io)
	{
		playerId = NetUtils.readUUID(io);
	}

	@Override
	public void toBytes(ByteBuf io)
	{
		NetUtils.writeUUID(io, playerId);
	}

	@Override
	public void onMessage(MessageRequestBadge m, EntityPlayer player)
	{
		new ThreadBadge(m.playerId, player).start();
	}
}