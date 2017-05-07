package com.feed_the_beast.ftbu.world;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nullable;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.events.player.ForgePlayerSettingsEvent;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.math.BlockDimPos;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.FTBUCommon;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.api.chunks.IChunkUpgrade;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import com.feed_the_beast.ftbu.api.chunks.IPlayerInChunk;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunk;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunkStorage;
import com.feed_the_beast.ftbu.api_impl.PlayerInChunk;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Created by LatvianModder on 11.02.2016.
 */
public class FTBUPlayerData implements INBTSerializable<NBTBase>
{
    public final PropertyBool renderBadge = new PropertyBool(true);
    public final PropertyBool chatLinks = new PropertyBool(true);
    public final PropertyBool disableGlobalBadge = new PropertyBool(false);

    public final IForgePlayer player;
    public BlockDimPos lastDeath, lastSafePos;
    public IForgePlayer lastChunkOwner;
    private Map<String, BlockDimPos> homes;

    public FTBUPlayerData(IForgePlayer p)
    {
        player = p;
    }

    @Nullable
    public static FTBUPlayerData get(IForgePlayer p)
    {
        return (FTBUPlayerData) p.getData(FTBLibIntegration.FTBU_DATA);
    }

    @Override
    public NBTBase serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();

        nbt.setBoolean("RenderBadge", renderBadge.getBoolean());
        nbt.setBoolean("ChatLinks", chatLinks.getBoolean());
        nbt.setBoolean("DisableGlobalBadges", disableGlobalBadge.getBoolean());

        if(homes != null && !homes.isEmpty())
        {
            NBTTagCompound tag1 = new NBTTagCompound();

            for(Map.Entry<String, BlockDimPos> e : homes.entrySet())
            {
                tag1.setIntArray(e.getKey(), e.getValue().toIntArray());
            }

            nbt.setTag("Homes", tag1);
        }

        if(lastDeath != null)
        {
            nbt.setIntArray("LastDeath", lastDeath.toIntArray());
        }

        Collection<IClaimedChunk> claimedChunks = ClaimedChunkStorage.INSTANCE.getChunks(player);

        if(!claimedChunks.isEmpty())
        {
            NBTTagCompound claimedChunkNBT = new NBTTagCompound();
            NBTTagList claimedChunksNBT = new NBTTagList();

            for (IClaimedChunk chunk : claimedChunks)
            {
                ChunkDimPos pos = chunk.getPos();

                int flags = 0;

                for(IChunkUpgrade upgrade : FTBUCommon.CHUNK_UPGRADES)
                {
                    if(upgrade != null && chunk.hasUpgrade(upgrade))
                    {
                        flags |= (1 << upgrade.getId());
                    }
                }

                NBTTagCompound coordinates = new NBTTagCompound();
                coordinates.setInteger("Dimension", pos.dim);
                coordinates.setInteger("X", pos.posX);
                coordinates.setInteger("Z", pos.posZ);
                claimedChunkNBT.setTag("Coordinates", coordinates);

                if(flags != 0)
                {
                    claimedChunkNBT.setInteger("Flags", flags);
                }
                
                if (!chunk.getAllPlayersInChunk().isEmpty()) {
                	NBTTagList playerInChunkList = new NBTTagList();
                	for (IPlayerInChunk playerInChunk : chunk.getAllPlayersInChunk()) {
                		NBTTagCompound tag2 = new NBTTagCompound();
                		tag2.setString("EnterTime", new SimpleDateFormat("yyyyMMWWuukkmmss").format(playerInChunk.getEnterTimeCalendar().getTime()));
                		if (playerInChunk.getLeaveTimeCalendar() != null) {
                			tag2.setString("LeaveTime", new SimpleDateFormat("yyyyMMWWuukkmmss").format(playerInChunk.getLeaveTimeCalendar().getTime()));
                			tag2.setString("StayTime", playerInChunk.getStayTime());
                		}
                		tag2.setString("Player" , playerInChunk.
                				getPlayer().
                				getName());
                		playerInChunkList.appendTag(tag2);	
                	}
                	claimedChunkNBT.setTag("PlayersInChunk", playerInChunkList);
                }
                claimedChunksNBT.appendTag(claimedChunkNBT);
            }
            nbt.setTag("ClaimedChunksNBT", claimedChunksNBT);
        }

