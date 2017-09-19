package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.lib.io.DataIn;
import com.feed_the_beast.ftbl.lib.io.DataOut;
import com.feed_the_beast.ftbl.lib.net.MessageToClient;
import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbu.client.CachedClientData;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class MessageSendBadge extends MessageToClient<MessageSendBadge>
{
	private UUID playerId;
	private String badgeURL;

	public MessageSendBadge()
	{
	}

	public MessageSendBadge(UUID player, String url)
	{
		playerId = player;
		badgeURL = url;
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUNetHandler.NET;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeUUID(playerId);
		data.writeString(badgeURL);
	}

	@Override
	public void readData(DataIn data)
	{
		playerId = data.readUUID();
		badgeURL = data.readString();
	}

	@Override
	public void onMessage(MessageSendBadge m, EntityPlayer player)
	{
		CachedClientData.setBadge(m.playerId, m.badgeURL);
	}
}