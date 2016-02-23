package ftb.utils.world;

import ftb.lib.api.players.*;
import ftb.utils.mod.FTBUFinals;
import ftb.utils.mod.client.FTBUClient;
import latmod.lib.Bits;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.fml.relauncher.*;

import java.util.*;

/**
 * Created by LatvianModder on 23.02.2016.
 */
@SideOnly(Side.CLIENT)
public class FTBUWorldDataSP extends ForgeWorldData
{
	public static FTBUWorldDataSP inst;
	
	public final Map<ChunkCoordIntPair, ChunkType> chunks;
	private int lastDimension = 0;
	
	public FTBUWorldDataSP(LMWorldSP w)
	{
		super(FTBUFinals.MOD_ID, w);
		chunks = new HashMap<>();
	}
	
	public void init()
	{
		inst = LMWorldSP.inst.serverDataIDs.contains(FTBUFinals.MOD_ID) ? this : null;
	}
	
	public void onClosed()
	{
		inst = null;
	}
	
	public ChunkType getType(int x, int z)
	{
		ChunkType i = chunks.get(Bits.intsToLong(x, z));
		return (i == null) ? ChunkType.UNLOADED : i;
	}
	
	public void setTypes(int dim, Map<ChunkCoordIntPair, ChunkType> types)
	{
		if(lastDimension != dim)
		{
			lastDimension = dim;
			chunks.clear();
		}
		
		chunks.putAll(types);
		
		if(FTBUClient.journeyMapHandler != null) FTBUClient.journeyMapHandler.refresh(dim);
	}
	
	public void getMessage(int x, int z, List<String> l, boolean shift)
	{
		ChunkType type = getType(x, z);
		
		if(type != ChunkType.UNLOADED)
		{
			if(type.asClaimed() != null)
			{
				LMPlayerSP owner = LMWorldSP.inst.getPlayer(type);
				if(owner != null) l.add(type.getChatColor(owner) + owner.getProfile().getName());
			}
			else l.add(type.getChatColor(null) + type.getIDS());
		}
	}
}
