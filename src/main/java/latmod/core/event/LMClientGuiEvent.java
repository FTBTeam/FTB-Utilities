package latmod.core.event;

import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class LMClientGuiEvent extends EventLM
{
	public final NBTTagCompound args;
	
	public LMClientGuiEvent(NBTTagCompound t)
	{ args = t; }
}