package latmod.ftbu.mod.claims;

import latmod.ftbu.core.*;
import latmod.ftbu.core.client.ClientConfig;
import latmod.ftbu.core.util.*;
import latmod.ftbu.mod.FTBUConfig;
import net.minecraft.nbt.*;
import net.minecraft.util.ChunkCoordinates;
import cpw.mods.fml.relauncher.*;

public class Claims
{
	public final LMPlayer owner;
	public final FastList<ClaimedChunk> chunks;
	public String desc;
	public boolean safe;
	
	public Claims(LMPlayer p)
	{
		owner = p;
		chunks = new FastList<ClaimedChunk>();
		desc = "";
		safe = false;
	}
	
	public void readFromNBT(NBTTagCompound serverData)
	{
		chunks.clear();
		
		NBTTagCompound tag = serverData.getCompoundTag("Claims");
		NBTTagList list = (NBTTagList)tag.getTag("Chunks");
		
		if(list != null) for(int i = 0; i < list.tagCount(); i++)
		{
			int[] ai = list.func_150306_c(i);
			chunks.add(new ClaimedChunk(this, ai[0], ai[1], ai[2]));
		}
		
		LatCoreMC.logger.info("Loaded claims: " + chunks);
		
		desc = tag.getString("Desc");
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
		if(list.tagCount() > 0)
			tag.setTag("Chunks", list);
		
		LatCoreMC.logger.info("Saved claims: " + list);
		
		if(!desc.isEmpty()) tag.setString("Desc", desc);
		if(safe) tag.setBoolean("Safe", safe);
		
		if(!tag.hasNoTags())
			serverData.setTag("Claims", tag);
	}
	
	public boolean isSafe()
	{
		return safe;
	}
	
	public ClaimedChunk getLocal(int dim, int cx, int cz)
	{
		for(int i = 0; i < chunks.size(); i++)
		{
			ClaimedChunk c = chunks.get(i);
			if(c.equalsChunk(dim, cx, cz)) return c;
		}
		
		return null;
	}
	
	public boolean claim(int dim, int cx, int cz, boolean admin)
	{
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
	
	// Static //
	
	@SideOnly(Side.CLIENT)
	public static class Client
	{
		public static final ClientConfig config = new ClientConfig("claims");
		public static final ClientConfig.Property displayChunk = new ClientConfig.Property("display_chunk", true);
		
		public static void init()
		{
			config.add(displayChunk);
			ClientConfig.Registry.add(config);
		}
	}
	
	public static void init()
	{
	}
	
	public static ClaimedChunk get(int dim, int cx, int cz)
	{
		for(int i = 0; i < LMPlayer.map.size(); i++)
		{
			ClaimedChunk c = LMPlayer.map.values.get(i).claims.getLocal(dim, cx, cz);
			if(c != null) return c;
		}
		
		return null;
	}
	
	public static boolean isInSpawn(int dim, int cx, int cz)
	{
		if(dim != 0) return false;
		int radius = LatCoreMC.getServer().getSpawnProtectionSize();
		if(radius <= 0) return false;
		ChunkCoordinates c = LatCoreMC.getSpawnPoint(0);
		int minX = MathHelperLM.chunk(c.posX + 0.5D - radius);
		int minZ = MathHelperLM.chunk(c.posZ + 0.5D - radius);
		int maxX = MathHelperLM.chunk(c.posX + 0.5D + radius);
		int maxZ = MathHelperLM.chunk(c.posZ + 0.5D + radius);
		return cx >= minX && cx <= maxX && cz >= minZ && cz <= maxZ;
	}
	
	public static boolean isInSpawn(int dim, double x, double z)
	{ return dim == 0 && isInSpawn(dim, MathHelperLM.chunk(x), MathHelperLM.chunk(z)); }
	
	public static boolean isOutsideWorldBorder(int dim, int cx, int cz)
	{
		if(!FTBUConfig.WorldBorder.inst.enabled) return false;
		int radius = FTBUConfig.WorldBorder.inst.getWorldBorder(dim);
		int min = MathHelperLM.chunk(-radius);
		int max = MathHelperLM.chunk(radius);
		return cx >= max || cx <= min || cz >= max || cz <= min;
	}
	
	public static boolean isOutsideWorldBorderD(int dim, double x, double z)
	{ return isOutsideWorldBorder(dim, MathHelperLM.chunk(x), MathHelperLM.chunk(z)); }
}