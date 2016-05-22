package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbl.util.PrivacyLevel;
import com.feed_the_beast.ftbu.FTBUPermissions;
import latmod.lib.Bits;
import latmod.lib.IntMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Collection;
import java.util.UUID;

/**
 * Created by LatvianModder on 23.02.2016.
 */
public class FTBUPlayerDataMP extends FTBUPlayerData implements INBTSerializable<NBTTagCompound>
{
    public Warps homes;
    public ChunkType lastChunkType;

    public FTBUPlayerDataMP()
    {
        homes = new Warps();
    }

    @Override
    public FTBUPlayerDataMP toMP()
    {
        return this;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag)
    {
        flags = tag.getByte("Flags");
        blocks = tag.hasKey("BlockSecurity") ? PrivacyLevel.VALUES_3[tag.getByte("BlockSecurity")] : PrivacyLevel.FRIENDS;
        homes.readFromNBT(tag, "Homes");

        if(tag.hasKey("ClaimedChunks"))
        {
            NBTTagList tag1 = tag.getTagList("ClaimedChunks", Constants.NBT.TAG_INT_ARRAY);

            UUID id = ForgeWorldMP.currentPlayer.getProfile().getId();

            for(int i = 0; i < tag1.tagCount(); i++)
            {
                int[] ai = tag1.getIntArrayAt(i);

                if(ai.length >= 3)
                {
                    ClaimedChunk chunk = new ClaimedChunk(id, new ChunkDimPos(ai[0], ai[1], ai[2]));

                    if(ai.length >= 4)
                    {
                        chunk.flags = (byte) ai[3];
                    }

                    ClaimedChunks.inst.chunks.put(chunk.pos, chunk);
                }
            }
        }
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();

        if(flags != 0)
        {
            tag.setByte("Flags", flags);
        }

        if(blocks != PrivacyLevel.FRIENDS)
        {
            tag.setByte("BlockSecurity", (byte) blocks.ordinal());
        }

        homes.writeToNBT(tag, "Homes");

        Collection<ClaimedChunk> chunks = ClaimedChunks.inst.getChunks(ForgeWorldMP.currentPlayer.getProfile().getId(), null);

        if(!chunks.isEmpty())
        {
            NBTTagList tag1 = new NBTTagList();

            for(ClaimedChunk c : chunks)
            {
                byte flags = Bits.setBit(c.flags, ClaimedChunk.FORCED, false);

                int ai[] = (flags == 0) ? new int[3] : new int[4];

                ai[0] = c.pos.dim;
                ai[1] = c.pos.chunkXPos;
                ai[2] = c.pos.chunkZPos;

                if(flags != 0)
                {
                    ai[3] = flags & 0xFF;
                }

                tag1.appendTag(new NBTTagIntArray(ai));
            }

            tag.setTag("ClaimedChunks", tag1);
        }

        return tag;
    }

    @Override
    public void writeSyncData(ForgePlayer player, NBTTagCompound tag, boolean self)
    {
        IntMap map = new IntMap();

        map.putIfNot0(0, flags);
        map.putIfNot0(1, blocks.ordinal());

        if(self)
        {
            map.putIfNot0(10, ClaimedChunks.inst.getClaimedChunks(player.getProfile().getId()));
            map.putIfNot0(11, ClaimedChunks.inst.getLoadedChunks(player.getProfile().getId(), true));
            map.putIfNot0(12, FTBUPermissions.claims_max_chunks.get(player.getProfile()).getAsShort());
            map.putIfNot0(13, FTBUPermissions.chunkloader_max_chunks.get(player.getProfile()).getAsShort());
        }

        tag.setIntArray("F", map.toArray());
    }
}
