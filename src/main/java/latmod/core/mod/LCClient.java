package latmod.core.mod;
import org.lwjgl.input.*;
import latmod.core.IGuiTile;
import latmod.core.ISecureTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
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
	
	//public MovingObjectPosition rayTrace(EntityPlayer ep, double d)
	//{ return ep.rayTrace(d, 1F); }
	
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if(te != null && te instanceof IGuiTile)
		{
			if(te instanceof ISecureTile && !((ISecureTile)te).getSecurity().canPlayerInteract(player)) return null;
			Object c = ((IGuiTile)te).getGui(player, ID);
			//System.out.println("Opening gui " + c);
			return c;
		}
		
		return null;
	}
}