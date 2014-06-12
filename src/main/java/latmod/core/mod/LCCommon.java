package latmod.core.mod;
import cpw.mods.fml.common.network.*;
import latmod.core.*;
import latmod.core.tile.*;
import net.minecraft.entity.player.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

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
	
	public MovingObjectPosition rayTrace(EntityPlayer ep, double d)
	{
		Vec3 pos = ep.worldObj.getWorldVec3Pool().getVecFromPool(ep.posX, ep.posY + 1.62D, ep.posZ);
		Vec3 look = ep.getLook(1F);
		Vec3 vec = pos.addVector(look.xCoord * d, look.yCoord * d, look.zCoord * d);
		return ep.worldObj.clip(pos, vec);
	}
	
	public Object getServerGuiElement(int ID, EntityPlayer ep, World world, int x, int y, int z)
	{
		//if(LC.inst.ignoredGuiIDs.contains(ID)) return null;
		
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te != null && te instanceof IGuiTile)
		{
			boolean canOpen = true;
			
			if(te instanceof ISecureTile)
			{
				ISecureTile st = (ISecureTile)te;
				if(st.enableSecurity() && st.getSecurity() != null && !st.getSecurity().canPlayerInteract(ep))
					canOpen = false;
			}
			
			if(canOpen) return ((IGuiTile)te).getContainer(ep, ID);
		}
		
		return null;
	}
	
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{ return null; }
}