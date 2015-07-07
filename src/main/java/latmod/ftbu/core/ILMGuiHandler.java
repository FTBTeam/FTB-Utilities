package latmod.ftbu.core;

import latmod.ftbu.core.util.FastMap;
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
	
	public static class Registry
	{
		private static final FastMap<String, ILMGuiHandler> guiHandlers = new FastMap<String, ILMGuiHandler>();
		
		public static ILMGuiHandler getLMGuiHandler(String id)
		{ return guiHandlers.get(id); }
		
		public static void addLMGuiHandler(String id, ILMGuiHandler i)
		{ guiHandlers.put(id, i); }
	}
}