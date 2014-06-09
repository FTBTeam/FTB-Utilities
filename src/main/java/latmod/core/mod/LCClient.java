package latmod.core.mod;
import org.lwjgl.input.*;
import latmod.core.client.*;
import latmod.core.tile.*;
import net.minecraft.client.gui.*;
import net.minecraft.entity.player.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.*;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class LCClient extends LCCommon
{
	public void preInit()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public void init() { }
	public void postInit() { }
	
	@SubscribeEvent
	public void preTexturesLoaded(TextureStitchEvent.Pre e)
	{
		if(e.map.getTextureType() == 0)
			LatCoreClient.blockNullIcon = e.map.registerIcon(LCFinals.ASSETS + "nullIcon");
	}
	
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