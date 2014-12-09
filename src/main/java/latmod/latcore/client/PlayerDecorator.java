package latmod.latcore.client;

import latmod.core.util.FastMap;
import net.minecraftforge.client.event.RenderPlayerEvent;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public abstract class PlayerDecorator
{
	public static final FastMap<String, PlayerDecorator> map = new FastMap<String, PlayerDecorator>();
	
	public static void init()
	{
		map.put("latmod", new PDLatMod());
	}
	
	public abstract void onPlayerRender(RenderPlayerEvent.Specials.Post e);
}