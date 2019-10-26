package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesUniverseData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.HttpUtil;

import java.util.UUID;

public class MessageRequestBadge extends MessageToServer
{
	private UUID playerId;

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
		HttpUtil.DOWNLOADER_EXECUTOR.submit(() -> {
			String badge = FTBUtilitiesUniverseData.getBadge(Universe.get(), playerId);
			player.server.addScheduledTask(() -> new MessageSendBadge(playerId, badge).sendTo(player));
		});
	}
}