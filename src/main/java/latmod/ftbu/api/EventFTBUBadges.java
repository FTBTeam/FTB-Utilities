package latmod.ftbu.api;

import java.util.UUID;

import cpw.mods.fml.relauncher.*;
import latmod.core.util.FastMap;
import latmod.ftbu.badges.Badge;

@SideOnly(Side.CLIENT)
public class EventFTBUBadges extends EventLM
{
	public final FastMap<UUID, Badge> customBadges;
	
	public EventFTBUBadges(FastMap<UUID, Badge> l)
	{ customBadges = l; }
}