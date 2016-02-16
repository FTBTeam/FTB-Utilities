package ftb.utils.mod.handlers.ftbl;

import com.google.gson.*;
import ftb.lib.*;
import ftb.lib.api.friends.*;
import ftb.lib.api.item.LMInvUtils;
import ftb.utils.badges.ServerBadges;
import ftb.utils.mod.FTBU;
import ftb.utils.mod.cmd.admin.CmdRestart;
import ftb.utils.mod.config.*;
import ftb.utils.mod.handlers.FTBUChunkEventHandler;
import ftb.utils.world.*;
import ftb.utils.world.claims.*;
import latmod.lib.*;
import latmod.lib.json.UUIDTypeAdapterLM;
import latmod.lib.util.EnumEnabled;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.relauncher.Side;

import java.util.*;

/**
 * Created by LatvianModder on 11.02.2016.
 */
public class FTBUWorldData extends ForgeWorldData implements ITickingLMWorld
{
	public static FTBUWorldData serverInstance = null;
	
	public HashMap<Integer, HashMap<Long, ClaimedChunk>> chunks;
	public Warps warps;
	public long nextChunkloaderUpdate;
	private long startMillis;
	private String lastRestartMessage;
	public long restartMillis;
	
	public FTBUWorldData(String id, LMWorld w)
	{
		super(id, w);
	}
	
	public void init()
	{
		if(world.side.isServer())
		{
			serverInstance = this;
			
			chunks = new HashMap<>();
			warps = new Warps();
			
			startMillis = LMUtils.millis();
			Backups.nextBackup = startMillis + FTBUConfigBackups.backupMillis();
			lastRestartMessage = "";
			
			if(FTBUConfigGeneral.restart_timer.get() > 0)
			{
				restartMillis = startMillis + (long) (FTBUConfigGeneral.restart_timer.get() * 3600D * 1000D);
				FTBU.logger.info("Server restart in " + LMStringUtils.getTimeString(restartMillis));
			}
		}
	}
	
	public List<ClaimedChunk> getAllChunks()
	{
		ArrayList<ClaimedChunk> l = new ArrayList<>();
		for(HashMap<Long, ClaimedChunk> m : chunks.values())
			l.addAll(m.values());
		return l;
	}
	
	/*
	public void onLoaded(World w)
	{
		if(w instanceof WorldServer)
		{
			
		}
	}*/
	
