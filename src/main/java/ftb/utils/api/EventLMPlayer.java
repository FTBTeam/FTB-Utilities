package ftb.utils.api;

import cpw.mods.fml.relauncher.Side;
import ftb.lib.api.EventLM;
import ftb.utils.world.LMPlayer;

public abstract class EventLMPlayer extends EventLM
{
	public abstract LMPlayer getPlayer();
	public abstract Side getSide();
}