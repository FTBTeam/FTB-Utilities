package com.feed_the_beast.ftbu.world.data;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.api.config.ConfigEntryBool;
import com.feed_the_beast.ftbl.api.config.ConfigEntryEnum;
import com.feed_the_beast.ftbl.api.security.EnumTeamPrivacyLevel;
import com.feed_the_beast.ftbu.FTBUCapabilities;
import com.feed_the_beast.ftbu.world.chunks.ClaimedChunk;
import com.latmod.lib.io.Bits;
import com.latmod.lib.math.ChunkDimPos;
import com.latmod.lib.util.LMUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Created by LatvianModder on 11.02.2016.
 */
public class FTBUTeamData implements ICapabilitySerializable<NBTTagCompound>
{
    public final ConfigEntryEnum<EnumTeamPrivacyLevel> blocks;
    public final ConfigEntryBool disable_explosions;
    public final ConfigEntryBool fakePlayers;

    public FTBUTeamData()
    {
        blocks = new ConfigEntryEnum<>(EnumTeamPrivacyLevel.ALLIES, EnumTeamPrivacyLevel.NAME_MAP);
        disable_explosions = new ConfigEntryBool(false);
        fakePlayers = new ConfigEntryBool(false);
    }

    public static FTBUTeamData get(IForgeTeam t)
    {
        return t.hasCapability(FTBUCapabilities.FTBU_TEAM_DATA, null) ? t.getCapability(FTBUCapabilities.FTBU_TEAM_DATA, null) : null;
    }

    @Override
    public final boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == FTBUCapabilities.FTBU_TEAM_DATA;
    }

    @Override
    public final <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if(capability == FTBUCapabilities.FTBU_TEAM_DATA)
        {
            return (T) this;
        }

        return null;
    }

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
                IForgePlayer player = FTBLibAPI.get().getWorld().getPlayer(LMUtils.fromString(s));

                if(player != null && player.isMemberOf(FTBLibAPI.get().getWorld().getCurrentTeam()))
                {
                    NBTTagList list = tag1.getTagList(s, Constants.NBT.TAG_INT_ARRAY);

                    for(int i = 0; i < list.tagCount(); i++)
                    {
                        int[] ai = list.getIntArrayAt(i);

                        if(ai.length >= 3)
                        {
                            ClaimedChunk chunk = new ClaimedChunk(player, new ChunkDimPos(ai[1], ai[2], ai[0]));

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

        for(IForgePlayer player : FTBLibAPI.get().getWorld().getCurrentTeam().getMembers())
        {
            Collection<ClaimedChunk> chunks = FTBUWorldDataMP.chunks.getChunks(player.getProfile().getId());

            if(!chunks.isEmpty())
            {
                NBTTagList tag1 = new NBTTagList();

                for(ClaimedChunk c : chunks)
                {
                    int ai[] = c.loaded ? new int[4] : new int[3];

                    ai[0] = c.pos.dim;
                    ai[1] = c.pos.posX;
                    ai[2] = c.pos.posZ;

                    if(c.loaded)
                    {
                        ai[3] = 1;
                    }

                    tag1.appendTag(new NBTTagIntArray(ai));
                }

                chunksTag.setTag(LMUtils.fromUUID(player.getProfile().getId()), tag1);
            }
        }

        if(!chunksTag.hasNoTags())
        {
            tag.setTag("ClaimedChunks", chunksTag);
        }

        return tag;
    }
}