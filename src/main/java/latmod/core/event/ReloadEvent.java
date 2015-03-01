package latmod.core.event;

import net.minecraft.command.ICommandSender;
import cpw.mods.fml.relauncher.Side;

public class ReloadEvent extends EventLM
{
	public final Side side;
	public final ICommandSender sender;
	
	public ReloadEvent(Side s, ICommandSender c)
	{ side = s; sender = c; }
}