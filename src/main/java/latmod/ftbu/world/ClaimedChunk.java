package latmod.ftbu.world;

import latmod.lib.*;
import net.minecraft.entity.player.EntityPlayer;

public final class ClaimedChunk
{
	public final Claims claims;
	public final int dim, posX, posZ;
	
	public ClaimedChunk(Claims c, int d, int x, int z)
	{
		claims = c;
		dim = d;
		posX = x;
		posZ = z;
	}
	
	public ClaimedChunk(EntityPlayer ep)
	{ this(LMWorldServer.inst.getPlayer(ep).claims, ep.dimension, MathHelperLM.chunk(ep.posX), MathHelperLM.chunk(ep.posZ)); }
	
	public boolean equals(Object o)
	{ return o != null && (o == this || (o.getClass() == ClaimedChunk.class && equalsChunk((ClaimedChunk)o))); }
	
	public boolean equalsChunk(int d, int x, int z)
	{ return dim == d && posX == x && posZ == z; }
	
	public boolean equalsChunk(ClaimedChunk c)
	{ return equalsChunk(c.dim, c.posX, c.posZ); }
	
	public String toString()
	{ return "[" + dim + ',' + posX + ',' + posZ + ']'; }
	
	public int hashCode()
	{ return LMUtils.hashCode(dim, posX, posZ); }
	
	public double getDistSq(double x, double z)
	{
		double x0 = posX * 16D + 8.5D;
		double z0 = posZ * 16D + 8.5D;
		return MathHelperLM.distSq(x0, 0D, z0, x, 0D, z);
	}
	
	public double getDistSq(ClaimedChunk c)
	{ return getDistSq(c.posX * 16D + 8.5D, c.posZ * 16D + 8.5D); }
}