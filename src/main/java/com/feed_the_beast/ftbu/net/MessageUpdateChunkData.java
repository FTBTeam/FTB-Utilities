package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.api.EnumTeamColor;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.io.Bits;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbl.lib.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.lib.net.MessageToClient;
import com.feed_the_beast.ftbl.lib.util.LMNetUtils;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.api_impl.FTBUtilitiesAPI_Impl;
import com.feed_the_beast.ftbu.client.CachedClientData;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageUpdateChunkData extends MessageToClient<MessageUpdateChunkData>
{
    private int dim;
    private Map<UUID, CachedClientData.TeamData> teamData;
    private TLongObjectHashMap<CachedClientData.ChunkData> chunkData;

    public MessageUpdateChunkData()
    {
    }

    public MessageUpdateChunkData(EntityPlayerMP player, int x, int z, int sx, int sz)
    {
        dim = player.dimension;
        teamData = new HashMap<>();
        chunkData = new TLongObjectHashMap<>();
        IForgePlayer player1 = FTBLibIntegration.API.getUniverse().getPlayer(player);

        for(int x1 = x; x1 < x + sx; x1++)
        {
            for(int z1 = z; z1 < z + sz; z1++)
            {
                ChunkDimPos pos = new ChunkDimPos(x1, z1, dim);
                IForgePlayer owner = FTBUtilitiesAPI_Impl.INSTANCE.getClaimedChunks().getChunkOwner(pos);

                if(owner != null && owner.getTeam() != null)
                {
                    CachedClientData.TeamData team = teamData.get(owner.getProfile().getId());

                    if(team == null)
                    {
                        team = new CachedClientData.TeamData(owner.getTeam());
                        teamData.put(team.ownerID, team);
                    }

                    byte flags = 0;

                    if(player1.equalsPlayer(owner))
                    {
                        flags |= CachedClientData.ChunkData.OWNER;

                        if(FTBUtilitiesAPI_Impl.INSTANCE.getLoadedChunks().isLoaded(pos, player1))
                        {
                            flags |= CachedClientData.ChunkData.LOADED;
                        }
                    }

                    chunkData.put(Bits.intsToLong(pos.posX, pos.posZ), new CachedClientData.ChunkData(team, flags));
                }
                else
                {
                    chunkData.put(Bits.intsToLong(pos.posX, pos.posZ), null);
                }
            }
        }
    }

    @Override
    public LMNetworkWrapper getWrapper()
    {
        return FTBUNetHandler.NET;
    }

    @Override
    public void fromBytes(ByteBuf io)
    {
        dim = io.readInt();
        teamData = new HashMap<>();
        chunkData = new TLongObjectHashMap<>();

        int s = io.readInt();

        while(--s >= 0)
        {
            CachedClientData.TeamData team = new CachedClientData.TeamData();
            team.ownerID = LMNetUtils.readUUID(io);
            team.color = EnumTeamColor.VALUES[io.readByte()];
            team.formattedName = LMNetUtils.readString(io);
            teamData.put(team.ownerID, team);
        }

        s = io.readInt();

        while(--s >= 0)
        {
            long pos = io.readLong();
            byte flags = io.readByte();

            if((flags & CachedClientData.ChunkData.NULL) != 0)
            {
                chunkData.put(pos, null);
            }
            else
            {
                CachedClientData.TeamData team = teamData.get(LMNetUtils.readUUID(io));
                chunkData.put(pos, new CachedClientData.ChunkData(team, flags));
            }
        }
    }

    @Override
    public void toBytes(ByteBuf io)
    {
        io.writeInt(dim);
        io.writeInt(teamData.size());

        for(CachedClientData.TeamData data : teamData.values())
        {
            LMNetUtils.writeUUID(io, data.ownerID);
            io.writeByte(data.color.ordinal());
            LMNetUtils.writeString(io, data.formattedName);
        }

        io.writeInt(chunkData.size());

        chunkData.forEachEntry((key, value) ->
        {
            io.writeLong(key);

            if(value == null)
            {
                io.writeByte(CachedClientData.ChunkData.NULL);
            }
            else
            {
                io.writeByte(value.flags);
                LMNetUtils.writeUUID(io, value.team.ownerID);
            }

            return true;
        });
    }

    @Override
    public void onMessage(MessageUpdateChunkData m)
    {
        CachedClientData.updateChunkData(m.dim, m.teamData, m.chunkData);
    }
}