package latmod.ftbu.mod.client.gui.claims;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.util.client.LatCoreMCClient;
import latmod.ftbu.world.*;
import latmod.lib.*;

@SideOnly(Side.CLIENT)
public class ClaimedAreasClient
{
	private static final FastMap<Integer, FastMap<Long, Integer>> areasMap = new FastMap<Integer, FastMap<Long, Integer>>();
	private static int lastDimension = 0;
	
	public static void clear()
	{
		for(int i = 0; i < areasMap.size(); i++)
			areasMap.values.get(i).clear();
		areasMap.clear();
	}
	
	public static int getType(int dim, int x, int z)
	{
		FastMap<Long, Integer> c = areasMap.get(Integer.valueOf(dim));
		if(c == null) return 0;
		Integer i = c.get(Bits.intsToLong(x, z));
		return (i == null) ? 0 : i.intValue();
	}
	
	public static ChunkType getTypeE(int dim, int x, int z)
	{ return ChunkType.getChunkTypeFromI(getType(dim, x, z)); }
	
	public static void setTypes(int dim, int chunkX, int chunkZ, int sx, int sz, int[] types)
	{
		if(lastDimension != LatCoreMCClient.getDim())
		{ lastDimension = LatCoreMCClient.getDim(); clear(); }
		
		FastMap<Long, Integer> c = areasMap.get(Integer.valueOf(dim));
		if(c == null) c = new FastMap<Long, Integer>();
		for(int z = 0; z < sz; z++) for(int x = 0; x < sx; x++)
			c.put(Bits.intsToLong(x + chunkX, z + chunkZ), Integer.valueOf(types[x + z * sx]));
		areasMap.put(Integer.valueOf(dim), c);
	}
	
	public static void getMessage(int dim, int x, int z, FastList<String> l, boolean shift)
	{
		int type = getType(dim, x, z);
		ChunkType typeE = ChunkType.getChunkTypeFromI(type);
		
		if(typeE != null)
		{
			if(typeE.isClaimed())
			{
				LMPlayerClient owner = LMWorldClient.inst.getPlayer(type);
				if(owner != null) l.add(typeE.getChatColor(owner) + owner.getName());
			}
			else l.add(typeE.getChatColor(null) + typeE.getIDS());
		}
	}
}