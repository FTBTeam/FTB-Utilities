package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.EnumTeamPrivacyLevel;
import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.ForgeTeam;
import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.api.config.ConfigEntryBool;
import com.feed_the_beast.ftbl.api.config.ConfigEntryEnum;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import latmod.lib.IntMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Collection;

/**
 * Created by LatvianModder on 01.06.2016.
 */
public class FTBUTeamDataMP extends FTBUTeamData implements INBTSerializable<NBTTagCompound>
{
    public final ConfigEntryEnum<EnumTeamPrivacyLevel> blocks;
    public final ConfigEntryBool explosions;
    public final ConfigEntryBool fakePlayers;

    public FTBUTeamDataMP()
    {
        blocks = new ConfigEntryEnum<>("blocks", EnumTeamPrivacyLevel.values(), EnumTeamPrivacyLevel.ALLIES, false);
        explosions = new ConfigEntryBool("explosions", true);
        fakePlayers = new ConfigEntryBool("fake_players", false);
    }

    @Override
    public FTBUTeamDataMP toMP()
    {
        return this;
    }

    public void writeSyncData(ForgeTeam team, NBTTagCompound tag, ForgePlayer player)
    {
        IntMap map = new IntMap();

        if(explosions.getAsBoolean())
        {
            map.put(0, 1);
        }

        if(fakePlayers.getAsBoolean())
        {
            map.put(1, 1);
        }

        map.put(2, blocks.getIndex());

        if(!map.list.isEmpty())
        {
            tag.setIntArray("F", map.toArray());
        }
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag)
    {
        byte flags = tag.getByte("flags");
        blocks.setIndex(tag.hasKey("BlockSecurity") ? tag.getByte("BlockSecurity") : blocks.defValue);

        if(tag.hasKey("ClaimedChunks"))
        {
            NBTTagList tag1 = tag.getTagList("ClaimedChunks", Constants.NBT.TAG_INT_ARRAY);

            for(int i = 0; i < tag1.tagCount(); i++)
            {
                int[] ai = tag1.getIntArrayAt(i);

                if(ai.length >= 3)
                {
                    ClaimedChunk chunk = new ClaimedChunk(ForgeWorldMP.inst, ForgeWorldMP.currentPlayer, new ChunkDimPos(ai[0], ai[1], ai[2]));

                    if(ai.length >= 4)
                    {
                        chunk.loaded = ai[3] != 0;
                    }

                    ClaimedChunks.inst.putChunk(chunk);
                }
            }
        }
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();

        byte flags = 0;

        if(flags != 0)
        {
            tag.setByte("Flags", flags);
        }

        if(!blocks.isDefault())
        {
            tag.setByte("BlockSecurity", (byte) blocks.getIndex());
        }

        Collection<ClaimedChunk> chunks = ClaimedChunks.inst.getChunks(ForgeWorldMP.currentPlayer.getProfile().getId(), null);

        if(!chunks.isEmpty())
        {
            NBTTagList tag1 = new NBTTagList();

            for(ClaimedChunk c : chunks)
            {
                int ai[] = c.loaded ? new int[4] : new int[3];

                ai[0] = c.pos.dim;
                ai[1] = c.pos.chunkXPos;
                ai[2] = c.pos.chunkZPos;

                if(c.loaded)
                {
                    ai[3] = flags & 0xFF;
                }

                tag1.appendTag(new NBTTagIntArray(ai));
            }

            tag.setTag("ClaimedChunks", tag1);
        }

        return tag;
    }
}