package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.api.EnumTeamStatus;
import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiConfigs;
import com.feed_the_beast.ftbl.lib.io.DataIn;
import com.feed_the_beast.ftbl.lib.io.DataOut;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbl.lib.net.MessageToClient;
import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.FTBUtilitiesAPI;
import com.feed_the_beast.ftbu.api.chunks.IChunkUpgrade;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import com.feed_the_beast.ftbu.api_impl.ChunkUpgrade;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunk;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunks;
import com.feed_the_beast.ftbu.gui.ClientClaimedChunks;
import com.feed_the_beast.ftbu.gui.GuiClaimedChunks;
import com.feed_the_beast.ftbu.util.FTBUTeamData;
import com.feed_the_beast.ftbu.util.FTBUUniverseData;
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
	public ClientClaimedChunks.Data[] chunkData;

	public MessageClaimedChunksUpdate()
	{
	}

	public MessageClaimedChunksUpdate(int sx, int sz, EntityPlayer player)
	{
		startX = sx;
		startZ = sz;

		IForgePlayer player1 = FTBLibAPI.API.getUniverse().getPlayer(player);
		FTBUTeamData teamData = player1.getTeam() == null ? null : FTBUTeamData.get(player1.getTeam());

		Collection<ClaimedChunk> chunks = teamData != null ? ClaimedChunks.INSTANCE.getTeamChunks(teamData.team) : Collections.emptyList();

		claimedChunks = chunks.size();

		loadedChunks = 0;

		for (IClaimedChunk c : chunks)
		{
			if (c.hasUpgrade(ChunkUpgrade.LOADED))
			{
				loadedChunks++;
			}
		}

		maxClaimedChunks = FTBUtilitiesAPI.API.getRankConfig(player, FTBUPermissions.CLAIMS_MAX_CHUNKS).getInt();
		maxLoadedChunks = FTBUtilitiesAPI.API.getRankConfig(player, FTBUPermissions.CHUNKLOADER_MAX_CHUNKS).getInt();

		chunkData = new ClientClaimedChunks.Data[GuiConfigs.CHUNK_SELECTOR_TILES_GUI * GuiConfigs.CHUNK_SELECTOR_TILES_GUI];
		teams = new HashMap<>();

		if (teamData != null)
		{
			ClientClaimedChunks.Team cteam = new ClientClaimedChunks.Team();
			cteam.ownerId = teamData.team.getOwner().getId();
			cteam.color = teamData.team.getColor();
			cteam.formattedName = teamData.team.getColor().getTextFormatting() + teamData.team.getTitle();
			cteam.isAlly = true;
			teams.put(cteam.ownerId, cteam);
		}

		boolean canSeeChunkInfo = PermissionAPI.hasPermission(player, FTBUPermissions.CLAIMS_CHUNKS_MODIFY_OTHERS);

		for (int x1 = 0; x1 < GuiConfigs.CHUNK_SELECTOR_TILES_GUI; x1++)
		{
			for (int z1 = 0; z1 < GuiConfigs.CHUNK_SELECTOR_TILES_GUI; z1++)
			{
				ChunkDimPos pos = new ChunkDimPos(startX + x1, startZ + z1, player.dimension);
				ClientClaimedChunks.Data data = new ClientClaimedChunks.Data();
				ClaimedChunk chunk = ClaimedChunks.INSTANCE.getChunk(pos);

				if (chunk != null)
				{
					IForgeTeam chunkTeam = chunk.getTeam();
					data.team = teams.get(chunkTeam.getOwner().getId());

					if (data.team == null)
					{
						data.team = new ClientClaimedChunks.Team();
						data.team.ownerId = chunkTeam.getOwner().getId();
						data.team.color = chunkTeam.getColor();
						data.team.formattedName = chunkTeam.getColor().getTextFormatting() + chunkTeam.getTitle();
						data.team.isAlly = chunkTeam.hasStatus(player.getGameProfile().getId(), EnumTeamStatus.ALLY);
						teams.put(data.team.ownerId, data.team);
					}

					data.setHasUpgrade(ChunkUpgrade.CLAIMED, true);

					boolean member = chunkTeam.hasStatus(player1, EnumTeamStatus.MEMBER);

					if (canSeeChunkInfo || member)
					{
						data.setHasUpgrade(ChunkUpgrade.CAN_CLAIM, member);

						for (IChunkUpgrade upgrade : FTBUUniverseData.CHUNK_UPGRADES.values())
						{
							if (upgrade != null && chunk.hasUpgrade(upgrade))
							{
								data.setHasUpgrade(upgrade, true);
							}
						}
					}
				}
				else
				{
					data.setHasUpgrade(ChunkUpgrade.CAN_CLAIM, true);
				}

				chunkData[x1 + z1 * GuiConfigs.CHUNK_SELECTOR_TILES_GUI] = data;
			}
		}
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUNetHandler.NET;
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
		data.writeMap(FTBUUniverseData.UPGRADE_TO_ID, ClientClaimedChunks.Data.UPGRADE_NAME_SERIALIZER, DataOut.INT);

		for (ClientClaimedChunks.Data d : chunkData)
		{
			data.writeCollection(d.upgrades, ClientClaimedChunks.Data.UPGRADE_ID_SERIALIZER);

			if (d.hasUpgrade(ChunkUpgrade.CLAIMED))
			{
				data.writeUUID(d.team.ownerId);
			}
		}
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

		data.readMap(null, ClientClaimedChunks.Data.UPGRADE_NAME_DESERIALIZER, DataIn.INT).forEach(FTBUUniverseData.SET_UPGRADE_ID);

		chunkData = new ClientClaimedChunks.Data[GuiConfigs.CHUNK_SELECTOR_TILES_GUI * GuiConfigs.CHUNK_SELECTOR_TILES_GUI];

		for (int i = 0; i < chunkData.length; i++)
		{
			chunkData[i] = new ClientClaimedChunks.Data();
			data.readCollection(chunkData[i].upgrades, ClientClaimedChunks.Data.UPGRADE_ID_DESERIALIZER);

			if (chunkData[i].hasUpgrade(ChunkUpgrade.CLAIMED))
			{
				chunkData[i].team = teams.get(data.readUUID());
			}
		}
	}

	@Override
	public void onMessage(MessageClaimedChunksUpdate m, EntityPlayer player)
	{
		GuiClaimedChunks.setData(m);
	}
}