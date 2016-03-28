package ftb.utils.world;

import com.google.gson.*;
import ftb.lib.*;
import ftb.lib.api.*;
import ftb.lib.api.item.LMInvUtils;
import ftb.lib.api.permissions.ForgePermissionRegistry;
import ftb.lib.mod.FTBLibPermissions;
import ftb.utils.*;
import ftb.utils.badges.ServerBadges;
import ftb.utils.cmd.admin.CmdRestart;
import ftb.utils.config.*;
import ftb.utils.handlers.FTBUChunkEventHandler;
import latmod.lib.*;
import latmod.lib.util.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.*;

import java.io.File;
import java.util.*;

/**
 * Created by LatvianModder on 23.02.2016.
 */
public class FTBUWorldDataMP extends ForgeWorldData implements IWorldTick
{
	private static FTBUWorldDataMP inst;
	
	public static FTBUWorldDataMP get()
	{
		if(inst == null)
		{
			inst = new FTBUWorldDataMP();
		}
		
		return inst;
	}
	
	public Map<ChunkDimPos, ClaimedChunk> chunks;
	public Warps warps;
	public long nextChunkloaderUpdate;
	private long startMillis;
	private String lastRestartMessage;
	public long restartMillis;
	
	private FTBUWorldDataMP()
	{ super(FTBUFinals.MOD_ID); }
	
	public void onLoaded(ForgeWorld w)
	{
		chunks = new HashMap<>();
		warps = new Warps();
		
		startMillis = LMUtils.millis();
		Backups.nextBackup = startMillis + FTBUConfigBackups.backupMillis();
		lastRestartMessage = "";
		
		if(FTBUConfigGeneral.restart_timer.getAsInt() > 0)
		{
			restartMillis = startMillis + (long) (FTBUConfigGeneral.restart_timer.getAsInt() * 3600D * 1000D);
			FTBU.logger.info("Server restart in " + LMStringUtils.getTimeString(restartMillis));
		}
	}
	
	public Collection<ClaimedChunk> getAllChunks(Integer dim)
	{
		if(dim == null) return chunks.values();
		else
		{
			ArrayList<ClaimedChunk> l = new ArrayList<>();
			
			for(ClaimedChunk c : chunks.values())
			{
				if(c.pos.dim == dim) l.add(c);
			}
			
			return l;
		}
	}
	
