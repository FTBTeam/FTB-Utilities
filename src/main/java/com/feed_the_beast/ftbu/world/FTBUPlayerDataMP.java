package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.util.PrivacyLevel;
import com.feed_the_beast.ftbu.FTBUPermissions;
import latmod.lib.IntMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

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
        setFlag(RENDER_BADGE, !tag.hasKey("Badge") || tag.getBoolean("Badge"));
        setFlag(CHAT_LINKS, tag.hasKey("ChatLinks") && tag.getBoolean("ChatLinks"));
        setFlag(EXPLOSIONS, !tag.hasKey("Explosions") || tag.getBoolean("Explosions"));
        setFlag(FAKE_PLAYERS, !tag.hasKey("FakePlayers") || tag.getBoolean("FakePlayers"));
        blocks = PrivacyLevel.VALUES_3[tag.getByte("BlockSecurity")];

        homes.readFromNBT(tag, "Homes");
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setBoolean("Badge", getFlag(RENDER_BADGE));
        tag.setBoolean("ChatLinks", getFlag(CHAT_LINKS));
        tag.setBoolean("Explosions", getFlag(EXPLOSIONS));
        tag.setBoolean("FakePlayers", getFlag(FAKE_PLAYERS));
        tag.setByte("BlockSecurity", (byte) blocks.ordinal());

        homes.writeToNBT(tag, "Homes");

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
