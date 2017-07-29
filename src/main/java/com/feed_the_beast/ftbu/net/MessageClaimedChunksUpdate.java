package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.api.EnumTeamColor;
import com.feed_the_beast.ftbl.api.EnumTeamStatus;
import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiConfigs;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbl.lib.net.MessageToClient;
import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbl.lib.util.NetUtils;
import com.feed_the_beast.ftbu.FTBUCommon;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.FTBUtilitiesAPI;
import com.feed_the_beast.ftbu.api.chunks.IChunkUpgrade;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import com.feed_the_beast.ftbu.api_impl.ChunkUpgrade;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunkStorage;
import com.feed_the_beast.ftbu.gui.ClaimedChunks;
import com.feed_the_beast.ftbu.gui.GuiClaimedChunks;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.server.permission.PermissionAPI;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class MessageClaimedChunksUpdate extends MessageToClient<MessageClaimedChunksUpdate>
{
	public int startX, startZ, claimedChunks, loadedChunks, maxClaimedChunks, maxLoadedChunks;
	public Map<UUID, ClaimedChunks.Team> teams;
	public ClaimedChunks.Data[] chunkData;

	public MessageClaimedChunksUpdate()
	{
	}

	public MessageClaimedChunksUpdate(int sx, int sz, EntityPlayer player)
	{
		startX = sx;
		startZ = sz;

		IForgePlayer player1 = FTBLibAPI.API.getUniverse().getPlayer(player);
		IForgeTeam team = player1.getTeam();

		Collection<IClaimedChunk> chunks = ClaimedChunkStorage.INSTANCE.getChunks(player1);

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

		chunkData = new ClaimedChunks.Data[GuiConfigs.CHUNK_SELECTOR_TILES_GUI * GuiConfigs.CHUNK_SELECTOR_TILES_GUI];
		teams = new HashMap<>();

		if (team != null)
		{
			ClaimedChunks.Team cteam = new ClaimedChunks.Team();
			cteam.ownerId = team.getOwner().getId();
			cteam.color = team.getColor();
			cteam.formattedName = team.getColor().getTextFormatting() + team.getTitle();
			cteam.isAlly = true;
			teams.put(cteam.ownerId, cteam);
		}

		boolean canSeeChunkInfo = PermissionAPI.hasPermission(player, FTBUPermissions.CLAIMS_CHUNKS_MODIFY_OTHERS);

		for (int x1 = 0; x1 < GuiConfigs.CHUNK_SELECTOR_TILES_GUI; x1++)
		{
			for (int z1 = 0; z1 < GuiConfigs.CHUNK_SELECTOR_TILES_GUI; z1++)
			{
				ChunkDimPos pos = new ChunkDimPos(startX + x1, startZ + z1, player.dimension);
				ClaimedChunks.Data data = new ClaimedChunks.Data();
				IClaimedChunk chunk = ClaimedChunkStorage.INSTANCE.getChunk(pos);
				IForgePlayer owner = chunk == null ? null : chunk.getOwner();

				if (owner != null && owner.getTeam() != null)
				{
					team = owner.getTeam();
					data.team = teams.get(team.getOwner().getId());

					if (data.team == null)
					{
						data.team = new ClaimedChunks.Team();
						data.team.ownerId = team.getOwner().getId();
						data.team.color = team.getColor();
						data.team.formattedName = team.getColor().getTextFormatting() + team.getTitle();
						data.team.isAlly = team.hasStatus(player.getGameProfile().getId(), EnumTeamStatus.ALLY);
						teams.put(data.team.ownerId, data.team);
					}

					data.setHasUpgrade(ChunkUpgrade.CLAIMED, true);

					if (canSeeChunkInfo || team.hasStatus(player1, EnumTeamStatus.MEMBER))
					{
						boolean isOwner = player1.equalsPlayer(owner);
						data.setHasUpgrade(ChunkUpgrade.CAN_CLAIM, isOwner);
						data.setHasUpgrade(ChunkUpgrade.IS_OWNER, isOwner);

						for (IChunkUpgrade upgrade : FTBUCommon.CHUNK_UPGRADES)
						{
							if (upgrade != null && chunk.hasUpgrade(upgrade))
							{
								data.setHasUpgrade(upgrade, true);
							}
						}
					}

					if (data.team.isAlly)
					{
						data.owner = owner.getName();
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
	public void fromBytes(ByteBuf io)
	{
		startX = io.readInt();
		startZ = io.readInt();
		claimedChunks = io.readInt();
		loadedChunks = io.readInt();
		maxClaimedChunks = io.readInt();
		maxLoadedChunks = io.readInt();

		chunkData = new ClaimedChunks.Data[GuiConfigs.CHUNK_SELECTOR_TILES_GUI * GuiConfigs.CHUNK_SELECTOR_TILES_GUI];
		teams = new HashMap<>();

		int s = io.readUnsignedShort();

		while (--s >= 0)
		{
			ClaimedChunks.Team team = new ClaimedChunks.Team();
			team.ownerId = NetUtils.readUUID(io);
			team.formattedName = ByteBufUtils.readUTF8String(io);
			team.color = EnumTeamColor.NAME_MAP.get(io.readUnsignedByte());
			team.isAlly = io.readBoolean();
			teams.put(team.ownerId, team);
		}

		for (int i = 0; i < chunkData.length; i++)
		{
			chunkData[i] = new ClaimedChunks.Data();
			chunkData[i].flags = io.readInt();

			if (chunkData[i].hasUpgrade(ChunkUpgrade.CLAIMED))
			{
				chunkData[i].team = teams.get(NetUtils.readUUID(io));

				if (chunkData[i].team.isAlly)
				{
					chunkData[i].owner = ByteBufUtils.readUTF8String(io);
				}
			}
		}
	}

	@Override
	public void toBytes(ByteBuf io)
	{
		io.writeInt(startX);
		io.writeInt(startZ);
		io.writeInt(claimedChunks);
		io.writeInt(loadedChunks);
		io.writeInt(maxClaimedChunks);
		io.writeInt(maxLoadedChunks);

		io.writeShort(teams.size());

		for (ClaimedChunks.Team t : teams.values())
		{
			NetUtils.writeUUID(io, t.ownerId);
			ByteBufUtils.writeUTF8String(io, t.formattedName);
			io.writeByte(t.color.ordinal());
			io.writeBoolean(t.isAlly);
		}

		for (ClaimedChunks.Data data : chunkData)
		{
			io.writeInt(data.flags);

			if (data.hasUpgrade(ChunkUpgrade.CLAIMED))
			{
				NetUtils.writeUUID(io, data.team.ownerId);

				if (data.team.isAlly)
				{
					ByteBufUtils.writeUTF8String(io, data.owner);
				}
			}
		}
	}

	@Override
	public void onMessage(MessageClaimedChunksUpdate m, EntityPlayer player)
	{
		GuiClaimedChunks.setData(m);
	}
}