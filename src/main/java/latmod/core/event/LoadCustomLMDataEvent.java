package latmod.core.event;

import net.minecraft.nbt.NBTTagCompound;

public class LoadCustomLMDataEvent extends EventLM
{
	public final Phase phase;
	public final NBTTagCompound tag;
	
	public LoadCustomLMDataEvent(Phase p, NBTTagCompound t)
	{ phase = p; tag = t; }
}