package latmod.ftbu.core.event;

import java.io.File;

import latmod.ftbu.core.util.LMFileUtils;

public class SaveLMDataEvent extends EventLM
{
	public final File latmodFolder;
	
	public SaveLMDataEvent(File f)
	{ latmodFolder = f; }
	
	public File getFile(String s)
	{ return LMFileUtils.newFile(new File(latmodFolder, s)); }
}