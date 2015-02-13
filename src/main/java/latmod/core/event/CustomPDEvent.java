package latmod.core.event;

import java.util.UUID;

import latmod.core.FastList;
import latmod.core.client.playerdeco.PlayerDecorator;
import latmod.core.mod.client.LCClientEventHandler;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class CustomPDEvent extends EventLM
{
	public void register(UUID id, PlayerDecorator p)
	{
		FastList<PlayerDecorator> l = LCClientEventHandler.instance.playerDecorators.get(id);
		
		if(l == null)
		{
			l = new FastList<PlayerDecorator>();
			LCClientEventHandler.instance.playerDecorators.put(id, l);
		}
		
		l.add(p);
	}
}