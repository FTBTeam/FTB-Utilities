package latmod.ftbu.world.claims;

import java.util.UUID;

import ftb.lib.*;
import ftb.lib.item.LMInvUtils;
import latmod.ftbu.api.tile.ISecureTile;
import latmod.ftbu.mod.config.*;
import latmod.ftbu.world.*;
import latmod.lib.*;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public class ClaimedChunks
{
	private final FastMap<Integer, FastMap<Long, ClaimedChunk>> chunks;
	
	public ClaimedChunks()
	{ chunks = new FastMap<Integer, FastMap<Long, ClaimedChunk>>(); }
	
	public void load(NBTTagCompound tag)
	{
		chunks.clear();
		
		FastMap<String, NBTTagCompound> tag1 = LMNBTUtils.toFastMapWithType(tag.getCompoundTag("ClaimedChunks"));
		
		for(int i = 0; i < tag1.size(); i++)
		{
			try
			{
				int dim = Integer.parseInt(tag1.keys.get(i));
				
				FastMap<Long, ClaimedChunk> map = new FastMap<Long, ClaimedChunk>();
				
				FastMap<String, NBTTagList> tag2 = LMNBTUtils.toFastMapWithType(tag1.values.get(i));
				
				for(int j = 0; j < tag2.size(); j++)
				{
					NBTTagList chunksList = tag2.values.get(j);
					
					for(int k = 0; k < chunksList.tagCount(); k++)
					{
						int[] ai = chunksList.func_150306_c(k);
						ClaimedChunk c = new ClaimedChunk(Integer.parseInt(tag2.keys.get(j)), dim, ai[0], ai[1]);
						if(ai.length >= 3 && ai[2] == 1) c.isChunkloaded = true;
						map.put(Bits.intsToLong(ai[0], ai[1]), c);
					}
				}
				
				chunks.put(Integer.valueOf(dim), map);
			}
			catch(Exception e)
			{ e.printStackTrace(); }
		}
	}
	
	public void save(NBTTagCompound tag)
	{
		NBTTagCompound tag1 = new NBTTagCompound();
		
		chunks.sortFromKeyNums();
		
		for(int i = 0; i < chunks.size(); i++)
		{
			NBTTagCompound tag2 = new NBTTagCompound();
			
			for(ClaimedChunk c : chunks.values.get(i))
			{
				NBTTagList tag3 = tag2.getTagList(Integer.toString(c.ownerID), LMNBTUtils.INT_ARRAY);
				
				IntList intList = new IntList();
				intList.add(c.pos.chunkXPos);
				intList.add(c.pos.chunkZPos);
				if(c.isChunkloaded) intList.add(1);
				tag3.appendTag(new NBTTagIntArray(intList.toArray()));
				
				tag2.setTag(Integer.toString(c.ownerID), tag3);
			}
			
			tag1.setTag(chunks.keys.get(i).toString(), tag2);
		}
		
		tag.setTag("ClaimedChunks", tag1);
	}
	
	public ClaimedChunk getChunk(int dim, int cx, int cz)
	{
		if(!chunks.keys.contains(dim)) return null;
		return chunks.get(Integer.valueOf(dim)).get(Long.valueOf(Bits.intsToLong(cx, cz)));
	}
	
	public FastList<ClaimedChunk> getChunks(LMPlayer p, Integer dim)
	{
		FastList<ClaimedChunk> list = new FastList<ClaimedChunk>();
		
		if(dim == null)
		{
			for(int i = 0; i < chunks.size(); i++)
			{
				FastMap<Long, ClaimedChunk> map = chunks.values.get(i);
				
				for(int j = 0; j < map.size(); j++)
				{
					ClaimedChunk c = map.values.get(j);
					if(c.ownerID == p.playerID) list.add(c);
				}
			}
		}
		else
		{
			FastMap<Long, ClaimedChunk> map = chunks.get(Integer.valueOf(dim));
			
			for(int j = 0; j < map.size(); j++)
			{
				ClaimedChunk c = map.values.get(j);
				if(c.ownerID == p.playerID) list.add(c);
			}
		}
		
		return list;
	}
	
	public boolean put(ClaimedChunk c)
	{
		if(c == null) return false;
		FastMap<Long, ClaimedChunk> map = chunks.get(Integer.valueOf(c.dim));
		if(map == null) chunks.put(Integer.valueOf(c.dim), map = new FastMap<Long, ClaimedChunk>());
		return map.put(Long.valueOf(Bits.intsToLong(c.pos.chunkXPos, c.pos.chunkZPos)), c);
	}
	
	public boolean remove(int dim, int cx, int cz)
	{
		FastMap<Long, ClaimedChunk> map = chunks.get(Integer.valueOf(dim));
		if(map != null && map.remove(Long.valueOf(Bits.intsToLong(cx, cz))))
		{
			if(map.isEmpty()) chunks.remove(Integer.valueOf(dim));
			return true;
		}
		
		return false;
	}
	
	public ChunkType getType(int dim, int cx, int cz)
	{
		World w = LMDimUtils.getWorld(dim);
		if(w == null || !w.getChunkProvider().chunkExists(cx, cz)) return ChunkType.UNLOADED;
		if(isInSpawn(dim, cx, cz)) return ChunkType.SPAWN;
		if(LMWorldServer.inst.settings.isOutside(dim, cx, cz)) return ChunkType.WORLD_BORDER;
		ClaimedChunk c = getChunk(dim, cx, cz);
		if(c == null) return ChunkType.WILDERNESS;
		return new ChunkType.PlayerClaimed(LMWorldServer.inst.getPlayer(c.ownerID));
	}
	
	public ChunkType getTypeD(int dim, double x, double z)
	{ return getType(dim, MathHelperLM.chunk(x), MathHelperLM.chunk(z)); }
	
	public static ChunkType getChunkTypeFromI(int i)
	{
		if(i <= 0) return ChunkType.UNCLAIMED_VALUES[-i];
		LMPlayer p = LMWorld.getWorld().getPlayer(i);
		return (p == null) ? ChunkType.WILDERNESS : new ChunkType.PlayerClaimed(p);
	}
	
	public static boolean isInSpawn(int dim, int cx, int cz)
	{
		if(dim != 0) return false;
		//if(!LatCoreMC.isDedicatedServer()) return false;
		int radius = FTBLib.getServer().getSpawnProtectionSize();
		if(radius <= 0) return false;
		ChunkCoordinates c = LMDimUtils.getSpawnPoint(0);
		int minX = MathHelperLM.chunk(c.posX + 0.5D - radius);
		int minZ = MathHelperLM.chunk(c.posZ + 0.5D - radius);
		int maxX = MathHelperLM.chunk(c.posX + 0.5D + radius);
		int maxZ = MathHelperLM.chunk(c.posZ + 0.5D + radius);
		return cx >= minX && cx <= maxX && cz >= minZ && cz <= maxZ;
	}
	
	public static boolean isInSpawnF(int dim, double x, double z)
	{ return dim == 0 && isInSpawn(dim, MathHelperLM.chunk(x), MathHelperLM.chunk(z)); }
	
	public boolean allowExplosion(int dim, int cx, int cz)
	{
		if(dim == 0 && FTBUConfigGeneral.safeSpawn.get() && isInSpawn(dim, cx, cz))
			return false;
		else if(LMWorldServer.inst.settings.isOutside(dim, cx, cz))
			return false;
		else
		{
			EnumEnabled fe = FTBUConfigClaims.forced_explosions.get();
			
			ClaimedChunk c = getChunk(dim, cx, cz);
			if(c != null)
			{
				if(fe == null) return c.getOwner().settings.explosions;
				else return fe.isEnabled();
			}
		}
		
		return true;
	}
	
	public static boolean canPlayerInteract(EntityPlayer ep, int x, int y, int z, boolean leftClick)
	{
		if(ep instanceof FakePlayer) return true;
		
		World w = ep.worldObj;
		boolean server = !w.isRemote;
		if(server && LMWorldServer.inst.settings.isOutsideF(w.provider.dimensionId, x, z)) return false;
		
		if(!server || FTBUConfigGeneral.allowCreativeInteractSecure(ep)) return true;
		
		Block block = w.getBlock(x, y, z);
		
		if(block.hasTileEntity(w.getBlockMetadata(x, y, z)))
		{
			TileEntity te = w.getTileEntity(x, y, z);
			if(te instanceof ISecureTile && !te.isInvalid() && !((ISecureTile)te).canPlayerInteract(ep, leftClick))
			{ ((ISecureTile)te).onPlayerNotOwner(ep, leftClick); return false; }
		}
		
		return canInteract(ep.getGameProfile().getId(), w, x, y, z, leftClick);
	}
	
	public static boolean canInteract(UUID playerID, World w, int x, int y, int z, boolean leftClick)
	{
		if(leftClick && FTBUConfigClaims.break_whitelist.get().contains(LMInvUtils.getRegName(w.getBlock(x, y, z)))) return true;
		ChunkType type = LMWorldServer.inst.claimedChunks.getTypeD(w.provider.dimensionId, x, z);
		return type.canInteract(LMWorldServer.inst.getPlayer(playerID), leftClick);
	}
}