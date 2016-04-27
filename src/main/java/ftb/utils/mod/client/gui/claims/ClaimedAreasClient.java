package ftb.utils.mod.client.gui.claims;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.utils.world.claims.ChunkType;
import net.minecraft.world.ChunkCoordIntPair;

import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ClaimedAreasClient
{
	private static final Map<ChunkCoordIntPair, ChunkType> chunks = new HashMap<>();
	private static int lastDimension = Integer.MIN_VALUE;
	
	public static void clear()
	{ chunks.clear(); }
	
	public static ChunkType getType(int x, int z)
	{
		ChunkType t = chunks.get(new ChunkCoordIntPair(x, z));
		if(t == null) return ChunkType.UNLOADED;
		return t;
	}
	
	public static void setTypes(int dim, Map<ChunkCoordIntPair, ChunkType> map)
	{
		if(lastDimension != dim)
		{
			lastDimension = dim;
			clear();
		}
		
		chunks.putAll(map);
	}
}