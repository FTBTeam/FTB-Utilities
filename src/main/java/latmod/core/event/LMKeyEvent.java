package latmod.core.event;

import latmod.core.FastList;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.relauncher.Side;

@Cancelable
public class LMKeyEvent extends EventLM
{
	public final Side side;
	public final FastList<Key> keys;
	public final EntityPlayer player;
	
	public LMKeyEvent(Side s, FastList<Key> l, EntityPlayer p)
	{ side = s; keys = l; player = p; }
}