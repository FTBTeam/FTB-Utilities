package latmod.ftbu.core.api;

import cpw.mods.fml.relauncher.Side;
import latmod.ftbu.core.world.LMPlayer;

public abstract class LMPlayerEvent extends EventLM
{
	public abstract LMPlayer getPlayer();
	public abstract Side getSide();
}