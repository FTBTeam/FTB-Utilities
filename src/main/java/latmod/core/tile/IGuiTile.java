package latmod.core.tile;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import cpw.mods.fml.relauncher.*;

public interface IGuiTile extends ITileInterface
{
	public Container getContainer(EntityPlayer ep, int ID);
	
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer ep, int ID);
}