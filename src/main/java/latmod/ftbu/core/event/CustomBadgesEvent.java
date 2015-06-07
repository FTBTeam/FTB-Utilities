package latmod.ftbu.core.event;

import java.util.UUID;

import latmod.ftbu.client.FTBUClientEventHandler;
import latmod.ftbu.core.client.badges.Badge;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class CustomBadgesEvent extends EventLM
{
	public void register(UUID user, Badge b)
	{ FTBUClientEventHandler.playerBadges.put(user, b); }
}