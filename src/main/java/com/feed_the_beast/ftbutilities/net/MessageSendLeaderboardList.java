package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbutilities.gui.GuiLeaderboardList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.Map;

public class MessageSendLeaderboardList extends MessageToClient<MessageSendLeaderboardList>
{
	private Map<ResourceLocation, ITextComponent> leaderboards;

	public MessageSendLeaderboardList()
	{
	}

	public MessageSendLeaderboardList(Map<ResourceLocation, ITextComponent> l)
	{
		leaderboards = l;
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUNetHandler.STATS;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeMap(leaderboards, DataOut.RESOURCE_LOCATION, DataOut.TEXT_COMPONENT);
	}

	@Override
	public void readData(DataIn data)
	{
		leaderboards = data.readMap(DataIn.RESOURCE_LOCATION, DataIn.TEXT_COMPONENT);
	}

	@Override
	public void onMessage(MessageSendLeaderboardList m, EntityPlayer player)
	{
		new GuiLeaderboardList(m.leaderboards).openGui();
	}
}