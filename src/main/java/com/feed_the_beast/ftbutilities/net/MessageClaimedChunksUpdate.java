package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.gui.misc.ChunkSelectorMap;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbutilities.FTBUPermissions;
import com.feed_the_beast.ftbutilities.data.ChunkUpgrade;
import com.feed_the_beast.ftbutilities.data.ChunkUpgrades;
import com.feed_the_beast.ftbutilities.data.ClaimedChunk;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import com.feed_the_beast.ftbutilities.gui.ClientClaimedChunks;
import com.feed_the_beast.ftbutilities.gui.UpdateClientDataEvent;
import com.feed_the_beast.ftbutilities.util.FTBUTeamData;
import com.feed_the_beast.ftbutilities.util.FTBUUniverseData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.server.permission.PermissionAPI;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class MessageClaimedChunksUpdate extends MessageToClient<MessageClaimedChunksUpdate>
{
	public int startX, startZ, claimedChunks, loadedChunks, maxClaimedChunks, maxLoadedChunks;
	public Map<UUID, ClientClaimedChunks.Team> teams;

	public MessageClaimedChunksUpdate()
	{
	}

	public MessageClaimedChunksUpdate(int sx, int sz, EntityPlayer player)
	{
		startX = sx;
		startZ = sz;

		ForgePlayer p = Universe.get().getPlayer(player);
		FTBUTeamData teamData = p.getTeam() == null ? null : FTBUTeamData.get(p.getTeam());

		Collection<ClaimedChunk> chunks = teamData != null ? ClaimedChunks.get().getTeamChunks(teamData.team) : Collections.emptyList();

		claimedChunks = chunks.size();
		loadedChunks = 0;

		for (ClaimedChunk c : chunks)
		{
			if (c.hasUpgrade(ChunkUpgrades.LOADED))
			{
				loadedChunks++;
			}
		}

		maxClaimedChunks = teamData == null ? -1 : teamData.getMaxClaimChunks();
		maxLoadedChunks = teamData == null ? -1 : teamData.getMaxChunkloaderChunks();
		teams = new HashMap<>();

		boolean canSeeChunkInfo = PermissionAPI.hasPermission(player, FTBUPermissions.CLAIMS_CHUNKS_MODIFY_OTHERS);

		for (int x1 = 0; x1 < ChunkSelectorMap.TILES_GUI; x1++)
		{
			for (int z1 = 0; z1 < ChunkSelectorMap.TILES_GUI; z1++)
			{
				ChunkDimPos pos = new ChunkDimPos(startX + x1, startZ + z1, player.dimension);
				ClaimedChunk chunk = ClaimedChunks.get().getChunk(pos);

				if (chunk != null)
				{
					ForgeTeam chunkTeam = chunk.getTeam();
					ClientClaimedChunks.Team team = teams.get(chunkTeam.getOwner().getId());

					if (team == null)
					{
						team = new ClientClaimedChunks.Team(chunkTeam.getOwner().getId());
						team.color = chunkTeam.getColor();
						team.formattedName = chunkTeam.getColor().getTextFormatting() + chunkTeam.getTitle();
						team.isAlly = chunkTeam.isAlly(p);
						teams.put(team.ownerId, team);
					}

					ClientClaimedChunks.ChunkData data = new ClientClaimedChunks.ChunkData(team);

					boolean member = chunkTeam.isMember(p);

					if (canSeeChunkInfo || member)
					{
						for (ChunkUpgrade upgrade : FTBUUniverseData.CHUNK_UPGRADES.values())
						{
							if (chunk.hasUpgrade(upgrade))
							{
								data.setHasUpgrade(upgrade, true);
							}
						}
					}

					team.chunks.put(x1 + z1 * ChunkSelectorMap.TILES_GUI, data);
				}
			}
		}
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
		data.writeInt(claimedChunks);
		data.writeInt(loadedChunks);
		data.writeInt(maxClaimedChunks);
		data.writeInt(maxLoadedChunks);
		data.writeCollection(teams.values(), ClientClaimedChunks.Team.SERIALIZER);
	}

	@Override
	public void readData(DataIn data)
	{
		startX = data.readInt();
		startZ = data.readInt();
		claimedChunks = data.readInt();
		loadedChunks = data.readInt();
		maxClaimedChunks = data.readInt();
		maxLoadedChunks = data.readInt();

		teams = new HashMap<>();

		for (ClientClaimedChunks.Team team : data.readCollection(ClientClaimedChunks.Team.DESERIALIZER))
		{
			teams.put(team.ownerId, team);
		}
	}

	@Override
	public void onMessage(MessageClaimedChunksUpdate m, EntityPlayer player)
	{
		new UpdateClientDataEvent(m).post();
	}
}