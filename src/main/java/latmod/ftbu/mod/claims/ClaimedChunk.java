package latmod.ftbu.mod.claims;

import latmod.ftbu.core.LMPlayer;

public class ClaimedChunk
{
	public final int dim, posX, posZ;
	public final LMPlayer owner;
	
	public ClaimedChunk(int d, int x, int z, LMPlayer p)
	{
		dim = d;
		posX = x;
		posZ = z;
		owner = p;
	}
}