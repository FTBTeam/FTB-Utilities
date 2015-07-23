package latmod.ftbu.core.event;

import com.google.gson.GsonBuilder;

public class LMGsonEvent extends EventLM // LatCore
{
	public final GsonBuilder builder;
	
	public LMGsonEvent(GsonBuilder gb)
	{ builder = gb; }
}