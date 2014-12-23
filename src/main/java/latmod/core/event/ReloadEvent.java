package latmod.core.event;

import cpw.mods.fml.relauncher.Side;

public class ReloadEvent extends EventLM
{
	public static final String ACTION = "ReloadClient";
	
	public final Side side;
	
	public ReloadEvent(Side s)
	{ side = s; }
}