	public void loadData(JsonObject o)
	{
		warps.readFromJson(o, "warps");
		
		chunks.clear();
		
		if(o.has("claimed_chunks"))
		{
			JsonObject claimedChunksGroup = o.get("claimed_chunks").getAsJsonObject();
			
			for(Map.Entry<String, JsonElement> e : claimedChunksGroup.entrySet())
			{
				int dim = Integer.parseInt(e.getKey());
				
				HashMap<Long, ClaimedChunk> map = new HashMap<>();
				
				for(Map.Entry<String, JsonElement> e1 : e.getValue().getAsJsonObject().entrySet())
				{
					try
					{
						UUID id = UUIDTypeAdapterLM.getUUID(e1.getKey());
						
						if(LMWorldMP.inst.playerMap.containsKey(id))
						{
							JsonArray chunksList = e1.getValue().getAsJsonArray();
							
							for(int k = 0; k < chunksList.size(); k++)
							{
								int[] ai = LMJsonUtils.fromIntArray(chunksList.get(k));
								
								if(ai != null)
								{
									ClaimedChunk c = new ClaimedChunk(id, dim, ai[0], ai[1]);
									if(ai.length >= 3 && ai[2] == 1) c.isChunkloaded = true;
									map.put(Bits.intsToLong(ai[0], ai[1]), c);
								}
							}
						}
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
				
				chunks.put(dim, map);
			}
		}
		
		nextChunkloaderUpdate = LMUtils.millis() + 10000L;
	}
	
	public void saveData(JsonObject o)
	{
		warps.writeToJson(o, "warps");
		
		JsonObject claimedChunksGroup = new JsonObject();
		
		Comparator<Map.Entry<Integer, HashMap<Long, ClaimedChunk>>> comparator = LMMapUtils.byKeyNumbers();
		Comparator<Map.Entry<Long, ClaimedChunk>> comparator2 = LMMapUtils.byKeyNumbers();
		
		for(Map.Entry<Integer, HashMap<Long, ClaimedChunk>> e : LMMapUtils.sortedEntryList(chunks, comparator))
		{
			JsonObject o1 = new JsonObject();
			
			for(ClaimedChunk c : LMMapUtils.values(e.getValue(), comparator2))
			{
				LMPlayer p = c.getOwner(Side.SERVER);
				
				if(p != null)
				{
					String id = p.getStringUUID();
					if(!o1.has(id)) o1.add(id, new JsonArray());
					
					JsonArray a = o1.get(id).getAsJsonArray();
					
					JsonArray a1 = new JsonArray();
					a1.add(new JsonPrimitive(c.posX));
					a1.add(new JsonPrimitive(c.posZ));
					if(c.isChunkloaded) a1.add(new JsonPrimitive(1));
					a.add(a1);
				}
			}
			
			claimedChunksGroup.add(e.getKey().toString(), o1);
		}
		
		o.add("claimed_chunks", claimedChunksGroup);
	}
	
	public void readFromNet(NBTTagCompound tag)
	{
	}
	
	public void writeToNet(NBTTagCompound tag)
	{
	}
	
	public void onClosed()
	{
		if(world.side.isServer())
		{
			startMillis = restartMillis = 0L;
			serverInstance = null;
		}
	}
	
	public void onTick(WorldServer w, long now)
	{
		if(w.provider.getDimensionId() == 0)
		{
			if(restartMillis > 0L)
			{
				int secondsLeft = (int) ((restartMillis - LMUtils.millis()) / 1000L);
				
				String msg = LMStringUtils.getTimeString(secondsLeft * 1000L);
				if(!lastRestartMessage.equals(msg))
				{
					lastRestartMessage = msg;
					
					if(secondsLeft <= 0)
					{
						CmdRestart.restart();
						return;
					}
					else if(secondsLeft <= 10 || secondsLeft == 60 || secondsLeft == 300 || secondsLeft == 600 || secondsLeft == 1800)
					{
						IChatComponent c = new ChatComponentTranslation(FTBU.mod.assets + "server_restart", msg);
						c.getChatStyle().setColor(EnumChatFormatting.LIGHT_PURPLE);
						BroadcastSender.inst.addChatMessage(c);
					}
				}
			}
			
			if(Backups.nextBackup > 0L && Backups.nextBackup <= now)
			{
				Backups.run(FTBLib.getServer());
			}
			
			if(nextChunkloaderUpdate < now)
			{
				nextChunkloaderUpdate = now + 2L * 3600L;
				FTBUChunkEventHandler.instance.markDirty(null);
			}
			
			if(Backups.thread != null && Backups.thread.isDone)
			{
				Backups.thread = null;
				Backups.postBackup();
			}
			
			if(ServerBadges.thread != null && ServerBadges.thread.isDone)
			{
				ServerBadges.thread = null;
				ServerBadges.sendToPlayer(null);
			}
		}
	}
	
	public ClaimedChunk getChunk(int dim, int cx, int cz)
	{
		if(!chunks.containsKey(dim)) return null;
		return chunks.get(dim).get(Bits.intsToLong(cx, cz));
	}
	
	public List<ClaimedChunk> getChunks(UUID playerID, Integer dim)
	{
		ArrayList<ClaimedChunk> list = new ArrayList<>();
		if(playerID == null) return list;
		
		if(dim == null)
		{
			for(HashMap<Long, ClaimedChunk> map : chunks.values())
			{
				for(ClaimedChunk c : map.values())
				{
					if(c.ownerID.equals(playerID)) list.add(c);
				}
			}
		}
		else
		{
			for(ClaimedChunk c : chunks.get(dim).values())
			{
				if(c.ownerID.equals(playerID)) list.add(c);
			}
		}
		
		return list;
	}
	
	public boolean put(ClaimedChunk c)
	{
		if(c == null) return false;
		HashMap<Long, ClaimedChunk> map = chunks.get(c.dim);
		if(map == null) chunks.put(c.dim, map = new HashMap<>());
		return map.put(c.getLongPos(), c) == null;
	}
	
	public ClaimedChunk remove(int dim, int cx, int cz)
	{
		HashMap<Long, ClaimedChunk> map = chunks.get(dim);
		if(map != null)
		{
			ClaimedChunk chunk = map.remove(Bits.intsToLong(cx, cz));
			
			if(chunk != null)
			{
				if(map.isEmpty()) chunks.remove(dim);
				return chunk;
			}
		}
		
		return null;
	}
	
	public ChunkType getType(int dim, int cx, int cz)
	{
		World w = LMDimUtils.getWorld(dim);
		if(w == null || !w.getChunkProvider().chunkExists(cx, cz)) return ChunkType.UNLOADED;
		if(isInSpawn(dim, cx, cz)) return ChunkType.SPAWN;
		//TODO: if(LMWorldMP.inst.settings.getWB(dim).isOutside(cx, cz)) return ChunkType.WORLD_BORDER;
		ClaimedChunk c = getChunk(dim, cx, cz);
		if(c == null) return ChunkType.WILDERNESS;
		return new ChunkType.PlayerClaimed(LMWorldMP.inst.getPlayer(c.ownerID));
	}
	
	public ChunkType getTypeD(int dim, BlockPos pos)
	{ return getType(dim, MathHelperLM.chunk(pos.getX()), MathHelperLM.chunk(pos.getZ())); }
	
	public static boolean isInSpawn(int dim, int cx, int cz)
	{
		if(dim != 0 || (!FTBLib.getServer().isDedicatedServer() && !FTBUConfigGeneral.spawn_area_in_sp.get()))
			return false;
		int radius = FTBLib.getServer().getSpawnProtectionSize();
		if(radius <= 0) return false;
		BlockDimPos c = LMDimUtils.getSpawnPoint(0);
		int minX = MathHelperLM.chunk(c.x + 0.5D - radius);
		int minZ = MathHelperLM.chunk(c.z + 0.5D - radius);
		int maxX = MathHelperLM.chunk(c.x + 0.5D + radius);
		int maxZ = MathHelperLM.chunk(c.z + 0.5D + radius);
		return cx >= minX && cx <= maxX && cz >= minZ && cz <= maxZ;
	}
	
	public static boolean isInSpawnD(int dim, double x, double z)
	{ return dim == 0 && isInSpawn(dim, MathHelperLM.chunk(x), MathHelperLM.chunk(z)); }
	
	public boolean allowExplosion(int dim, int cx, int cz)
	{
		if(dim == 0 && FTBUConfigGeneral.safe_spawn.get() && isInSpawn(dim, cx, cz)) return false;
			//TODO: else if(LMWorldMP.inst.settings.getWB(dim).isOutside(cx, cz)) return false;
		else
		{
			ClaimedChunk c = getChunk(dim, cx, cz);
			if(c != null)
			{
				LMPlayer p = c.getOwner(Side.SERVER);
				
				if(p != null)
				{
					EnumEnabled fe = FTBUPermissions.forced_explosions.getEnum(p.toPlayerMP().getPlayer());
					if(fe == null) return FTBUPlayerData.get(p).getFlag(FTBUPlayerData.EXPLOSIONS);
					else return fe.isEnabled();
				}
			}
		}
		
		return true;
	}
	
	public static boolean canPlayerInteract(EntityPlayerMP ep, BlockPos pos, boolean leftClick)
	{
		if(ep == null) return true;
		
		LMPlayerMP p = LMWorldMP.inst.getPlayer(ep);
		
		if(p == null) return true;
		else if(!p.isFake() && p.allowCreativeInteractSecure()) return true;
		//TODO: World border
		
		if(leftClick)
		{
			if(FTBUPermissions.break_whitelist.getStringList(ep).contains(LMInvUtils.getRegName(ep.worldObj.getBlockState(pos).getBlock())))
				return true;
		}
		
		ChunkType type = serverInstance.getTypeD(ep.dimension, pos);
		return type.canInteract(p.toPlayerMP(), leftClick);
	}
}