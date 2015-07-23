package latmod.ftbu.core.event;

import java.io.File;

import latmod.ftbu.core.util.LMFileUtils;
import net.minecraft.nbt.NBTTagCompound;

public class SaveLMDataEvent extends EventLM
{
	public final File latmodFolder;
	public final NBTTagCompound worldData;
	
	public SaveLMDataEvent(File f, NBTTagCompound wd)
	{ latmodFolder = f; worldData = wd; }
	
	public File getFile(String s)
	{ return LMFileUtils.newFile(new File(latmodFolder, s)); }
}