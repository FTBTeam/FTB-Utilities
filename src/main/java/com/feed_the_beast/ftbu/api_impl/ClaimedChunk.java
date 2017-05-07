package com.feed_the_beast.ftbu.api_impl;

import java.util.ArrayList;
import java.util.Calendar;

import javax.annotation.Nullable;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.io.Bits;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.chunks.IChunkUpgrade;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import com.feed_the_beast.ftbu.api.chunks.IPlayerInChunk;
import com.feed_the_beast.ftbu.config.FTBUConfigWorld;
import com.feed_the_beast.ftbu.world.FTBUTeamData;

import net.minecraftforge.server.permission.PermissionAPI;

/**
 * Created by LatvianModder on 03.10.2016.
 */
public class ClaimedChunk implements IClaimedChunk
{
    private final ChunkDimPos pos;
    private final IForgePlayer owner;
    private int flags;
    private ArrayList<IPlayerInChunk> playersInChunk = new ArrayList<IPlayerInChunk>();

    public ClaimedChunk(ChunkDimPos c, IForgePlayer p, int f)
    {
        pos = c;
        owner = p;
        flags = f;
    }

    @Override
    public ChunkDimPos getPos()
    {
        return pos;
    }

    @Override
    public IForgePlayer getOwner()
    {
        return owner;
    }

    @Override
    public boolean hasUpgrade(IChunkUpgrade upgrade)
    {
        if(upgrade == ChunkUpgrade.SHOULD_FORCE)
        {
            if(!FTBUConfigWorld.CHUNK_LOADING.getBoolean() || !hasUpgrade(ChunkUpgrade.LOADED) || !FTBUPermissions.canUpgradeChunk(owner.getProfile(), ChunkUpgrade.LOADED))
            {
                return false;
            }
            else if(!owner.isOnline())
            {
                /*
                double max = FTBUtilitiesAPI_Impl.INSTANCE.getRankConfig(owner.getProfile(), FTBUPermissions.CHUNKLOADER_OFFLINE_TIMER).getDouble();

                if(max == 0 || (max > 0 && FTBLibStats.getLastSeenDeltaInHours(owner.stats(), false) > max))
                */
                if(!PermissionAPI.hasPermission(owner.getProfile(), FTBUPermissions.CHUNKLOADER_LOAD_OFFLINE, null))
                {
                    return false;
                }
            }

            return true;
        }
        else if(upgrade == ChunkUpgrade.NO_EXPLOSIONS)
        {
            if(!FTBUPermissions.canUpgradeChunk(owner.getProfile(), ChunkUpgrade.NO_EXPLOSIONS))
            {
                return false;
            }

            FTBUTeamData data = owner.getTeam() == null ? null : FTBUTeamData.get(owner.getTeam());
            return data != null && !data.explosions.getBoolean();
        }

        return Bits.getFlag(flags, 1 << upgrade.getId());
    }

    @Override
    public void setHasUpgrade(IChunkUpgrade upgrade, boolean v)
    {
        flags = Bits.setFlag(flags, 1 << upgrade.getId(), v);
    }

    @Override
    public void playerEnteredChunk(IForgePlayer player) {
    	playersInChunk.add(0, new PlayerInChunk(player));
    }
    
    @Override
    public void playerInChunkFromNBT(Calendar entertime, Calendar leavetime, String staytime, IForgePlayer player) {
    	playersInChunk.add(0, new PlayerInChunk(entertime, leavetime, staytime, player));
    }
    
    @Override
    public ArrayList<IPlayerInChunk> getAllPlayersInChunk() {
    	return playersInChunk;
    }
    
    @Override
    public void playerLeft(IForgePlayer player) {
    	ArrayList<IPlayerInChunk> players = getPlayer(player);
    	players.get(0).playerLeft();
    }
    
    @Override
    @Nullable
    public ArrayList<IPlayerInChunk> getPlayer(IForgePlayer player) {
    	ArrayList<IPlayerInChunk> players = new ArrayList<IPlayerInChunk>();
    	for (IPlayerInChunk playerInChunk : playersInChunk) {
    		if (playerInChunk.getPlayer().getName() == player.getName()) {
    			players.add(playerInChunk);
    		}
    	}
    	return players;
    }

    @Override
    public void tick() {
    	final ArrayList<IPlayerInChunk> toRemove = new ArrayList<IPlayerInChunk>();
    	for (IPlayerInChunk playerInChunk : playersInChunk) {
    		if (playerInChunk.getLeaveTime() != null) {
    			long currentHourMinusSix = Calendar.getInstance().getTimeInMillis() / 1000 / 60 / 60 - 6;
    		    if (playerInChunk.getLeaveTimeCalendar().getTimeInMillis() / 1000 / 60 / 60 < currentHourMinusSix) {
        			toRemove.add(playerInChunk);
        		}
    		}
    	}
    	getAllPlayersInChunk().removeAll(toRemove);
    }
}