package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbl.util.LMDimUtils;
import com.feed_the_beast.ftbl.util.PrivacyLevel;
import com.feed_the_beast.ftbu.FTBUCapabilities;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.handlers.FTBUChunkEventHandler;
import com.feed_the_beast.ftbu.net.MessageAreaUpdate;
import com.google.gson.JsonArray;
import com.mojang.authlib.GameProfile;
import latmod.lib.IntMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;
import java.util.UUID;

/**
 * Created by LatvianModder on 23.02.2016.
 */
public class FTBUPlayerDataMP extends FTBUPlayerData implements INBTSerializable<NBTTagCompound>
{
    public static FTBUPlayerDataMP get(ForgePlayerMP p)
    {
        return p.hasCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null) ? (FTBUPlayerDataMP) p.getCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null) : null;
    }
    
    public Warps homes;
    public ChunkType lastChunkType;
    
    public FTBUPlayerDataMP()
    {
        homes = new Warps();
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
            map.putIfNot0(10, getClaimedChunks(player.getProfile().getId()));
            map.putIfNot0(11, getLoadedChunks(player.getProfile().getId(), true));
            map.putIfNot0(12, FTBUPermissions.claims_max_chunks.get(player.getProfile()).getAsShort());
            map.putIfNot0(13, FTBUPermissions.chunkloader_max_chunks.get(player.getProfile()).getAsShort());
        }
        
        tag.setIntArray("F", map.toArray());
    }
    
    public static int getClaimedChunks(UUID id)
    { return FTBUWorldDataMP.get().getChunks(id, null).size(); }
    
    public static int getLoadedChunks(UUID id, boolean forced)
    {
        int loaded = 0;
        for(ClaimedChunk c : FTBUWorldDataMP.get().getChunks(id, null))
        {
            if(c.isChunkloaded && (!forced || c.isForced))
            {
                loaded++;
            }
        }
        
        return loaded;
    }
    
    public static short getMaxLoadedChunks(GameProfile profile)
    { return FTBUPermissions.chunkloader_max_chunks.get(profile).getAsShort(); }
    
    public static boolean isDimensionBlacklisted(GameProfile profile, DimensionType dim)
    {
        JsonArray a = FTBUPermissions.claims_dimension_blacklist.get(profile).getAsJsonArray();
        
        for(int i = 0; i < a.size(); i++)
        {
            if(a.get(i).getAsInt() == dim.getId()) { return true; }
        }
        
        return false;
    }
    
    public static void claimChunk(ForgePlayer player, ChunkDimPos pos)
    {
        if(isDimensionBlacklisted(player.getProfile(), pos.dim)) { return; }
        short max = FTBUPermissions.claims_max_chunks.get(player.getProfile()).getAsShort();
        if(max == 0) { return; }
        if(getClaimedChunks(player.getProfile().getId()) >= max) { return; }
        
        ChunkType t = FTBUWorldDataMP.get().getType(player.toPlayerMP(), pos);
        if(t.asClaimed() == null && t.isChunkOwner(player.toPlayerMP()) && FTBUWorldDataMP.get().put(new ClaimedChunk(player.getProfile().getId(), pos)))
        {
            player.sendUpdate();
        }
    }
    
    public static void unclaimChunk(ForgePlayer player, ChunkDimPos pos)
    {
        if(FTBUWorldDataMP.get().getType(player.toPlayerMP(), pos).isChunkOwner(player.toPlayerMP()))
        {
            setLoaded(player, pos, false);
            FTBUWorldDataMP.get().remove(pos);
            player.sendUpdate();
        }
    }
    
    public static void unclaimAllChunks(ForgePlayer player, DimensionType dim)
    {
        List<ClaimedChunk> list = FTBUWorldDataMP.get().getChunks(player.getProfile().getId(), dim);
        int size0 = list.size();
        if(size0 == 0) { return; }
        
        for(ClaimedChunk c : list)
        {
            setLoaded(player, c.pos, false);
            FTBUWorldDataMP.get().remove(c.pos);
        }
        
        player.sendUpdate();
    }
    
    public static void setLoaded(ForgePlayer player, ChunkDimPos pos, boolean flag)
    {
        ClaimedChunk chunk = FTBUWorldDataMP.get().getChunk(pos);
        if(chunk == null) { return; }
        
        if(flag != chunk.isChunkloaded && player.equalsPlayer(chunk.getOwner()))
        {
            if(flag)
            {
                if(isDimensionBlacklisted(player.getProfile(), pos.dim)) { return; }
                short max = getMaxLoadedChunks(player.getProfile());
                if(max == 0) { return; }
                if(getLoadedChunks(player.getProfile().getId(), false) >= max) { return; }
            }
            
            chunk.isChunkloaded = flag;
            FTBUChunkEventHandler.instance.markDirty(LMDimUtils.getWorld(pos.dim));
            
            if(player.getPlayer() != null)
            {
                new MessageAreaUpdate(player.toPlayerMP(), pos.chunkXPos, pos.chunkZPos, pos.dim, 1, 1).sendTo(player.toPlayerMP().getPlayer());
                player.sendUpdate();
            }
        }
    }
}
