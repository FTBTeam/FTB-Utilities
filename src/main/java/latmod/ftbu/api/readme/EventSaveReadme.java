package latmod.ftbu.api.readme;

import latmod.ftbu.api.EventLM;

public class EventSaveReadme extends EventLM
{
	public final ReadmeFile file;
	
	public EventSaveReadme(ReadmeFile f)
	{
		file = f;
	}
}