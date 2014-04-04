package latmod.core;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;

public interface IGuiTile extends ITileInterface
{
	public Container getContainer(EntityPlayer ep, int ID);

	@SideOnly(Side.CLIENT)
	public GuiContainer getGui(EntityPlayer ep, int ID);
}