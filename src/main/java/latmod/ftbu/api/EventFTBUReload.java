package latmod.ftbu.api;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.command.ICommandSender;

public class EventFTBUReload extends EventLM
{
	public final Side side;
	public final ICommandSender sender;
	
	public EventFTBUReload(Side s, ICommandSender ics)
	{ side = s; sender = ics; }
}