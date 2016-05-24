package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.api.net.MessageToClient;
import com.feed_the_beast.ftbl.util.BlockDimPos;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbu.world.ChunkType;
import com.feed_the_beast.ftbu.world.ClaimedChunks;
import com.feed_the_beast.ftbu.world.FTBUWorldDataSP;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Map;

public class MessageAreaUpdate extends MessageToClient<MessageAreaUpdate>
{
    public int dim;
    public Map<ChunkDimPos, ChunkType> types;

    //Only on server side
    private ForgePlayerMP player;

    public MessageAreaUpdate()
    {
    }

    public MessageAreaUpdate(ForgePlayerMP p, int x, int z, int d, int sx, int sz)
    {
        player = p;
        dim = d;

        types = new HashMap<>();

        for(int x1 = x; x1 < x + sx; x1++)
        {
            for(int z1 = z; z1 < z + sz; z1++)
            {
                ChunkDimPos pos = new ChunkDimPos(d, x1, z1);
                ChunkType type = ClaimedChunks.inst.getType(player, pos);

                if(type != ChunkType.UNLOADED)
                {
                    types.put(pos, type);
                }
            }
        }
    }

    public MessageAreaUpdate(ForgePlayerMP p, BlockDimPos pos, int radius)
    {
        this(p, pos.chunkX() - radius, pos.chunkZ() - radius, pos.dim, radius * 2 + 1, radius * 2 + 1);
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
        types = new HashMap<>();

        int s = io.readInt();

        for(int i = 0; i < s; i++)
        {
            int x = io.readInt();
            int z = io.readInt();
            ChunkDimPos pos = new ChunkDimPos(dim, x, z);
            int id = io.readUnsignedByte();

            if(id == 99)
            {
                ChunkType.PlayerClaimed type = new ChunkType.PlayerClaimed();
                type.readFromNBT(readTag(io), pos);
                types.put(pos, type);
            }
            else
            {
                types.put(pos, ChunkType.VALUES[id]);
            }
        }
    }

    @Override
    public void toBytes(ByteBuf io)
    {
        io.writeInt(dim);
        io.writeInt(types.size());

        NBTTagCompound tag;

        for(Map.Entry<ChunkDimPos, ChunkType> e : types.entrySet())
        {
            io.writeInt(e.getKey().chunkXPos);
            io.writeInt(e.getKey().chunkZPos);

            ChunkType t = e.getValue();
            io.writeByte(t.ID);

            if(t.ID == 99)
            {
                tag = new NBTTagCompound();
                t.writeToNBT(tag, player);
                writeTag(io, tag);
            }
        }
    }

    @Override
    public void onMessage(MessageAreaUpdate m, Minecraft mc)
    {
        FTBUWorldDataSP.setTypes(m.types);
    }
}