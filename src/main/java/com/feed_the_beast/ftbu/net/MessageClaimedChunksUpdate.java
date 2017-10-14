package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.api.EnumTeamStatus;
import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.lib.gui.misc.ChunkSelectorMap;
import com.feed_the_beast.ftbl.lib.io.DataIn;
import com.feed_the_beast.ftbl.lib.io.DataOut;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbl.lib.net.MessageToClient;
import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.FTBUtilitiesAPI;
import com.feed_the_beast.ftbu.api.chunks.ChunkUpgrade;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import com.feed_the_beast.ftbu.api_impl.ChunkUpgrades;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunk;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunks;
import com.feed_the_beast.ftbu.gui.ClientClaimedChunks;
import com.feed_the_beast.ftbu.gui.UpdateClientDataEvent;
import com.feed_the_beast.ftbu.util.FTBUTeamData;
import com.feed_the_beast.ftbu.util.FTBUUniverseData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.server.permission.PermissionAPI;

import java.util.ArrayList;
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

		IForgePlayer p = FTBLibAPI.API.getUniverse().getPlayer(player);
		FTBUTeamData teamData = p.getTeam() == null ? null : FTBUTeamData.get(p.getTeam());

		Collection<ClaimedChunk> chunks = teamData != null ? ClaimedChunks.INSTANCE.getTeamChunks(teamData.team) : Collections.emptyList();

		claimedChunks = chunks.size();
		loadedChunks = 0;

		for (IClaimedChunk c : chunks)
		{
			if (c.hasUpgrade(ChunkUpgrades.LOADED))
			{
				loadedChunks++;
			}
		}

		maxClaimedChunks = 0;
		maxLoadedChunks = 0;

		if (teamData != null)
		{
			for (IForgePlayer member : teamData.team.getPlayersWithStatus(new ArrayList<>(), EnumTeamStatus.MEMBER))
			{
				maxClaimedChunks += FTBUtilitiesAPI.API.getRankConfig(member.getProfile(), FTBUPermissions.CLAIMS_MAX_CHUNKS).getInt();
				maxLoadedChunks += FTBUtilitiesAPI.API.getRankConfig(member.getProfile(), FTBUPermissions.CHUNKLOADER_MAX_CHUNKS).getInt();
			}
		}

		teams = new HashMap<>();

		boolean canSeeChunkInfo = PermissionAPI.hasPermission(player, FTBUPermissions.CLAIMS_CHUNKS_MODIFY_OTHERS);

		for (int x1 = 0; x1 < ChunkSelectorMap.TILES_GUI; x1++)
		{
			for (int z1 = 0; z1 < ChunkSelectorMap.TILES_GUI; z1++)
			{
				ChunkDimPos pos = new ChunkDimPos(startX + x1, startZ + z1, player.dimension);
				ClaimedChunk chunk = ClaimedChunks.INSTANCE.getChunk(pos);

				if (chunk != null)
				{
					IForgeTeam chunkTeam = chunk.getTeam();
					ClientClaimedChunks.Team team = teams.get(chunkTeam.getOwner().getId());

					if (team == null)
					{
						team = new ClientClaimedChunks.Team(chunkTeam.getOwner().getId());
						team.color = chunkTeam.getColor();
						team.formattedName = chunkTeam.getColor().getTextFormatting() + chunkTeam.getTitle();
						team.isAlly = chunkTeam.hasStatus(player.getGameProfile().getId(), EnumTeamStatus.ALLY);
						teams.put(team.ownerId, team);
					}

					ClientClaimedChunks.ChunkData data = new ClientClaimedChunks.ChunkData(team);

					boolean member = chunkTeam.hasStatus(p, EnumTeamStatus.MEMBER);

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
		data.writeMap(FTBUUniverseData.ID_TO_UPGRADE, DataOut.INT, ClientClaimedChunks.ChunkData.UPGRADE_NAME_SERIALIZER);
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
		ClientClaimedChunks.ID_TO_UPGRADE.clear();
		data.readMap(null, DataIn.INT, ClientClaimedChunks.ChunkData.UPGRADE_NAME_DESERIALIZER).forEach(ClientClaimedChunks.ID_TO_UPGRADE::put);

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