package latmod.ftbu.world.claims;

import latmod.ftbu.world.*;
import latmod.lib.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.fml.relauncher.*;

public final class ClaimedChunk extends ChunkCoordIntPair
{
	public final int ownerID;
	public final int dim;
	public boolean isChunkloaded = false;
	public boolean isForced = false;
	
	public ClaimedChunk(int o, int d, int x, int z)
	{
		super(x, z);
		ownerID = o;
		dim = d;
	}
	
	public ClaimedChunk(EntityPlayer ep)
	{ this(LMWorldServer.inst.getPlayer(ep).playerID, ep.dimension, MathHelperLM.chunk(ep.posX), MathHelperLM.chunk(ep.posZ)); }
	
	public Long getLongPos()
	{ return Long.valueOf(Bits.intsToLong(chunkXPos, chunkZPos)); }
	
	public LMPlayerServer getOwnerS()
	{ return LMWorldServer.inst.getPlayer(ownerID); }
	
	@SideOnly(Side.CLIENT)
	public LMPlayerClient getOwnerC()
	{ return LMWorldClient.inst.getPlayer(ownerID); }
	
	public boolean equals(Object o)
	{ return o != null && (o == this || (o.getClass() == ClaimedChunk.class && equalsChunk((ClaimedChunk) o))); }
	
	public boolean equalsChunk(int d, int x, int z)
	{ return dim == d && chunkXPos == x && chunkZPos == z; }
	
	public boolean equalsChunk(ClaimedChunk c)
	{ return equalsChunk(c.dim, c.chunkXPos, c.chunkZPos); }
	
	public String toString()
	{ return "[" + dim + ',' + chunkXPos + ',' + chunkZPos + ']'; }
	
	public int hashCode()
	{ return LMUtils.hashCode(dim, chunkXPos, chunkZPos); }
	
	public double getDistSq(double x, double z)
	{
		double x0 = MathHelperLM.unchunk(chunkXPos) + 8.5D;
		double z0 = MathHelperLM.unchunk(chunkZPos) + 8.5D;
		return MathHelperLM.distSq(x0, 0D, z0, x, 0D, z);
	}
	
	public double getDistSq(ClaimedChunk c)
	{ return getDistSq(MathHelperLM.unchunk(c.chunkXPos) + 8.5D, MathHelperLM.unchunk(c.chunkZPos) + 8.5D); }
}