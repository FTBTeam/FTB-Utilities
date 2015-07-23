package latmod.ftbu.core.event;

import java.io.File;

import net.minecraft.nbt.NBTTagCompound;

public class LoadLMDataEvent extends EventLM
{
	public final File latmodFolder;
	public final Phase phase;
	public final NBTTagCompound worldData;
	
	public LoadLMDataEvent(File f, Phase p, NBTTagCompound wd)
	{ latmodFolder = f; phase = p; worldData = wd; }
	
	public File getFile(String s)
	{ return new File(latmodFolder, s); }
}