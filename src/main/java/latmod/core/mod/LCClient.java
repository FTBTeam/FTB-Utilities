package latmod.core.mod;
import org.lwjgl.input.*;
import latmod.core.tile.*;
import net.minecraft.client.gui.*;
import net.minecraft.entity.player.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class LCClient extends LCCommon
{
	public void preInit() { }
	public void init() { }
	public void postInit() { }
	
	public int getKeyID(String s) { return Keyboard.getKeyIndex(s); }
	public boolean isKeyDown(int id) { return Keyboard.isKeyDown(id); }
	public boolean isShiftDown() { return GuiScreen.isShiftKeyDown(); }
	public boolean isCtrlDown() { return GuiScreen.isCtrlKeyDown(); }
	
	public Object getClientGuiElement(int ID, EntityPlayer ep, World world, int x, int y, int z)
	{
		//if(LC.inst.ignoredGuiIDs.contains(ID)) return null;
		
		TileEntity te = world.getTileEntity(x, y, z);
		if(te != null && te instanceof IGuiTile)
		{
			if(te instanceof ISecureTile && !((ISecureTile)te).getSecurity().canPlayerInteract(ep)) return null;
			return ((IGuiTile)te).getGui(ep, ID);
		}
		
		return null;
	}
}