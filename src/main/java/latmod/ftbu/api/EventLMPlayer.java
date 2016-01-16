package latmod.ftbu.api;

import ftb.lib.api.EventLM;
import latmod.ftbu.world.LMPlayer;
import net.minecraftforge.fml.relauncher.Side;

public abstract class EventLMPlayer extends EventLM
{
	public abstract LMPlayer getPlayer();
	public abstract Side getSide();
}