package latmod.core.event;

import java.io.File;

import net.minecraft.nbt.NBTTagCompound;

public class LoadLMDataEvent extends EventLM
{
	public final File latmodFolder;
	public final Phase phase;
	
	public LoadLMDataEvent(File f, Phase p)
	{ latmodFolder = f; phase = p; }
	
	public File getFile(String s)
	{ return new File(latmodFolder, s); }
	
	public static class CommonData extends EventLM
	{
		public final NBTTagCompound tag;
		
		public CommonData(NBTTagCompound t)
		{ tag = t; }
	}
}