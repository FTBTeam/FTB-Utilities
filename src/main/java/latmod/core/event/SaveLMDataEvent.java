package latmod.core.event;

import java.io.File;

import latmod.core.util.LatCore;
import net.minecraft.nbt.NBTTagCompound;

public class SaveLMDataEvent extends EventLM
{
	public final File latmodFolder;
	
	public SaveLMDataEvent(File f)
	{ latmodFolder = f; }
	
	public File getFile(String s)
	{ return LatCore.newFile(new File(latmodFolder, s)); }
	
	public static class CommonData extends EventLM
	{
		public final NBTTagCompound tag;
		
		public CommonData(NBTTagCompound t)
		{ tag = t; }
	}
}