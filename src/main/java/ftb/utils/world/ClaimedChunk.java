package ftb.utils.world;

import ftb.lib.api.*;
import latmod.lib.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.ChunkCoordIntPair;

import java.util.UUID;

public final class ClaimedChunk
{
	public final ChunkCoordIntPair pos;
	public final UUID ownerID;
	public final int dim;
	public boolean isChunkloaded = false;
	public boolean isForced = false;
	
	public ClaimedChunk(UUID o, int d, ChunkCoordIntPair p)
	{
		pos = p;
		ownerID = o;
		dim = d;
	}
	
	public ClaimedChunk(EntityPlayerMP ep)
	{ this(ForgeWorldMP.inst.getPlayer(ep).getProfile().getId(), ep.dimension, new ChunkCoordIntPair(MathHelperLM.chunk(ep.posX), MathHelperLM.chunk(ep.posZ))); }
	
	public ForgePlayerMP getOwner()
	{ return ForgeWorldMP.inst.getPlayer(ownerID); }
	
	public boolean equals(Object o)
	{ return o != null && (o == this || (o instanceof ClaimedChunk && equalsChunk((ClaimedChunk) o))); }
	
	public boolean equalsChunk(int d, ChunkCoordIntPair p)
	{ return dim == d && pos.chunkXPos == p.chunkXPos && pos.chunkZPos == p.chunkZPos; }
	
	public boolean equalsChunk(ClaimedChunk c)
	{ return equalsChunk(c.dim, c.pos); }
	
	public String toString()
	{ return "[" + dim + ',' + pos.chunkXPos + ',' + pos.chunkZPos + ']'; }
	
	public int hashCode()
	{ return LMUtils.hashCode(dim, pos.chunkXPos, pos.chunkZPos); }
	
	public double getDistSq(double x, double z)
	{
		double x0 = MathHelperLM.unchunk(pos.chunkXPos) + 8.5D;
		double z0 = MathHelperLM.unchunk(pos.chunkZPos) + 8.5D;
		return MathHelperLM.distSq(x0, 0D, z0, x, 0D, z);
	}
	
	public double getDistSq(ClaimedChunk c)
	{ return getDistSq(MathHelperLM.unchunk(c.pos.chunkXPos) + 8.5D, MathHelperLM.unchunk(c.pos.chunkZPos) + 8.5D); }
}