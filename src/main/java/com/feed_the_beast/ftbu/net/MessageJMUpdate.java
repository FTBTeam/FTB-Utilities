package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.api.EnumTeamColor;
import com.feed_the_beast.ftbl.api.EnumTeamStatus;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiConfigs;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbl.lib.net.MessageToClient;
import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbl.lib.util.NetUtils;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import com.feed_the_beast.ftbu.api_impl.ChunkUpgrade;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunkStorage;
import com.feed_the_beast.ftbu.client.FTBUClient;
import com.feed_the_beast.ftbu.gui.ClaimedChunks;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by LatvianModder on 29.09.2016.
 */
public class MessageJMUpdate extends MessageToClient<MessageJMUpdate>
{
    private int startX, startZ;
    private Map<UUID, ClaimedChunks.Team> teams;
    private ClaimedChunks.Data[] chunkData;

    public MessageJMUpdate()
    {
    }

    public MessageJMUpdate(int sx, int sz, EntityPlayer player)
    {
        startX = sx;
        startZ = sz;

        IForgePlayer player1 = FTBLibIntegration.API.getUniverse().getPlayer(player);
        IForgeTeam team = player1.getTeam();

        chunkData = new ClaimedChunks.Data[GuiConfigs.CHUNK_SELECTOR_TILES_GUI * GuiConfigs.CHUNK_SELECTOR_TILES_GUI];
        teams = new HashMap<>();

        if(team != null)
        {
            ClaimedChunks.Team cteam = new ClaimedChunks.Team();
            cteam.ownerId = team.getOwner().getId();
            cteam.color = team.getColor();
            cteam.formattedName = team.getColor().getTextFormatting() + team.getTitle();
            cteam.isAlly = true;
            teams.put(cteam.ownerId, cteam);
        }

        for(int x1 = 0; x1 < GuiConfigs.CHUNK_SELECTOR_TILES_GUI; x1++)
        {
            for(int z1 = 0; z1 < GuiConfigs.CHUNK_SELECTOR_TILES_GUI; z1++)
            {
                ChunkDimPos pos = new ChunkDimPos(startX + x1, startZ + z1, player.dimension);
                ClaimedChunks.Data data = new ClaimedChunks.Data();
                IClaimedChunk chunk = ClaimedChunkStorage.INSTANCE.getChunk(pos);
                IForgePlayer owner = chunk == null ? null : chunk.getOwner();

                if(owner != null && owner.getTeam() != null)
                {
                    team = owner.getTeam();
                    data.team = teams.get(team.getOwner().getId());

                    if(data.team == null)
                    {
                        data.team = new ClaimedChunks.Team();
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
    public void fromBytes(ByteBuf io)
    {
        startX = io.readInt();
        startZ = io.readInt();

        chunkData = new ClaimedChunks.Data[GuiConfigs.CHUNK_SELECTOR_TILES_GUI * GuiConfigs.CHUNK_SELECTOR_TILES_GUI];
        teams = new HashMap<>();

        int s = io.readUnsignedShort();

        while(--s >= 0)
        {
            ClaimedChunks.Team team = new ClaimedChunks.Team();
            team.ownerId = NetUtils.readUUID(io);
            team.formattedName = ByteBufUtils.readUTF8String(io);
            team.color = EnumTeamColor.get(io.readUnsignedByte());
            teams.put(team.ownerId, team);
        }

        for(int i = 0; i < chunkData.length; i++)
        {
            chunkData[i] = new ClaimedChunks.Data();
            chunkData[i].flags = io.readInt();

            if(chunkData[i].hasUpgrade(ChunkUpgrade.CLAIMED))
            {
                chunkData[i].team = teams.get(NetUtils.readUUID(io));
            }
        }
    }

    @Override
    public void toBytes(ByteBuf io)
    {
        io.writeInt(startX);
        io.writeInt(startZ);

        io.writeShort(teams.size());

        for(ClaimedChunks.Team t : teams.values())
        {
            NetUtils.writeUUID(io, t.ownerId);
            ByteBufUtils.writeUTF8String(io, t.formattedName);
            io.writeByte(t.color.ordinal());
        }

        for(ClaimedChunks.Data data : chunkData)
        {
            io.writeInt(data.flags);

            if(data.hasUpgrade(ChunkUpgrade.CLAIMED))
            {
                NetUtils.writeUUID(io, data.team.ownerId);
            }
        }
    }

    @Override
    public void onMessage(MessageJMUpdate m, EntityPlayer player)
    {
        if(FTBUClient.JM_INTEGRATION != null)
        {
            for(int z = 0; z < GuiConfigs.CHUNK_SELECTOR_TILES_GUI; z++)
            {
                for(int x = 0; x < GuiConfigs.CHUNK_SELECTOR_TILES_GUI; x++)
                {
                    FTBUClient.JM_INTEGRATION.chunkChanged(new ChunkPos(m.startX + x, m.startZ + z), m.chunkData[x + z * GuiConfigs.CHUNK_SELECTOR_TILES_GUI]);
                }
            }
        }
    }
}