package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbutilities.handlers.FTBUtilitiesClientEventHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class MessageSendBadge extends MessageToClient
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
		return FTBUtilitiesNetHandler.GENERAL;
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
	@SideOnly(Side.CLIENT)
	public void onMessage()
	{
		FTBUtilitiesClientEventHandler.setBadge(playerId, badgeURL);
	}
}