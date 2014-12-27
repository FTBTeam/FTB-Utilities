package latmod.core.event;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.relauncher.Side;

@Cancelable
public class LMKeyEvent extends EventLM
{
	public final Side side;
	public final boolean shiftDown;
	public final boolean ctrlDown;
	public final EntityPlayer player;
	
	public LMKeyEvent(Side s, boolean b1, boolean b2, EntityPlayer p)
	{ side = s; shiftDown = b1; ctrlDown = b2; player = p; }
}