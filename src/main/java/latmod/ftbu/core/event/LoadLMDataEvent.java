package latmod.ftbu.core.event;

import java.io.File;

public class LoadLMDataEvent extends EventLM
{
	public final File latmodFolder;
	public final Phase phase;
	
	public LoadLMDataEvent(File f, Phase p)
	{ latmodFolder = f; phase = p; }
	
	public File getFile(String s)
	{ return new File(latmodFolder, s); }
}