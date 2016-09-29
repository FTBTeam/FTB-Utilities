package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.api.rankconfig.RankConfigAPI;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbl.lib.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.lib.net.MessageToClient;
import com.feed_the_beast.ftbl.lib.util.LMNetUtils;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunkStorage;
import com.feed_the_beast.ftbu.api_impl.FTBUtilitiesAPI_Impl;
import com.feed_the_beast.ftbu.api_impl.LoadedChunkStorage;
import com.feed_the_beast.ftbu.gui.ClaimedChunks;
import com.feed_the_beast.ftbu.gui.GuiClaimedChunks;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by LatvianModder on 29.09.2016.
 */
public class MessageClaimedChunksUpdate extends MessageToClient<MessageClaimedChunksUpdate>
{
    private int startX, startZ, claimedChunks, loadedChunks, maxClaimedChunks, maxLoadedChunks;
    private Map<UUID, ClaimedChunks.Team> teams;
    private ClaimedChunks.Data[] chunkData;

    public MessageClaimedChunksUpdate()
    {
    }

    public MessageClaimedChunksUpdate(int sx, int sz, EntityPlayerMP player)
    {
        startX = sx;
        startZ = sz;

        IForgePlayer player1 = FTBLibIntegration.API.getUniverse().getPlayer(player);
        IForgeTeam team1 = player1.getTeam();

        claimedChunks = ClaimedChunkStorage.INSTANCE.getChunks(player1).size();
        loadedChunks = LoadedChunkStorage.INSTANCE.getLoadedChunks(player1);
        maxClaimedChunks = RankConfigAPI.getRankConfig(player, FTBUPermissions.CLAIMS_MAX_CHUNKS).getInt();
        maxLoadedChunks = RankConfigAPI.getRankConfig(player, FTBUPermissions.CHUNKLOADER_MAX_CHUNKS).getInt();

        chunkData = new ClaimedChunks.Data[ClaimedChunks.TILES_GUI * ClaimedChunks.TILES_GUI];
        teams = new HashMap<>();

        if(team1 != null)
        {
            ClaimedChunks.Team cteam = new ClaimedChunks.Team();
            cteam.ownerID = team1.getOwner().getProfile().getId();
            cteam.colorID = team1.getColor().getColorID();
            cteam.formattedName = team1.getTitle();
            cteam.isAlly = true;
            teams.put(cteam.ownerID, cteam);
        }

        for(int x1 = 0; x1 < ClaimedChunks.TILES_GUI; x1++)
        {
            for(int z1 = 0; z1 < ClaimedChunks.TILES_GUI; z1++)
            {
                ChunkDimPos pos = new ChunkDimPos(startX + x1, startZ + z1, player.dimension);
                ClaimedChunks.Data data = new ClaimedChunks.Data();
                IForgePlayer owner = FTBUtilitiesAPI_Impl.INSTANCE.getClaimedChunks().getChunkOwner(pos);

                if(owner != null && owner.getTeam() != null)
                {
                    IForgeTeam team = owner.getTeam();
                    data.team = teams.get(team.getOwner().getProfile().getId());

                    if(data.team == null)
                    {
                        data.team = new ClaimedChunks.Team();
                        data.team.ownerID = team.getOwner().getProfile().getId();
                        data.team.colorID = team.getColor().getColorID();
                        data.team.formattedName = team.getTitle();
                        data.team.isAlly = team1 != null && team.isAllyTeam(team1.getName()) && team1.isAllyTeam(team.getName());
                        teams.put(data.team.ownerID, data.team);
                    }

                    data.flags |= ClaimedChunks.DATA_IS_CLAIMED;

                    if(player1.equalsPlayer(owner))
                    {
                        data.flags |= ClaimedChunks.DATA_CAN_CLAIM;
                        data.flags |= ClaimedChunks.DATA_OWNER;

                        if(FTBUtilitiesAPI_Impl.INSTANCE.getLoadedChunks().isLoaded(pos, player1))
                        {
                            data.flags |= ClaimedChunks.DATA_LOADED;
                        }
                    }

                    if(data.team.isAlly)
                    {
                        data.owner = owner.getProfile().getName();
                    }
                }
                else
                {
                    data.flags |= ClaimedChunks.DATA_CAN_CLAIM;
                }

                chunkData[x1 + z1 * ClaimedChunks.TILES_GUI] = data;
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
        startX = io.readInt();
        startZ = io.readInt();
        claimedChunks = io.readUnsignedShort();
        loadedChunks = io.readUnsignedShort();
        maxClaimedChunks = io.readUnsignedShort();
        maxLoadedChunks = io.readUnsignedShort();

        chunkData = new ClaimedChunks.Data[ClaimedChunks.TILES_GUI * ClaimedChunks.TILES_GUI];
        teams = new HashMap<>();

        int s = io.readUnsignedShort();

        while(--s >= 0)
        {
            ClaimedChunks.Team team = new ClaimedChunks.Team();
            team.ownerID = LMNetUtils.readUUID(io);
            team.formattedName = LMNetUtils.readString(io);
            team.colorID = io.readByte();
            team.isAlly = io.readBoolean();
            teams.put(team.ownerID, team);
        }

        for(int i = 0; i < chunkData.length; i++)
        {
            chunkData[i] = new ClaimedChunks.Data();
            chunkData[i].flags = io.readByte();

            if(chunkData[i].isClaimed())
            {
                chunkData[i].team = teams.get(LMNetUtils.readUUID(io));

                if(chunkData[i].team.isAlly)
                {
                    chunkData[i].owner = LMNetUtils.readString(io);
                }
            }
        }
    }

    @Override
    public void toBytes(ByteBuf io)
    {
        io.writeInt(startX);
        io.writeInt(startZ);
        io.writeShort(claimedChunks);
        io.writeShort(loadedChunks);
        io.writeShort(maxClaimedChunks);
        io.writeShort(maxLoadedChunks);

        io.writeShort(teams.size());

        for(ClaimedChunks.Team t : teams.values())
        {
            LMNetUtils.writeUUID(io, t.ownerID);
            LMNetUtils.writeString(io, t.formattedName);
            io.writeByte(t.colorID);
            io.writeBoolean(t.isAlly);
        }

        for(int i = 0; i < chunkData.length; i++)
        {
            io.writeByte(chunkData[i].flags);

            if(chunkData[i].isClaimed())
            {
                LMNetUtils.writeUUID(io, chunkData[i].team.ownerID);

                if(chunkData[i].team.isAlly)
                {
                    LMNetUtils.writeString(io, chunkData[i].owner);
                }
            }
        }
    }

    @Override
    public void onMessage(MessageClaimedChunksUpdate m)
    {
        if(GuiClaimedChunks.instance != null)
        {
            GuiClaimedChunks.instance.setData(m.claimedChunks, m.loadedChunks, m.maxClaimedChunks, m.maxLoadedChunks, m.chunkData, m.teams);
        }
    }
}