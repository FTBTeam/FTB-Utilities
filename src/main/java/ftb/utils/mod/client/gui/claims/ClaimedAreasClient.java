package ftb.utils.mod.client.gui.claims;

import ftb.utils.world.*;
import ftb.utils.world.claims.*;
import latmod.lib.Bits;
import net.minecraftforge.fml.relauncher.*;

import java.util.*;

@SideOnly(Side.CLIENT)
public class ClaimedAreasClient
{
	private static final HashMap<Long, Integer> chunks = new HashMap<>();
	private static int lastDimension = 0;
	
	public static void clear()
	{ chunks.clear(); }
	
	public static int getType(int x, int z)
	{
		Integer i = chunks.get(Bits.intsToLong(x, z));
		return (i == null) ? 0 : i.intValue();
	}
	
	public static ChunkType getTypeE(int x, int z)
	{ return ClaimedChunks.getChunkTypeFromI(getType(x, z)); }
	
	public static void setTypes(int dim, int chunkX, int chunkZ, int sx, int sz, int[] types)
	{
		if(lastDimension != dim)
		{
			lastDimension = dim;
			clear();
		}
		
		for(int z = 0; z < sz; z++)
			for(int x = 0; x < sx; x++)
				chunks.put(Bits.intsToLong(x + chunkX, z + chunkZ), Integer.valueOf(types[x + z * sx]));
	}
	
	public static void getMessage(int x, int z, List<String> l, boolean shift)
	{
		int type = getType(x, z);
		ChunkType typeE = ClaimedChunks.getChunkTypeFromI(type);
		
		if(typeE != null)
		{
			if(typeE.isClaimed())
			{
				LMPlayerClient owner = LMWorldClient.inst.getPlayer(type);
				if(owner != null) l.add(typeE.getChatColor(owner) + owner.getProfile().getName());
			}
			else l.add(typeE.getChatColor(null) + typeE.getIDS());
		}
	}
}