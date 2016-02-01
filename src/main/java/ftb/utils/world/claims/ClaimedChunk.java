package ftb.utils.world.claims;

import cpw.mods.fml.relauncher.*;
import ftb.utils.world.*;
import latmod.lib.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.ChunkCoordIntPair;

public final class ClaimedChunk
{
	public final int posX, posZ;
	public final int ownerID;
	public final int dim;
	public boolean isChunkloaded = false;
	public boolean isForced = false;
	
	public ClaimedChunk(int o, int d, int x, int z)
	{
		posX = x;
		posZ = z;
		ownerID = o;
		dim = d;
	}
	
	public ClaimedChunk(EntityPlayer ep)
	{ this(LMWorldServer.inst.getPlayer(ep).playerID, ep.dimension, MathHelperLM.chunk(ep.posX), MathHelperLM.chunk(ep.posZ)); }
	
	public Long getLongPos()
	{ return Long.valueOf(Bits.intsToLong(posX, posZ)); }
	
	public LMPlayerServer getOwnerS()
	{ return LMWorldServer.inst.getPlayer(ownerID); }
	
	@SideOnly(Side.CLIENT)
	public LMPlayerClient getOwnerC()
	{ return LMWorldClient.inst.getPlayer(ownerID); }
	
	public boolean equals(Object o)
	{ return o != null && (o == this || (o instanceof ClaimedChunk && equalsChunk((ClaimedChunk) o))); }
	
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
		double x0 = MathHelperLM.unchunk(posX) + 8.5D;
		double z0 = MathHelperLM.unchunk(posZ) + 8.5D;
		return MathHelperLM.distSq(x0, 0D, z0, x, 0D, z);
	}
	
	public double getDistSq(ClaimedChunk c)
	{ return getDistSq(MathHelperLM.unchunk(c.posX) + 8.5D, MathHelperLM.unchunk(c.posZ) + 8.5D); }
	
	public ChunkCoordIntPair getPos()
	{ return new ChunkCoordIntPair(posX, posZ); }
}