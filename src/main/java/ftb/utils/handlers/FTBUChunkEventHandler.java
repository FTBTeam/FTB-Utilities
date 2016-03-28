package ftb.utils.handlers;

import com.google.common.collect.MapMaker;
import ftb.lib.*;
import ftb.lib.api.*;
import ftb.utils.*;
import ftb.utils.config.FTBUConfigGeneral;
import ftb.utils.world.*;
import latmod.lib.LMUtils;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.util.Constants;

import java.util.*;

public class FTBUChunkEventHandler implements ForgeChunkManager.LoadingCallback, ForgeChunkManager.OrderedLoadingCallback
{
	public static final FTBUChunkEventHandler instance = new FTBUChunkEventHandler();
	private final Map<World, Map<UUID, ForgeChunkManager.Ticket>> table = new MapMaker().weakKeys().makeMap();
	private static final String PLAYER_ID_TAG = "PID";
	
	public void init()
	{
		if(!ForgeChunkManager.getConfig().hasCategory(FTBUFinals.MOD_ID))
		{
			ForgeChunkManager.getConfig().get(FTBUFinals.MOD_ID, "maximumTicketCount", 2000).setMinValue(0);
			ForgeChunkManager.getConfig().get(FTBUFinals.MOD_ID, "maximumChunksPerTicket", 30000).setMinValue(0);
			ForgeChunkManager.getConfig().save();
		}
		
		EventBusHelper.register(this);
		ForgeChunkManager.setForcedChunkLoadingCallback(FTBU.inst, this);
	}
	
	private ForgeChunkManager.Ticket request(World w, ForgePlayerMP player)
	{
		if(w == null || player == null) return null;
		
		UUID playerID = player.getProfile().getId();
		
		Map<UUID, ForgeChunkManager.Ticket> map = table.get(w);
		ForgeChunkManager.Ticket t = (map == null) ? null : map.get(playerID);
		
		if(t == null)
		{
			t = ForgeChunkManager.requestTicket(FTBU.inst, w, ForgeChunkManager.Type.NORMAL);
			if(t == null) return null;
			else
			{
				t.getModData().setString(PLAYER_ID_TAG, LMUtils.fromUUID(playerID));
				
				if(map == null)
				{
					map = new HashMap<>();
					table.put(w, map);
				}
				
				map.put(playerID, t);
			}
		}
		
		return t;
	}
	
	public List<ForgeChunkManager.Ticket> ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world, int maxTicketCount)
	{
		table.remove(world);
		List<ForgeChunkManager.Ticket> tickets1 = new ArrayList<>();
		if(tickets.isEmpty() || FTBUConfigGeneral.disable_chunkloading.getAsBoolean()) return tickets1;
		Map<UUID, ForgeChunkManager.Ticket> map = new HashMap<>();
		
		for(ForgeChunkManager.Ticket t : tickets)
		{
			if(t.getModData().getTagId(PLAYER_ID_TAG) == Constants.NBT.TAG_STRING)
			{
				UUID playerID = LMUtils.fromString(t.getModData().getString(PLAYER_ID_TAG));
				
				if(playerID != null)
				{
					map.put(playerID, t);
					tickets1.add(t);
				}
			}
		}
		
		table.put(world, map);
		return tickets1;
	}
	
	public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world)
	{
		for(ForgeChunkManager.Ticket t : tickets)
		{
			UUID playerID = LMUtils.fromString(t.getModData().getString(PLAYER_ID_TAG));
			
			if(playerID != null)
			{
				List<ClaimedChunk> chunks = FTBUWorldDataMP.get().getChunks(playerID, world.provider.getDimensionId());
				
				if(chunks != null) for(ClaimedChunk c : chunks)
				{
					if(c.isChunkloaded)
					{
						ForgeChunkManager.forceChunk(t, c.pos);
					}
				}
			}
		}
		
		// force chunks //
		markDirty(world);
	}
	
	public void markDirty(World w)
	{
		if(ForgeWorldMP.inst == null || FTBLib.getServerWorld() == null) return;
		if(w != null) markDirty0(w);
		
		if(!table.isEmpty())
		{
			World[] worlds = table.keySet().toArray(new World[table.size()]);
			for(World w1 : worlds)
				markDirty0(w1);
		}
	}
	
	private void markDirty0(World w)
	{
		/*int total = 0;
		int totalLoaded = 0;
		int markedLoaded = 0;
		int loaded = 0;
		int unloaded = 0;*/
		
		for(ClaimedChunk c : FTBUWorldDataMP.get().getAllChunks(w.provider.getDimensionId()))
		{
			//total++;
			
			boolean isLoaded = c.isChunkloaded;
			
			if(c.isChunkloaded)
			{
				ForgePlayerMP p = c.getOwner();
				if(p == null) isLoaded = false;
				else
				{
					ChunkloaderType type = FTBUPermissions.chunkloader_type.getEnum(p.getProfile());
					
					if(type == ChunkloaderType.DISABLED) isLoaded = false;
					else if(type == ChunkloaderType.ONLINE) isLoaded = p.isOnline();
					else if(type == ChunkloaderType.OFFLINE)
					{
						if(!p.isOnline())
						{
							double max = FTBUPermissions.chunkloader_offline_timer.get(p.getProfile()).getAsDouble();
							
							if(max > 0D && p.stats.getLastSeenDeltaInHours(p) > max)
							{
								isLoaded = false;
								if(c.isForced)
								{
									FTBU.logger.info("Unloading " + p.getProfile().getName() + " chunks for being offline for too long");
								}
							}
						}
					}
				}
			}
			
			//if(isLoaded) totalLoaded++;
			//if(c.isChunkloaded) markedLoaded++;
			
			if(c.isForced != isLoaded)
			{
				ForgeChunkManager.Ticket ticket = request(LMDimUtils.getWorld(c.pos.dim), c.getOwner());
				
				if(ticket != null)
				{
					if(isLoaded)
					{
						ForgeChunkManager.forceChunk(ticket, c.pos);
						//loaded++;
					}
					else
					{
						ForgeChunkManager.unforceChunk(ticket, c.pos);
						//unloaded++;
					}
					
					c.isForced = isLoaded;
				}
			}
		}
		
		//FTBLib.dev_logger.info("Total: " + total + ", Loaded: " + totalLoaded + "/" + markedLoaded + ", DLoaded: " + loaded + ", DUnloaded: " + unloaded);
	}
	
	private void releaseTicket(ForgeChunkManager.Ticket t)
	{
		if(t.getModData().hasKey(PLAYER_ID_TAG))
		{
			Map<UUID, ForgeChunkManager.Ticket> map = table.get(t.world);
			
			if(map != null)
			{
				map.remove(LMUtils.fromString(t.getModData().getString(PLAYER_ID_TAG)));
				
				if(map.isEmpty())
				{
					table.remove(t.world);
				}
			}
		}
		
		ForgeChunkManager.releaseTicket(t);
	}
	
	public void clear()
	{
		table.clear();
	}
}