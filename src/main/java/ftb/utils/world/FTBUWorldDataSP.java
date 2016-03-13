package ftb.utils.world;

import ftb.lib.ChunkDimPos;
import ftb.lib.api.*;
import ftb.utils.mod.FTBUFinals;
import ftb.utils.mod.client.FTBUClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.fml.relauncher.*;

import java.util.*;

/**
 * Created by LatvianModder on 23.02.2016.
 */
@SideOnly(Side.CLIENT)
public class FTBUWorldDataSP extends ForgeWorldData
{
	private static FTBUWorldDataSP inst = null;
	
	public static boolean exists()
	{ return inst != null || (ForgeWorldSP.inst != null && ForgeWorldSP.inst.serverDataIDs.contains(FTBUFinals.MOD_ID_LC)); }
	
	public static FTBUWorldDataSP get()
	{
		if(inst == null) inst = (FTBUWorldDataSP) ForgeWorldSP.inst.getData(FTBUFinals.MOD_ID_LC);
		return inst;
	}
	
	public final Map<ChunkDimPos, ChunkType> chunks;
	
	public FTBUWorldDataSP(ForgeWorldSP w)
	{
		super(FTBUFinals.MOD_ID_LC, w);
		chunks = new HashMap<>();
	}
	
	public void init()
	{
		get();
		//inst = ForgeWorldSP.inst.serverDataIDs.contains(FTBUFinals.MOD_ID) ? this : null;
	}
	
	public void readFromNet(NBTTagCompound tag)
	{
	}
	
	public void onClosed()
	{
		inst = null;
	}
	
	public ChunkType getType(ChunkCoordIntPair pos)
	{
		if(pos == null) return ChunkType.UNLOADED;
		ChunkType i = chunks.get(pos);
		return (i == null) ? ChunkType.UNLOADED : i;
	}
	
	public void setTypes(int dim, Map<ChunkDimPos, ChunkType> types)
	{
		chunks.putAll(types);
		if(FTBUClient.journeyMapHandler != null) FTBUClient.journeyMapHandler.refresh(dim);
	}
}
