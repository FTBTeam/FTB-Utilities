package latmod.core.event;

import latmod.core.FastList;
import latmod.core.client.playerdeco.PlayerDecorator;
import latmod.core.mod.client.LCClientEventHandler;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class CustomPDEvent extends EventLM
{
	public void register(String s, PlayerDecorator p)
	{
		FastList<PlayerDecorator> l = LCClientEventHandler.instance.playerDecorators.get(s);
		
		if(l == null)
		{
			l = new FastList<PlayerDecorator>();
			LCClientEventHandler.instance.playerDecorators.put(s, l);
		}
		
		l.add(p);
	}
}