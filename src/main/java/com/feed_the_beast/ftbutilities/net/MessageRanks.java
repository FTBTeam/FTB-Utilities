package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbutilities.gui.ranks.GuiRanks;
import com.feed_the_beast.ftbutilities.gui.ranks.RankInst;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class MessageRanks extends MessageToClient
{
	private Collection<RankInst> ranks;
	private Collection<String> tags;
	private Map<String, String> playerRanks;

	public MessageRanks()
	{
	}

	public MessageRanks(Ranks r)
	{
		ranks = new ArrayList<>();

		for (Rank rank : r.ranks.values())
		{
			RankInst inst = new RankInst(rank.getId());
			inst.parent = rank.parent.isNone() ? "" : rank.parent.getId();
			inst.tags = rank.tags;
			inst.permissions = rank.permissions;
			ranks.add(inst);
		}

		tags = new HashSet<>(Rank.TAGS);
		playerRanks = new HashMap<>();

		for (ForgePlayer player : r.universe.getPlayers())
		{
			Rank set = r.getSetRank(player.getProfile());
			playerRanks.put(player.getName(), set == null || set.isNone() ? "" : set.getId());
		}
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUtilitiesNetHandler.FILES;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeCollection(ranks, RankInst.SERIALIZER);
		data.writeCollection(tags, DataOut.STRING);
		data.writeMap(playerRanks, DataOut.STRING, DataOut.STRING);
	}

	@Override
	public void readData(DataIn data)
	{
		ranks = data.readCollection(RankInst.DESERIALIZER);
		tags = data.readCollection(DataIn.STRING);
		playerRanks = data.readMap(DataIn.STRING, DataIn.STRING);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onMessage()
	{
		new GuiRanks(ranks, tags, playerRanks).openGui();
	}
}