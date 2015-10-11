package latmod.ftbu.api.client;

import java.util.UUID;

import latmod.ftbu.api.EventLM;
import latmod.ftbu.badges.Badge;
import latmod.lib.FastMap;

public class EventFTBUBadges extends EventLM
{
	public final FastMap<UUID, Badge> customBadges;
	
	public EventFTBUBadges(FastMap<UUID, Badge> l)
	{ customBadges = l; }
}