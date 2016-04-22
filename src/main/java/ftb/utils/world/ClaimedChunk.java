package ftb.utils.world;

import ftb.lib.ChunkDimPos;
import ftb.lib.api.*;

import java.util.UUID;

public final class ClaimedChunk
{
	public final ChunkDimPos pos;
	public final UUID ownerID;
	public boolean isChunkloaded = false;
	public boolean isForced = false;
	
	public ClaimedChunk(UUID o, ChunkDimPos p)
	{
		pos = p;
		ownerID = o;
	}
	
	public ForgePlayerMP getOwner()
	{ return ForgeWorldMP.inst.getPlayer(ownerID); }
	
	@Override
	public boolean equals(Object o)
	{ return o != null && (o == this || (o instanceof ClaimedChunk && pos.equalsChunk(((ClaimedChunk) o).pos))); }
	
	@Override
	public String toString()
	{ return pos.toString(); }
	
	@Override
	public int hashCode()
	{ return pos.hashCode(); }
}