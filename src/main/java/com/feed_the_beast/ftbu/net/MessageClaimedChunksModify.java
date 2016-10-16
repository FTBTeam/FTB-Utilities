package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbl.lib.math.MathHelperLM;
import com.feed_the_beast.ftbl.lib.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.lib.net.MessageToServer;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunkStorage;
import com.feed_the_beast.ftbu.handlers.FTBUPlayerEventHandler;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.BlockPosContext;

import java.util.ArrayList;
import java.util.Collection;

public class MessageClaimedChunksModify extends MessageToServer<MessageClaimedChunksModify>
{
    public static final byte CLAIM = 0;
    public static final byte UNCLAIM = 1;
    public static final byte LOAD = 2;
    public static final byte UNLOAD = 3;

    private int startX, startZ;
    private byte action;
    private Collection<ChunkPos> chunks;

    public MessageClaimedChunksModify()
    {
    }

    public MessageClaimedChunksModify(int sx, int sz, byte a, Collection<ChunkPos> c)
    {
        startX = sx;
        startZ = sz;
        action = a;
        chunks = c;
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
        action = io.readByte();

        int s = io.readUnsignedByte();
        chunks = new ArrayList<>(s);

        while(--s >= 0)
        {
            int x = io.readInt();
            int z = io.readInt();
            chunks.add(new ChunkPos(x, z));
        }
    }

    @Override
    public void toBytes(ByteBuf io)
    {
        io.writeInt(startX);
        io.writeInt(startZ);
        io.writeByte(action);
        io.writeByte(chunks.size());

        for(ChunkPos p : chunks)
        {
            io.writeInt(p.chunkXPos);
            io.writeInt(p.chunkZPos);
        }
    }

    @Override
    public void onMessage(MessageClaimedChunksModify m, EntityPlayerMP player)
    {
        IForgePlayer p = FTBLibIntegration.API.getUniverse().getPlayer(player);

        if(p == null)
        {
            return;
        }

        if(!PermissionAPI.hasPermission(player, FTBUPermissions.CLAIMS_CLAIM_CHUNKS)) {
            return;
        }

        for(ChunkPos pos0 : m.chunks)
        {
            ChunkDimPos pos = new ChunkDimPos(pos0.chunkXPos, pos0.chunkZPos, player.dimension);

            switch(m.action)
            {
                case CLAIM:
                    FTBUUniverseData.claimChunk(p, pos);
                    break;
                case UNCLAIM:
                    if(p.equalsPlayer(ClaimedChunkStorage.INSTANCE.getChunkOwner(pos)) || PermissionAPI.hasPermission(player.getGameProfile(), FTBUPermissions.CLAIMS_MODIFY_OTHER_CHUNKS, new BlockPosContext(player, pos.getChunkPos())))
                    {
                        FTBUUniverseData.unclaimChunk(p, pos);
                    }
                    break;
                case LOAD:
                    FTBUUniverseData.setLoaded(p, pos, true);
                    break;
                case UNLOAD:
                    FTBUUniverseData.setLoaded(p, pos, false);
                    break;
            }
        }

        FTBUPlayerEventHandler.updateChunkMessage(player, new ChunkDimPos(MathHelperLM.chunk(player.posX), MathHelperLM.chunk(player.posZ), player.dimension));
        new MessageClaimedChunksUpdate(m.startX, m.startZ, player).sendTo(player);
    }
}