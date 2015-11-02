package latmod.ftbu.api.client;

import ftb.lib.api.EventLM;
import latmod.ftbu.mod.client.gui.friends.PlayerAction;
import latmod.ftbu.world.*;
import latmod.lib.FastList;

public class EventPlayerAction extends EventLM
{
	public final FastList<PlayerAction> actions;
	public final LMPlayerClient player;
	public final boolean isSelf;
	
	public EventPlayerAction(FastList<PlayerAction> l, LMPlayerClient p)
	{
		actions = l;
		player = p;
		isSelf = player.equalsPlayer(LMWorldClient.inst.getClientPlayer());
	}
}