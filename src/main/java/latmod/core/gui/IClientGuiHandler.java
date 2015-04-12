package latmod.core.gui;

import latmod.core.util.FastList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public interface IClientGuiHandler
{
	public GuiScreen displayGui(String s, NBTTagCompound data, EntityPlayer ep);
	
	public static class Registry
	{
		public static final FastList<IClientGuiHandler> list = new FastList<IClientGuiHandler>();
		
		public static final void add(IClientGuiHandler h)
		{ list.add(h); }
	}
}