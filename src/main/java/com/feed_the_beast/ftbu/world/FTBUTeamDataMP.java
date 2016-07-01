package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.EnumTeamPrivacyLevel;
import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.api.config.ConfigEntryBool;
import com.feed_the_beast.ftbl.api.config.ConfigEntryEnum;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.latmod.lib.io.Bits;
import com.latmod.lib.util.LMUtils;
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
    public final ConfigEntryBool disable_explosions;
    public final ConfigEntryBool fakePlayers;

    public FTBUTeamDataMP()
    {
        blocks = new ConfigEntryEnum<>(EnumTeamPrivacyLevel.ALLIES, EnumTeamPrivacyLevel.NAME_MAP);
        disable_explosions = new ConfigEntryBool(false);
        fakePlayers = new ConfigEntryBool(false);
    }

    @Override
    public FTBUTeamDataMP toMP()
    {
        return this;
    }

    //public void writeSyncData(ForgeTeam team, NBTTagCompound tag, ForgePlayer player)
    //{
    //}

    @Override
    public void deserializeNBT(NBTTagCompound tag)
    {
        byte flags = tag.getByte("Flags");

        disable_explosions.set(Bits.getBit(flags, (byte) 0));
        fakePlayers.set(Bits.getBit(flags, (byte) 1));

        blocks.setIndex(tag.hasKey("BlockSecurity") ? tag.getByte("BlockSecurity") : blocks.defValue);

        if(tag.hasKey("ClaimedChunks"))
        {
            NBTTagCompound tag1 = tag.getCompoundTag("ClaimedChunks");

            for(String s : tag1.getKeySet())
            {
                ForgePlayer player = ForgeWorldMP.inst.getPlayer(LMUtils.fromString(s));

                if(player != null && player.isMemberOf(ForgeWorldMP.currentTeam))
                {
                    NBTTagList list = tag1.getTagList(s, Constants.NBT.TAG_INT_ARRAY);

                    for(int i = 0; i < list.tagCount(); i++)
                    {
                        int[] ai = list.getIntArrayAt(i);

                        if(ai.length >= 3)
                        {
                            ClaimedChunk chunk = new ClaimedChunk(ForgeWorldMP.inst, player, new ChunkDimPos(ai[0], ai[1], ai[2]));

                            if(ai.length >= 4)
                            {
                                chunk.loaded = ai[3] != 0;
                            }

                            FTBUWorldDataMP.chunks.put(chunk.pos, chunk);
                        }
                    }
                }
            }
        }
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();

        byte flags = 0;

        flags = Bits.setBit(flags, (byte) 0, disable_explosions.getAsBoolean());
        flags = Bits.setBit(flags, (byte) 1, fakePlayers.getAsBoolean());

        if(flags != 0)
        {
            tag.setByte("Flags", flags);
        }

        if(!blocks.isDefault())
        {
            tag.setByte("BlockSecurity", (byte) blocks.getIndex());
        }

        NBTTagCompound chunksTag = new NBTTagCompound();

        for(ForgePlayer player : ForgeWorldMP.currentTeam.getMembers())
        {
            Collection<ClaimedChunk> chunks = FTBUWorldDataMP.chunks.getChunks(player.getProfile().getId());

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
                        ai[3] = 1;
                    }

                    tag1.appendTag(new NBTTagIntArray(ai));
                }

                chunksTag.setTag(player.getStringUUID(), tag1);
            }
        }

        if(!chunksTag.hasNoTags())
        {
            tag.setTag("ClaimedChunks", chunksTag);
        }

        return tag;
    }
}