	public void loadData(NBTTagCompound tag, Phase phase)
	{
		warps.writeToNBT(tag, "warps");
		
		chunks.clear();
		JsonElement claimsFile = LMJsonUtils.fromJson(new File(ForgeWorldMP.inst.latmodFolder, "ClaimedChunks.json"));
		
		if(claimsFile.isJsonObject())
		{
			JsonObject claimedChunksGroup = claimsFile.getAsJsonObject();
			
			for(Map.Entry<String, JsonElement> e : claimedChunksGroup.entrySet())
			{
				try
				{
					UUID id = LMUtils.fromString(e.getKey());
					
					if(id != null)
					{
						if(ForgeWorldMP.inst.playerMap.containsKey(id))
						{
							for(JsonElement e1 : e.getValue().getAsJsonArray())
							{
								String[] s = e1.getAsString().split(",");
								
								if(s.length >= 3)
								{
									ClaimedChunk c = new ClaimedChunk(id, new ChunkDimPos(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2])));
									
									if(s.length > 3)
									{
										for(int i = 3; i < s.length; i++)
										{
											switch(s[i].charAt(0))
											{
												case 'L':
												{
													c.isChunkloaded = true;
													break;
												}
												//Possible other chunk properties
											}
										}
									}
									
									chunks.put(c.pos, c);
								}
							}
						}
					}
					else
					{
						int dim = Integer.parseInt(e.getKey());
						
						for(Map.Entry<String, JsonElement> e1 : e.getValue().getAsJsonObject().entrySet())
						{
							id = LMUtils.fromString(e1.getKey());
							
							if(ForgeWorldMP.inst.playerMap.containsKey(id))
							{
								JsonArray chunksList = e1.getValue().getAsJsonArray();
								
								for(int k = 0; k < chunksList.size(); k++)
								{
									int[] ai = LMJsonUtils.fromIntArray(chunksList.get(k));
									
									if(ai != null)
									{
										ClaimedChunk c = new ClaimedChunk(id, new ChunkDimPos(dim, ai[0], ai[1]));
										if(ai.length >= 3 && ai[2] == 1) c.isChunkloaded = true;
										chunks.put(c.pos, c);
									}
								}
							}
							
						}
					}
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
		
		nextChunkloaderUpdate = LMUtils.millis() + 10000L;
	}
	
	public void saveData(NBTTagCompound tag)
	{
		warps.readFromNBT(tag, "warps");
		
		Map<UUID, JsonArray> map = new HashMap<>();
		
		for(Map.Entry<ChunkDimPos, ClaimedChunk> e : chunks.entrySet())
		{
			ForgePlayerMP p = e.getValue().getOwner();
			
			if(p != null)
			{
				ClaimedChunk c = e.getValue();
				
				JsonArray a = map.get(p.getProfile().getId());
				
				if(a == null)
				{
					a = new JsonArray();
					map.put(p.getProfile().getId(), a);
				}
				
				StringBuilder sb = new StringBuilder();
				
				sb.append(c.pos.dim);
				sb.append(',');
				
				sb.append(c.pos.chunkXPos);
				sb.append(',');
				
				sb.append(c.pos.chunkZPos);
				
				if(c.isChunkloaded)
				{
					sb.append(',');
					sb.append("L");
				}
				
				a.add(new JsonPrimitive(sb.toString()));
			}
		}
		
		JsonObject claimedChunksGroup = new JsonObject();
		
		for(Map.Entry<UUID, JsonArray> entry : map.entrySet())
		{
			claimedChunksGroup.add(LMUtils.fromUUID(entry.getKey()), entry.getValue());
		}
		
		LMJsonUtils.toJson(new File(ForgeWorldMP.inst.latmodFolder, "ClaimedChunks.json"), claimedChunksGroup);
	}
	
	public void writeToNet(NBTTagCompound tag, ForgePlayerMP self, boolean login)
	{
	}
	
	public void onClosed()
	{
		startMillis = restartMillis = 0L;
		inst = null;
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
						IChatComponent c = FTBU.mod.chatComponent("server_restart", msg);
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
	
	public ClaimedChunk getChunk(ChunkDimPos pos)
	{ return chunks.get(pos); }
	
	public List<ClaimedChunk> getChunks(UUID playerID, Integer dim)
	{
		ArrayList<ClaimedChunk> list = new ArrayList<>();
		if(playerID == null) return list;
		
		for(ClaimedChunk c : chunks.values())
		{
			if((dim == null || c.pos.dim == dim) && c.ownerID.equals(playerID)) list.add(c);
		}
		
		return list;
	}
	
	public boolean put(ClaimedChunk c)
	{
		if(c == null) return false;
		return chunks.put(c.pos, c) == null;
	}
	
	public ClaimedChunk remove(ChunkDimPos pos)
	{ return chunks.remove(pos); }
	
	public ChunkType getType(ForgePlayerMP p, ChunkDimPos pos)
	{
		World w = LMDimUtils.getWorld(pos.dim);
		if(w == null || !w.getChunkProvider().chunkExists(pos.chunkXPos, pos.chunkZPos)) return ChunkType.UNLOADED;
		if(isInSpawn(pos)) return ChunkType.SPAWN;
		//TODO: if(ForgeWorldMP.inst.settings.getWB(dim).isOutside(cx, cz)) return ChunkType.WORLD_BORDER;
		ClaimedChunk c = getChunk(pos);
		if(c == null) return ChunkType.WILDERNESS;
		return new ChunkType.PlayerClaimed(c);
	}
	
	public ChunkType getTypeD(ForgePlayerMP p, int dim, BlockPos pos)
	{ return getType(p, new ChunkDimPos(dim, MathHelperLM.chunk(pos.getX()), MathHelperLM.chunk(pos.getZ()))); }
	
	public static boolean isInSpawn(ChunkDimPos pos)
	{
		if(pos.dim != 0 || (!FTBLib.getServer().isDedicatedServer() && !FTBUConfigGeneral.spawn_area_in_sp.getAsBoolean()))
			return false;
		int radius = FTBLib.getServer().getSpawnProtectionSize();
		if(radius <= 0) return false;
		BlockDimPos c = LMDimUtils.getSpawnPoint(0);
		int minX = MathHelperLM.chunk(c.x + 0.5D - radius);
		int minZ = MathHelperLM.chunk(c.z + 0.5D - radius);
		int maxX = MathHelperLM.chunk(c.x + 0.5D + radius);
		int maxZ = MathHelperLM.chunk(c.z + 0.5D + radius);
		return pos.chunkXPos >= minX && pos.chunkXPos <= maxX && pos.chunkZPos >= minZ && pos.chunkZPos <= maxZ;
	}
	
	public static boolean isInSpawnD(int dim, double x, double z)
	{ return dim == 0 && isInSpawn(new ChunkDimPos(dim, MathHelperLM.chunk(x), MathHelperLM.chunk(z))); }
	
	public boolean allowExplosion(ChunkDimPos pos)
	{
		if(pos.dim == 0 && FTBUConfigGeneral.safe_spawn.getAsBoolean() && isInSpawn(pos)) return false;
			//TODO: else if(ForgeWorldMP.inst.settings.getWB(dim).isOutside(cx, cz)) return false;
		else
		{
			ClaimedChunk c = getChunk(pos);
			if(c != null)
			{
				ForgePlayerMP p = c.getOwner();
				
				if(p != null)
				{
					EnumEnabled fe = FTBUPermissions.claims_forced_explosions.getEnum(p.getProfile());
					if(fe == null) return FTBUPlayerDataMP.get(p).getFlag(FTBUPlayerData.EXPLOSIONS);
					else return fe.isEnabled();
				}
			}
		}
		
		return true;
	}
	
	public boolean canPlayerInteract(EntityPlayerMP ep, BlockPos pos, boolean leftClick)
	{
		if(ep == null) return true;
		
		ForgePlayerMP p = ForgeWorldMP.inst.getPlayer(ep);
		
		if(p == null) return true;
		else if(!p.isFake() && ForgePermissionRegistry.hasPermission(FTBLibPermissions.interact_secure, ep.getGameProfile()))
		{
			return true;
		}
		
		//TODO: World border
		
		if(leftClick)
		{
			JsonArray a = FTBUPermissions.claims_break_whitelist.get(p.getProfile()).getAsJsonArray();
			
			for(int i = 0; i < a.size(); i++)
			{
				if(a.get(i).getAsString().equals(LMInvUtils.getRegName(ep.worldObj.getBlockState(pos).getBlock())))
					return true;
			}
		}
		
		ChunkType type = getTypeD(p, ep.dimension, pos);
		return type.canInteract(p.toPlayerMP(), leftClick);
	}
	
	public Map<ChunkDimPos, ChunkType> getChunkTypes(ForgePlayerMP p, int x, int z, int d, int sx, int sz)
	{
		Map<ChunkDimPos, ChunkType> map = new HashMap<>();
		
		for(int x1 = x; x1 < x + sx; x1++)
		{
			for(int z1 = z; z1 < z + sz; z1++)
			{
				ChunkDimPos pos = new ChunkDimPos(d, x1, z1);
				ChunkType type = getType(p, pos);
				if(type != ChunkType.UNLOADED)
				{
					map.put(pos, type);
				}
			}
		}
		
		return map;
	}
}
