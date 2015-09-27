package latmod.ftbu.mod;

import java.util.UUID;

import latmod.ftbu.api.readme.ReadmeFile;
import latmod.ftbu.tile.TileLM;
import latmod.ftbu.world.LMWorld;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.world.World;

public class FTBUCommon // FTBUClient
{
	public void preInit()
	{
	}
	
	public void postInit()
	{
	}
	
	public void onReadmeEvent(ReadmeFile file)
	{
	}
	
	public boolean isShiftDown() { return false; }
	public boolean isCtrlDown() { return false; }
	public boolean isTabDown() { return false; }
	public boolean inGameHasFocus() { return true; }
	
	public EntityPlayer getClientPlayer()
	{ return null; }
	
	public EntityPlayer getClientPlayer(UUID id)
	{ return null; }
	
	public World getClientWorld()
	{ return null; }
	
	public LMWorld<?> getClientWorldLM()
	{ return null; }
	
	public double getReachDist(EntityPlayer ep)
	{
		if(ep instanceof EntityPlayerMP)
			return ((EntityPlayerMP)ep).theItemInWorldManager.getBlockReachDistance();
		return 0D;
	}
	
	public void spawnDust(World w, double x, double y, double z, int col) { }
	public boolean openClientGui(EntityPlayer ep, String mod, int id, NBTTagCompound data) { return false; }
	public void readTileData(TileLM t, S35PacketUpdateTileEntity p) { }
}