package latmod.ftbu.core.api;

import latmod.ftbu.core.world.LMPlayer;
import cpw.mods.fml.relauncher.Side;

public abstract class LMPlayerEvent extends EventLM
{
	public abstract LMPlayer getPlayer();
	public abstract Side getSide();
}