package latmod.core.tile;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;

public interface ILMGuiHandler
{
	public Container getContainer(int ID, EntityPlayer ep, TileEntity te);
	
	@SideOnly(Side.CLIENT)
	public GuiContainer getGui(int ID, EntityPlayer ep, TileEntity te);
}