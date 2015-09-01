package latmod.ftbu.core.api.readme;

import latmod.ftbu.core.api.EventLM;

public class EventSaveReadme extends EventLM
{
	public final ReadmeFile file;
	
	public EventSaveReadme(ReadmeFile f)
	{
		file = f;
	}
}