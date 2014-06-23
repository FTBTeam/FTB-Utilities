package latmod.core.mod;
import cpw.mods.fml.common.network.*;
import latmod.core.tile.*;
import net.minecraft.entity.player.*;
import net.minecraft.tileentity.*;
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
	
	public Object getServerGuiElement(int ID, EntityPlayer ep, World world, int x, int y, int z)
	{
		//if(LC.inst.ignoredGuiIDs.contains(ID)) return null;
		
		TileEntity te = world.getTileEntity(x, y, z);
		if(te != null && te instanceof IGuiTile)
		{
			if(te instanceof ISecureTile && !((ISecureTile)te).getSecurity().canPlayerInteract(ep)) return null;
			return ((IGuiTile)te).getContainer(ep, ID);
		}
		
		return null;
	}
	
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{ return null; }
}