package latmod.ftbu.api.config;

import cpw.mods.fml.common.eventhandler.Cancelable;
import latmod.ftbu.api.EventLM;
import latmod.lib.config.ConfigList;

@Cancelable
public class EventSyncedConfig extends EventLM
{
	public final ConfigList list;
	
	public EventSyncedConfig(ConfigList l)
	{ list = l; }
}