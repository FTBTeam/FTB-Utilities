package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.ForgeWorld;
import com.feed_the_beast.ftbl.api.ForgeWorldData;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.client.FTBUClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

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
	
	@Override
	public void onLoaded(ForgeWorld w)
	{
		chunks = new HashMap<>();
	}
	
	@Override
	public void readFromNet(NBTTagCompound tag, boolean login)
	{
	}
	
	@Override
	public void onClosed()
	{
		chunks = null;
		inst = null;
	}
	
	public ChunkType getType(ChunkCoordIntPair pos)
	{
		if(pos == null) { return ChunkType.UNLOADED; }
		ChunkType i = chunks.get(pos);
		return (i == null) ? ChunkType.UNLOADED : i;
	}
	
	public void setTypes(DimensionType dim, Map<ChunkDimPos, ChunkType> types)
	{
		chunks.putAll(types);
		if(FTBUClient.journeyMapHandler != null) { FTBUClient.journeyMapHandler.refresh(dim.getId()); }
	}
}
