package latmod.core.event;

import net.minecraft.nbt.NBTTagCompound;

public class SaveCustomLMDataEvent extends EventLM
{
	public final NBTTagCompound tag;
	
	public SaveCustomLMDataEvent(NBTTagCompound t)
	{ tag = t; }
}