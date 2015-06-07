package latmod.ftbu.core;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.*;

public interface ILMGuiHandler
{
	public Container getContainer(EntityPlayer ep, String id, NBTTagCompound data);
	
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer ep, String id, NBTTagCompound data);
}