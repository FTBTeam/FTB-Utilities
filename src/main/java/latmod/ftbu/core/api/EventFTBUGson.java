package latmod.ftbu.core.api;

import com.google.gson.GsonBuilder;

public class EventFTBUGson extends EventLM
{
	public final GsonBuilder builder;
	
	public EventFTBUGson(GsonBuilder b)
	{ builder = b; }
}