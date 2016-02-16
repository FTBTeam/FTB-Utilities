package ftb.utils.mod.client.gui.claims;

import ftb.lib.api.friends.*;
import ftb.utils.mod.client.FTBUClient;
import ftb.utils.world.claims.ChunkType;
import latmod.lib.Bits;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.fml.relauncher.*;

import java.util.*;

@SideOnly(Side.CLIENT)
public class ClaimedAreasClient
{
	public static final HashMap<ChunkCoordIntPair, ChunkType> chunks = new HashMap<>();
	private static int lastDimension = 0;
	
	public static void clear()
	{ chunks.clear(); }
	
	public static ChunkType getType(int x, int z)
	{
		ChunkType i = chunks.get(Bits.intsToLong(x, z));
		return (i == null) ? ChunkType.UNLOADED : i;
	}
	
	public static void setTypes(int dim, Map<ChunkCoordIntPair, ChunkType> types)
	{
		if(lastDimension != dim)
		{
			lastDimension = dim;
			clear();
		}
		
		chunks.putAll(types);
		
		if(FTBUClient.journeyMapHandler != null) FTBUClient.journeyMapHandler.refresh(dim);
	}
	
	public static void getMessage(int x, int z, List<String> l, boolean shift)
	{
		ChunkType type = getType(x, z);
		
		if(type != ChunkType.UNLOADED)
		{
			if(type.isClaimed())
			{
				LMPlayerSP owner = LMWorldSP.inst.getPlayer(type);
				if(owner != null) l.add(type.getChatColor(owner) + owner.getProfile().getName());
			}
			else l.add(type.getChatColor(null) + type.getIDS());
		}
	}
}