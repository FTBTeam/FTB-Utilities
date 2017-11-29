package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.lib.io.DataIn;
import com.feed_the_beast.ftbl.lib.io.DataOut;
import com.feed_the_beast.ftbl.lib.net.MessageToClient;
import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbu.gui.GuiLeaderboardList;
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
		return FTBUNetHandler.LEADERBOARDS;
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