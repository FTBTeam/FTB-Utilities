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
import com.feed_the_beast.ftbu.api_impl.ChunkUpgrade;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunks;
import com.feed_the_beast.ftbu.client.FTBUClient;
import com.feed_the_beast.ftbu.gui.ClientClaimedChunks;
import com.feed_the_beast.ftbu.util.FTBUUniverseData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.ChunkPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class MessageJMUpdate extends MessageToClient<MessageJMUpdate>
{
	private int startX, startZ;
	private Map<UUID, ClientClaimedChunks.Team> teams;
	private ClientClaimedChunks.Data[] chunkData;

	public MessageJMUpdate()
	{
	}

	public MessageJMUpdate(int sx, int sz, EntityPlayer player)
	{
		startX = sx;
		startZ = sz;

		IForgePlayer player1 = FTBLibAPI.API.getUniverse().getPlayer(player);
		IForgeTeam team = player1.getTeam();

		chunkData = new ClientClaimedChunks.Data[GuiConfigs.CHUNK_SELECTOR_TILES_GUI * GuiConfigs.CHUNK_SELECTOR_TILES_GUI];
		teams = new HashMap<>();

		if (team != null)
		{
			ClientClaimedChunks.Team cteam = new ClientClaimedChunks.Team();
			cteam.ownerId = team.getOwner().getId();
			cteam.color = team.getColor();
			cteam.formattedName = team.getColor().getTextFormatting() + team.getTitle();
			cteam.isAlly = true;
			teams.put(cteam.ownerId, cteam);
		}

		for (int x1 = 0; x1 < GuiConfigs.CHUNK_SELECTOR_TILES_GUI; x1++)
		{
			for (int z1 = 0; z1 < GuiConfigs.CHUNK_SELECTOR_TILES_GUI; z1++)
			{
				ChunkDimPos pos = new ChunkDimPos(startX + x1, startZ + z1, player.dimension);
				ClientClaimedChunks.Data data = new ClientClaimedChunks.Data();
				IForgeTeam chunkTeam = ClaimedChunks.INSTANCE.getChunkTeam(pos);

				if (chunkTeam != null)
				{
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
		data.writeShort(teams.size());
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
		teams = new HashMap<>();

		for (ClientClaimedChunks.Team team : data.readCollection(null, ClientClaimedChunks.Team.DESERIALIZER))
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
	public void onMessage(MessageJMUpdate m, EntityPlayer player)
	{
		if (FTBUClient.JM_INTEGRATION != null)
		{
			for (int z = 0; z < GuiConfigs.CHUNK_SELECTOR_TILES_GUI; z++)
			{
				for (int x = 0; x < GuiConfigs.CHUNK_SELECTOR_TILES_GUI; x++)
				{
					FTBUClient.JM_INTEGRATION.chunkChanged(new ChunkPos(m.startX + x, m.startZ + z), m.chunkData[x + z * GuiConfigs.CHUNK_SELECTOR_TILES_GUI]);
				}
			}
		}
	}
}