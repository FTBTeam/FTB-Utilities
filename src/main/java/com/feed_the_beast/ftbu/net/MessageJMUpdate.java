package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.api.EnumTeamColor;
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
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import com.feed_the_beast.ftbu.api_impl.ChunkUpgrade;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunks;
import com.feed_the_beast.ftbu.client.FTBUClient;
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
	private Map<UUID, com.feed_the_beast.ftbu.gui.ClaimedChunks.Team> teams;
	private com.feed_the_beast.ftbu.gui.ClaimedChunks.Data[] chunkData;

	public MessageJMUpdate()
	{
	}

	public MessageJMUpdate(int sx, int sz, EntityPlayer player)
	{
		startX = sx;
		startZ = sz;

		IForgePlayer player1 = FTBLibAPI.API.getUniverse().getPlayer(player);
		IForgeTeam team = player1.getTeam();

		chunkData = new com.feed_the_beast.ftbu.gui.ClaimedChunks.Data[GuiConfigs.CHUNK_SELECTOR_TILES_GUI * GuiConfigs.CHUNK_SELECTOR_TILES_GUI];
		teams = new HashMap<>();

		if (team != null)
		{
			com.feed_the_beast.ftbu.gui.ClaimedChunks.Team cteam = new com.feed_the_beast.ftbu.gui.ClaimedChunks.Team();
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
				com.feed_the_beast.ftbu.gui.ClaimedChunks.Data data = new com.feed_the_beast.ftbu.gui.ClaimedChunks.Data();
				IClaimedChunk chunk = ClaimedChunks.INSTANCE.getChunk(pos);
				IForgePlayer owner = chunk == null ? null : chunk.getOwner();

				if (owner != null && owner.getTeam() != null)
				{
					team = owner.getTeam();
					data.team = teams.get(team.getOwner().getId());

					if (data.team == null)
					{
						data.team = new com.feed_the_beast.ftbu.gui.ClaimedChunks.Team();
						data.team.ownerId = team.getOwner().getId();
						data.team.color = team.getColor();
						data.team.formattedName = team.getColor().getTextFormatting() + team.getTitle();
						data.team.isAlly = team.hasStatus(player.getGameProfile().getId(), EnumTeamStatus.ALLY);
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

		for (com.feed_the_beast.ftbu.gui.ClaimedChunks.Team t : teams.values())
		{
			data.writeUUID(t.ownerId);
			data.writeString(t.formattedName);
			data.writeByte(t.color.ordinal());
		}

		for (com.feed_the_beast.ftbu.gui.ClaimedChunks.Data d : chunkData)
		{
			data.writeInt(d.flags);

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

		chunkData = new com.feed_the_beast.ftbu.gui.ClaimedChunks.Data[GuiConfigs.CHUNK_SELECTOR_TILES_GUI * GuiConfigs.CHUNK_SELECTOR_TILES_GUI];
		teams = new HashMap<>();

		int s = data.readUnsignedShort();

		while (--s >= 0)
		{
			com.feed_the_beast.ftbu.gui.ClaimedChunks.Team team = new com.feed_the_beast.ftbu.gui.ClaimedChunks.Team();
			team.ownerId = data.readUUID();
			team.formattedName = data.readString();
			team.color = EnumTeamColor.NAME_MAP.get(data.readUnsignedByte());
			teams.put(team.ownerId, team);
		}

		for (int i = 0; i < chunkData.length; i++)
		{
			chunkData[i] = new com.feed_the_beast.ftbu.gui.ClaimedChunks.Data();
			chunkData[i].flags = data.readInt();

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