package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.gui.misc.ChunkSelectorMap;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.math.MathUtils;
import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

public class MessageClaimedChunksRequest extends MessageToServer
{
	private int startX, startZ;

	public MessageClaimedChunksRequest()
	{
	}

	public MessageClaimedChunksRequest(int sx, int sz)
	{
		startX = sx;
		startZ = sz;
	}

	public MessageClaimedChunksRequest(Entity entity)
	{
		this(MathUtils.chunk(entity.posX) - ChunkSelectorMap.TILES_GUI2, MathUtils.chunk(entity.posZ) - ChunkSelectorMap.TILES_GUI2);
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUtilitiesNetHandler.CLAIMS;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeVarInt(startX);
		data.writeVarInt(startZ);
	}

	@Override
	public void readData(DataIn data)
	{
		startX = data.readVarInt();
		startZ = data.readVarInt();
	}

	@Override
	public void onMessage(EntityPlayerMP player)
	{
		if (ClaimedChunks.isActive())
		{
			new MessageClaimedChunksUpdate(startX, startZ, player).sendTo(player);
		}
	}
}