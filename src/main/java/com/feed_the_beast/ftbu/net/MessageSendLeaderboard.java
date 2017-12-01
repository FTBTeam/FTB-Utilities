package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.io.DataIn;
import com.feed_the_beast.ftbl.lib.io.DataOut;
import com.feed_the_beast.ftbl.lib.net.MessageToClient;
import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbu.api.Leaderboard;
import com.feed_the_beast.ftbu.api.LeaderboardValue;
import com.feed_the_beast.ftbu.gui.GuiLeaderboard;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class MessageSendLeaderboard extends MessageToClient<MessageSendLeaderboard>
{
	private static final DataOut.Serializer<LeaderboardValue> VALUE_SERIALIZER = (data, object) -> {
		data.writeString(object.username);
		data.writeTextComponent(object.value);
		data.writeByte(object.color.ordinal());
	};

	private static final DataIn.Deserializer<LeaderboardValue> VALUE_DESERIALIZER = data ->
	{
		LeaderboardValue value = new LeaderboardValue();
		value.username = data.readString();
		value.value = data.readTextComponent();
		value.color = TextFormatting.values()[data.readUnsignedByte()];
		return value;
	};

	private ITextComponent title;
	private List<LeaderboardValue> values;

	public MessageSendLeaderboard()
	{
	}

	public MessageSendLeaderboard(EntityPlayerMP player, Leaderboard leaderboard)
	{
		title = leaderboard.getTitle();
		values = new ArrayList<>();

		IForgePlayer p0 = FTBLibAPI.API.getUniverse().getPlayer(player);
		List<IForgePlayer> players = FTBLibAPI.API.getUniverse().getRealPlayers();
		players.sort(leaderboard.getComparator());

		for (int i = 0; i < players.size(); i++)
		{
			IForgePlayer p = players.get(i);
			LeaderboardValue value = new LeaderboardValue();
			value.username = p.getName();
			value.value = leaderboard.createValue(p);

			if (p == p0)
			{
				value.color = TextFormatting.DARK_GREEN;
			}
			else if (!leaderboard.hasValidValue(p))
			{
				value.color = TextFormatting.DARK_GRAY;
			}
			else if (i < 3)
			{
				value.color = TextFormatting.GOLD;
			}
			else
			{
				value.color = TextFormatting.RESET;
			}

			values.add(value);
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
		data.writeCollection(values, VALUE_SERIALIZER);
	}

	@Override
	public void readData(DataIn data)
	{
		title = data.readTextComponent();
		values = new ArrayList<>();
		data.readCollection(values, VALUE_DESERIALIZER);
	}

	@Override
	public void onMessage(MessageSendLeaderboard m, EntityPlayer player)
	{
		new GuiLeaderboard(m.title, m.values).openGui();
	}
}