package ftb.utils.api;

import ftb.lib.api.EventLM;
import ftb.utils.world.LMPlayer;
import net.minecraftforge.fml.relauncher.Side;

public abstract class EventLMPlayer extends EventLM
{
	public abstract LMPlayer getPlayer();
	public abstract Side getSide();
}