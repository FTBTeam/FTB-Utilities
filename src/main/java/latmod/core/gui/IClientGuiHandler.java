package latmod.core.gui;

import latmod.core.util.FastMap;
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
		public static final FastMap<String, IClientGuiHandler> map = new FastMap<String, IClientGuiHandler>();
		
		public static final void add(IClientGuiHandler h, String... s)
		{ for(String s1 : s) map.put(s1, h); }
	}
}