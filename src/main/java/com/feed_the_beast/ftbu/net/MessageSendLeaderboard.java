package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.io.DataIn;
import com.feed_the_beast.ftbl.lib.io.DataOut;
import com.feed_the_beast.ftbl.lib.net.MessageToClient;
import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbu.api.Leaderboard;
import com.feed_the_beast.ftbu.gui.GuiLeaderboard;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MessageSendLeaderboard extends MessageToClient<MessageSendLeaderboard>
{
	private ITextComponent title;
	private Map<String, ITextComponent> values;

	public MessageSendLeaderboard()
	{
	}

	public MessageSendLeaderboard(Leaderboard leaderboard)
	{
		title = leaderboard.getTitle();
		values = new LinkedHashMap<>();

		List<Map.Entry<IForgePlayer, ITextComponent>> list = new ArrayList<>();

		for (IForgePlayer p : FTBLibAPI.API.getUniverse().getRealPlayers())
		{
			list.add(new AbstractMap.SimpleEntry<>(p, leaderboard.createValue(p)));
		}

		list.sort((o1, o2) -> {
			int o = leaderboard.getComparator().compare(o1.getKey(), o2.getKey());
			return o == 0 ? o1.getKey().getName().compareToIgnoreCase(o2.getKey().getName()) : o;
		});

		for (Map.Entry<IForgePlayer, ITextComponent> entry : list)
		{
			values.put(entry.getKey().getName(), entry.getValue());
		}
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUNetHandler.LEADERBOARDS;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeTextComponent(title);
		data.writeMap(values, DataOut.STRING, DataOut.TEXT_COMPONENT);
	}

	@Override
	public void readData(DataIn data)
	{
		title = data.readTextComponent();
		values = data.readMap(DataIn.STRING, DataIn.TEXT_COMPONENT);
	}

	@Override
	public void onMessage(MessageSendLeaderboard m, EntityPlayer player)
	{
		new GuiLeaderboard(m.title, m.values).openGui();
	}
}