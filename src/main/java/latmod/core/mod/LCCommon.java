package latmod.core.mod;

import latmod.core.*;
import latmod.core.tile.IGuiTile;
import net.minecraft.entity.player.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class LCCommon extends LMProxy implements IGuiHandler // LCClient
{
	public boolean isShiftDown() { return false; }
	public boolean isCtrlDown() { return false; }
	public boolean isTabDown() { return false; }
	public boolean inGameHasFocus() { return true; }
	
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof IGuiTile)
			return ((IGuiTile)te).getContainer(player, ID);
		return null;
	}
	
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}
	
	public EntityPlayer getClientPlayer()
	{ return null; }
	
	public World getClientWorld()
	{ return null; }
	
	public double getReachDist(EntityPlayer ep)
	{
		if(ep instanceof EntityPlayerMP)
			return ((EntityPlayerMP)ep).theItemInWorldManager.getBlockReachDistance();
		return 0F;
	}
	
	public void openClientGui(EntityPlayer ep, IGuiTile i, int ID) { }
	public void notifyPlayer(Notification n) { }
	public void spawnDust(World w, double x, double y, double z, int col) { }
	public void receiveLMPlayerUpdate(LMPlayer p, String action) { }
}