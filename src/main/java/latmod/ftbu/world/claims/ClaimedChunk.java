package latmod.ftbu.world.claims;

import latmod.ftbu.world.LMWorldServer;
import latmod.lib.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.ChunkCoordIntPair;

public final class ClaimedChunk
{
	public final Claims claims;
	public final int dim;
	public final ChunkCoordIntPair pos;
	public boolean isChunkloaded = false;
	public boolean isForced = false;
	
	public ClaimedChunk(Claims c, int d, int x, int z)
	{
		claims = c;
		dim = d;
		pos = new ChunkCoordIntPair(x, z);
	}
	
	public ClaimedChunk(EntityPlayer ep)
	{ this(LMWorldServer.inst.getPlayer(ep).claims, ep.dimension, MathHelperLM.chunk(ep.posX), MathHelperLM.chunk(ep.posZ)); }
	
	public boolean equals(Object o)
	{ return o != null && (o == this || (o.getClass() == ClaimedChunk.class && equalsChunk((ClaimedChunk)o))); }
	
	public boolean equalsChunk(int d, int x, int z)
	{ return dim == d && pos.chunkXPos == x && pos.chunkZPos == z; }
	
	public boolean equalsChunk(ClaimedChunk c)
	{ return equalsChunk(c.dim, c.pos.chunkXPos, c.pos.chunkZPos); }
	
	public String toString()
	{ return "[" + dim + ',' + pos.chunkXPos + ',' + pos.chunkZPos + ']'; }
	
	public int hashCode()
	{ return LMUtils.hashCode(dim, pos.chunkXPos, pos.chunkZPos); }
	
	public double getDistSq(double x, double z)
	{
		double x0 = pos.chunkXPos * 16D + 8.5D;
		double z0 = pos.chunkZPos * 16D + 8.5D;
		return MathHelperLM.distSq(x0, 0D, z0, x, 0D, z);
	}
	
	public double getDistSq(ClaimedChunk c)
	{ return getDistSq(c.pos.chunkXPos * 16D + 8.5D, c.pos.chunkZPos * 16D + 8.5D); }
}