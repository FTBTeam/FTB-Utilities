package latmod.core.event;

import latmod.core.client.badges.Badge;
import latmod.core.mod.client.LCClientEventHandler;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class CustomBadgesEvent extends EventLM
{
	public void register(String user, Badge b)
	{ LCClientEventHandler.playerBadges.put(user, b); }
}