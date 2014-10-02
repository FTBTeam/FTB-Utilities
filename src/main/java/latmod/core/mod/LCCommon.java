package latmod.core.mod;

import latmod.core.mod.tile.IGuiTile;
import net.minecraft.entity.player.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class LCCommon implements IGuiHandler // LCClient
{
	public void preInit() { }
	public void init() { }
	public void postInit() { }
	
	public int getKeyID(String s) { return 0; }
	public boolean isKeyDown(int id) { return false; }
	public boolean isShiftDown() { return false; }
	public boolean isCtrlDown() { return false; }
	
	public boolean isKeyDown(String id) { return isKeyDown(getKeyID(id)); }
	
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
	
	public double getReachDist(EntityPlayer ep)
	{
		if(ep instanceof EntityPlayerMP)
			return ((EntityPlayerMP)ep).theItemInWorldManager.getBlockReachDistance();
		return 0F;
	}
	
	public void setSkinAndCape(EntityPlayer ep)
	{
	}
}