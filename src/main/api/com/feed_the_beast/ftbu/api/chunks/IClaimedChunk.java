package com.feed_the_beast.ftbu.api.chunks;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;

/**
 * Created by LatvianModder on 03.10.2016.
 */
public interface IClaimedChunk
{
    ChunkDimPos getPos();

    IForgePlayer getOwner();

    boolean hasUpgrade(IChunkUpgrade upgrade);

    void setHasUpgrade(IChunkUpgrade upgrade, boolean v);
    
    void playerEnteredChunk(IForgePlayer player);
    
    void playerInChunkFromNBT(Calendar entertime, Calendar leavetime, String staytime, IForgePlayer player);
    
    Collection<IPlayerInChunk> getAllPlayersInChunk();
    
    void playerLeft(IForgePlayer player);
    
    public ArrayList<IPlayerInChunk> getPlayer(IForgePlayer player);
    
    public void tick();
}