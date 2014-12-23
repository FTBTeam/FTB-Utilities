package latmod.core.event;

import net.minecraft.nbt.NBTTagCompound;

public class LoadCustomLMDataEvent extends EventLM
{
	public final NBTTagCompound tag;
	
	public LoadCustomLMDataEvent(NBTTagCompound t)
	{ tag = t; }
}