        return nbt;
    }

    @Override
    public void deserializeNBT(NBTBase nbt0)
    {
        NBTTagCompound nbt = (NBTTagCompound) nbt0;

        if(nbt.hasKey("Homes"))
        {
            homes = new HashMap<>();

            NBTTagCompound tag1 = (NBTTagCompound) nbt.getTag("Homes");

            if(tag1 != null && !tag1.hasNoTags())
            {
                for(String s1 : tag1.getKeySet())
                {
                    setHome(s1.toLowerCase(), new BlockDimPos(tag1.getIntArray(s1)));
                }
            }
        }
        else
        {
            homes = null;
        }

        lastDeath = null;
        if(nbt.hasKey("LastDeath"))
        {
            int[] ai = nbt.getIntArray("LastDeath");
            lastDeath = (ai.length == 4) ? new BlockDimPos(ai) : null;
        }

        if(nbt.hasKey("ClaimedChunksNBT"))
        {
            NBTTagList ClaimedChunksNBT = (NBTTagList) nbt.getTag("ClaimedChunksNBT");

            for(int i = 0;
            		i < 
            		ClaimedChunksNBT.
            		tagCount();
            		i++)
            {
                NBTTagCompound claimedChunkNBT = (NBTTagCompound) ClaimedChunksNBT.get(i);

                if((claimedChunkNBT.hasKey("Flags") ? 1 : 0) + ((NBTTagCompound) claimedChunkNBT.getTag("Coordinates")).getSize() >= 3)
                {
                	NBTTagCompound coordinates = (NBTTagCompound) claimedChunkNBT.getTag("Coordinates");
                    ClaimedChunk chunk = new ClaimedChunk(new ChunkDimPos(coordinates.getInteger("X"), coordinates.getInteger("Z"), coordinates.getInteger("Dimension")), player, (claimedChunkNBT.hasKey("Flags") ? claimedChunkNBT.getInteger("Flags") : 0));
                    ClaimedChunkStorage.INSTANCE.setChunk(chunk.getPos(), chunk);
                    if (claimedChunkNBT.hasKey("PlayersInChunk")) {
                    	NBTTagList players = (NBTTagList) claimedChunkNBT.getTag("PlayersInChunk");
                    	for (int j = 0; j < players.tagCount(); j++)
                    	{
                    		NBTTagCompound player = (NBTTagCompound) players.get(j);
                    		Calendar enterTime = Calendar.getInstance();
                    		Calendar leaveTime = Calendar.getInstance();
                    		try {
                    			enterTime.setTimeInMillis(new SimpleDateFormat("yyyyMMWWuukkmmss").parse(player.getString("EnterTime")).getTime());
                    			leaveTime.setTimeInMillis(new SimpleDateFormat("yyyyMMWWuukkmmss").parse(player.getString("LeaveTime")).getTime());
                    		} catch (ParseException e) {
                    			e.printStackTrace();
                    		}
                    		chunk.playerInChunkFromNBT(enterTime, leaveTime, player.getString("StayTime"), FTBLibIntegration.API.getUniverse().getPlayer(player.getString("Player")));
                    	}
                    }
                }
            }
        }
    }

    public Collection<String> listHomes()
    {
        if(homes == null || homes.isEmpty())
        {
            return Collections.emptySet();
        }

        return homes.keySet();
    }

    @Nullable
    public BlockDimPos getHome(String s)
    {
        return homes == null ? null : homes.get(s.toLowerCase());
    }

    public boolean setHome(String s, @Nullable BlockDimPos pos)
    {
        if(pos == null)
        {
            return homes != null && homes.remove(s) != null;
        }

        if(homes == null)
        {
            homes = new HashMap<>();
        }

        return homes.put(s, pos.copy()) == null;
    }

    public int homesSize()
    {
        return homes == null ? 0 : homes.size();
    }

    public void addConfig(ForgePlayerSettingsEvent event)
    {
        String group = FTBUFinals.MOD_ID;
        event.add(group, "render_badge", renderBadge);
        event.add(group, "chat_links", chatLinks);
        event.add(group, "disable_global_badge", disableGlobalBadge);
    }
}