package latmod.ftbu.world;

import latmod.ftbu.util.LMNBTUtils;
import latmod.lib.FastList;
import net.minecraft.nbt.*;

public class Claims
{
	public final LMPlayerServer owner;
	private final FastList<ClaimedChunk> chunks;
	
	public Claims(LMPlayerServer p)
	{
		owner = p;
		chunks = new FastList<ClaimedChunk>();
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
		serverData.setTag("Claims", tag);
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
	
	public void claim(int dim, int cx, int cz, int sx, int sz)
	{
		int max = owner.getMaxClaimPower();
		
		for(int z = cz; z < cz + sz; z++)
		for(int x = cx; x < cx + sx; x++)
		{
			if(max >= 0 && chunks.size() >= max) return;
			
			ChunkType t = ChunkType.get(dim, x, z, owner);
			if(t == ChunkType.WILDERNESS)
				chunks.add(new ClaimedChunk(this, dim, x, z));
		}
		
		owner.sendUpdate();
	}
	
	public void unclaim(int dim, int cx, int cz, int sx, int sz, boolean admin)
	{
		for(int z = cz; z < cz + sz; z++)
		for(int x = cx; x < cx + sx; x++)
		{
			ChunkType t = ChunkType.get(dim, x, z, owner);
			if(t == ChunkType.CLAIMED_SELF)
				chunks.remove(new ClaimedChunk(this, dim, x, z));
			if(chunks.isEmpty()) return;
		}
		
		owner.sendUpdate();
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
}