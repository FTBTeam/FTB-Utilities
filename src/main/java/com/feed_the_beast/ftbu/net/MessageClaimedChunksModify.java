package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftblib.FTBLibNotifications;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbu.FTBUConfig;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.data.ClaimedChunks;
import com.feed_the_beast.ftbu.util.FTBUTeamData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.server.permission.PermissionAPI;

import java.util.Collection;

public class MessageClaimedChunksModify extends MessageToServer<MessageClaimedChunksModify>
{
	public static final int CLAIM = 0;
	public static final int UNCLAIM = 1;
	public static final int LOAD = 2;
	public static final int UNLOAD = 3;

	private int startX, startZ;
	private int action;
	private Collection<ChunkPos> chunks;

	public MessageClaimedChunksModify()
	{
	}

	public MessageClaimedChunksModify(int sx, int sz, int a, Collection<ChunkPos> c)
	{
		startX = sx;
		startZ = sz;
		action = a;
		chunks = c;
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUNetHandler.CLAIMS;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeInt(startX);
		data.writeInt(startZ);
		data.writeByte(action);
		data.writeCollection(chunks, DataOut.CHUNK_POS);
	}

	@Override
	public void readData(DataIn data)
	{
		startX = data.readInt();
		startZ = data.readInt();
		action = data.readUnsignedByte();
		chunks = data.readCollection(null, DataIn.CHUNK_POS);
	}

	@Override
	public void onMessage(MessageClaimedChunksModify m, EntityPlayer player)
	{
		ForgePlayer p = Universe.get().getPlayer(player);

		if (!FTBUConfig.world.chunk_claiming)
		{
			return;
		}

		ForgeTeam team = p.getTeam();

		if (team == null)
		{
			FTBLibNotifications.NO_TEAM.send(player);
			return;
		}

		boolean canUnclaim = m.action == UNCLAIM && PermissionAPI.hasPermission(player.getGameProfile(), FTBUPermissions.CLAIMS_CHUNKS_MODIFY_OTHERS, null);

		for (ChunkPos pos0 : m.chunks)
		{
			ChunkDimPos pos = new ChunkDimPos(pos0, player.dimension);

			switch (m.action)
			{
				case CLAIM:
					ClaimedChunks.get().claimChunk(FTBUTeamData.get(team), pos);
					break;
				case UNCLAIM:
					if (canUnclaim || team.equalsTeam(ClaimedChunks.get().getChunkTeam(pos)))
					{
						ClaimedChunks.get().unclaimChunk(team, pos);
					}
					break;
				case LOAD:
					ClaimedChunks.get().setLoaded(team, pos, true);
					break;
				case UNLOAD:
					ClaimedChunks.get().setLoaded(team, pos, false);
					break;
			}
		}
	}
}