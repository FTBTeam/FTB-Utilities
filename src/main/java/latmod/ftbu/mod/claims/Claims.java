package latmod.ftbu.mod.claims;

import latmod.ftbu.core.*;
import latmod.ftbu.core.util.*;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.config.FTBUConfig;
import net.minecraft.nbt.*;
import net.minecraft.util.ChunkCoordinates;

public class Claims
{
	public final LMPlayerServer owner;
	private final FastList<ClaimedChunk> chunks;
	private boolean safe;
	
	public Claims(LMPlayerServer p)
	{
		owner = p;
		chunks = new FastList<ClaimedChunk>();
		safe = false;
	}
	
	public void readFromNBT(NBTTagCompound serverData)
	{
		chunks.clear();
		
		NBTTagCompound tag = serverData.getCompoundTag("Claims");
		NBTTagList list = tag.getTagList("Chunks", LMNBTUtils.INT_ARRAY);
		
		if(list != null) for(int i = 0; i < list.tagCount(); i++)
		{
			int[] ai = list.func_150306_c(i);
			chunks.add(new ClaimedChunk(this, ai[0], ai[1], ai[2]));
		}
		
		safe = tag.getBoolean("Safe");
	}
	
	public void writeToNBT(NBTTagCompound serverData)
	{
		NBTTagCompound tag = new NBTTagCompound();
		
		NBTTagList list = new NBTTagList();
		for(int i = 0; i < chunks.size(); i++)
		{
			ClaimedChunk c = chunks.get(i);
			list.appendTag(new NBTTagIntArray(new int[] { c.dim, c.posX, c.posZ }));
		}
		
		tag.setTag("Chunks", list);
		tag.setBoolean("Safe", safe);
		
		serverData.setTag("Claims", tag);
	}
	
	public boolean isSafe()
	{ return safe; }
	
	public ClaimedChunk getLocal(int dim, int cx, int cz)
	{
		for(int i = 0; i < chunks.size(); i++)
		{
			ClaimedChunk c = chunks.get(i);
			if(c.equalsChunk(dim, cx, cz)) return c;
		}
		
		return null;
	}
	
	public boolean claim(int dim, int cx, int cz)
	{
		if(chunks.size() >= owner.getMaxClaimPower())
			return false;
		
		ChunkType t = ChunkType.get(dim, cx, cz, owner);
		
		if(t == ChunkType.WILDERNESS)
		{
			chunks.add(new ClaimedChunk(this, dim, cx, cz));
			owner.sendUpdate(null, true);
			return true;
		}
		
		return false;
	}
	
	public boolean unclaim(int dim, int cx, int cz, boolean admin)
	{
		ChunkType t = ChunkType.get(dim, cx, cz, owner);
		
		if(t == ChunkType.CLAIMED_SELF)
		{
			chunks.remove(new ClaimedChunk(this, dim, cx, cz));
			owner.sendUpdate(null, true);
			return true;
		}
		
		return false;
	}
	
	public int getClaimedChunks()
	{ return chunks.size(); }
	
	// Static //
	
	public static ClaimedChunk get(int dim, int cx, int cz)
	{
		for(int i = 0; i < LMWorldServer.inst.players.size(); i++)
		{
			ClaimedChunk c = LMWorldServer.inst.players.get(i).claims.getLocal(dim, cx, cz);
			if(c != null) return c;
		}
		
		return null;
	}
	
	public static boolean isInSpawn(int dim, int cx, int cz)
	{
		if(dim != 0) return false;
		int radius = LatCoreMC.getServer().getSpawnProtectionSize();
		if(radius <= 0) return false;
		ChunkCoordinates c = LMDimUtils.getSpawnPoint(0);
		int minX = MathHelperLM.chunk(c.posX + 0.5D - radius);
		int minZ = MathHelperLM.chunk(c.posZ + 0.5D - radius);
		int maxX = MathHelperLM.chunk(c.posX + 0.5D + radius);
		int maxZ = MathHelperLM.chunk(c.posZ + 0.5D + radius);
		return cx >= minX && cx <= maxX && cz >= minZ && cz <= maxZ;
	}
	
	public static boolean isInSpawnD(int dim, double x, double z)
	{ return dim == 0 && isInSpawn(dim, MathHelperLM.chunk(x), MathHelperLM.chunk(z)); }
	
	public static boolean isOutsideWorldBorder(int dim, int cx, int cz)
	{
		if(!FTBUConfig.world_border.enabled) return false;
		int radius = FTBUConfig.world_border.getWorldBorder(dim);
		int min = MathHelperLM.chunk(-radius);
		int max = MathHelperLM.chunk(radius);
		return cx >= max || cx <= min || cz >= max || cz <= min;
	}
	
	public static boolean isOutsideWorldBorderD(int dim, double x, double z)
	{ return isOutsideWorldBorder(dim, MathHelperLM.chunk(x), MathHelperLM.chunk(z)); }
}