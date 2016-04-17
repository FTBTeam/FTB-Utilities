package ftb.utils.world;

import ftb.lib.ChunkDimPos;
import ftb.lib.api.*;
import ftb.utils.FTBUFinals;
import ftb.utils.client.FTBUClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.*;
import net.minecraftforge.fml.relauncher.*;

import java.util.*;

/**
 * Created by LatvianModder on 23.02.2016.
 */
@SideOnly(Side.CLIENT)
public class FTBUWorldDataSP extends ForgeWorldData
{
	private static FTBUWorldDataSP inst;
	
	public static FTBUWorldDataSP get()
	{
		if(inst == null)
		{
			inst = new FTBUWorldDataSP();
		}
		
		return inst;
	}
	
	public Map<ChunkDimPos, ChunkType> chunks;
	
	private FTBUWorldDataSP()
	{
		super(FTBUFinals.MOD_ID);
	}
	
	public void onLoaded(ForgeWorld w)
	{
		chunks = new HashMap<>();
	}
	
	public void readFromNet(NBTTagCompound tag, boolean login)
	{
	}
	
	public void onClosed()
	{
		chunks = null;
		inst = null;
	}
	
	public ChunkType getType(ChunkCoordIntPair pos)
	{
		if(pos == null) return ChunkType.UNLOADED;
		ChunkType i = chunks.get(pos);
		return (i == null) ? ChunkType.UNLOADED : i;
	}
	
	public void setTypes(DimensionType dim, Map<ChunkDimPos, ChunkType> types)
	{
		chunks.putAll(types);
		if(FTBUClient.journeyMapHandler != null) FTBUClient.journeyMapHandler.refresh(dim.getId());
	}
}
