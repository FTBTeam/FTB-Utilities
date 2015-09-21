package latmod.ftbu.core.api;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.core.util.FastList;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.client.gui.friends.PlayerAction;

@SideOnly(Side.CLIENT)
public class EventPlayerAction extends EventLM
{
	public final FastList<PlayerAction> actions;
	public final LMPlayerClient player;
	public final boolean isSelf;
	
	public EventPlayerAction(FastList<PlayerAction> l, LMPlayerClient p)
	{
		actions = l;
		player = p;
		isSelf = player.equalsPlayer(LMWorldClient.inst.clientPlayer);
	}
}