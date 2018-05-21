package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbutilities.data.Leaderboard;
import com.feed_the_beast.ftbutilities.data.LeaderboardValue;
import com.feed_the_beast.ftbutilities.gui.GuiLeaderboard;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class MessageLeaderboardResponse extends MessageToClient
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

	public MessageLeaderboardResponse()
	{
	}

	public MessageLeaderboardResponse(EntityPlayerMP player, Leaderboard leaderboard)
	{
		title = leaderboard.getTitle();
		values = new ArrayList<>();

		ForgePlayer p0 = Universe.get().getPlayer(player);
		List<ForgePlayer> players = new ArrayList<>(Universe.get().getPlayers());
		players.sort(leaderboard.getComparator());

		for (int i = 0; i < players.size(); i++)
		{
			ForgePlayer p = players.get(i);
			LeaderboardValue value = new LeaderboardValue();
			value.username = p.getDisplayNameString();
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
		return FTBUtilitiesNetHandler.STATS;
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
	@SideOnly(Side.CLIENT)
	public void onMessage()
	{
		new GuiLeaderboard(title, values).openGui();
	}
}