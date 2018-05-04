package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbutilities.FTBUtilitiesCommon;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.data.Leaderboard;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.server.permission.PermissionAPI;

public class MessageLeaderboard extends MessageToServer
{
	private ResourceLocation id;

	public MessageLeaderboard()
	{
	}

	public MessageLeaderboard(ResourceLocation _id)
	{
		id = _id;
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUtilitiesNetHandler.STATS;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeResourceLocation(id);
	}

	@Override
	public void readData(DataIn data)
	{
		id = data.readResourceLocation();
	}

	@Override
	public void onMessage(EntityPlayerMP player)
	{
		Leaderboard leaderboard = FTBUtilitiesCommon.LEADERBOARDS.get(id);

		if (leaderboard != null && PermissionAPI.hasPermission(player, FTBUtilitiesPermissions.getLeaderboardNode(leaderboard)))
		{
			new MessageLeaderboardResponse(player, leaderboard).sendTo(player);
		}
	}
}