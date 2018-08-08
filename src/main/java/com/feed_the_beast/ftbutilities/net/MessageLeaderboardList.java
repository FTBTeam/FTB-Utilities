package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbutilities.FTBUtilitiesCommon;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.data.Leaderboard;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.server.permission.PermissionAPI;

import java.util.LinkedHashMap;
import java.util.Map;

public class MessageLeaderboardList extends MessageToServer
{
	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUtilitiesNetHandler.STATS;
	}

	@Override
	public void onMessage(EntityPlayerMP player)
	{
		Map<ResourceLocation, ITextComponent> map = new LinkedHashMap<>();

		for (Leaderboard leaderboard : FTBUtilitiesCommon.LEADERBOARDS.values())
		{
			if (PermissionAPI.hasPermission(player, FTBUtilitiesPermissions.getLeaderboardNode(leaderboard)))
			{
				map.put(leaderboard.id, leaderboard.getTitle());
			}
		}

		new MessageLeaderboardListResponse(map).sendTo(player);
	}
}