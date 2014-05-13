package latmod.core.tile;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.tileentity.*;

public class DefaultLMGuiHandler implements ILMGuiHandler
{
	public Container getContainer(int ID, EntityPlayer ep, TileEntity te)
	{ return ((IDefGuiTile)te).getContainer(ep, ID); }

	@SideOnly(Side.CLIENT)
	public GuiContainer getGui(int ID, EntityPlayer ep, TileEntity te)
	{ return ((IDefGuiTile)te).getGui(ep, ID); }
}