package latmod.latcore.client;

import latmod.core.util.FastMap;
import net.minecraftforge.client.event.RenderPlayerEvent;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public abstract class PlayerDecorator
{
	public static final FastMap<String, PlayerDecorator> map = new FastMap<String, PlayerDecorator>();
	
	static
	{
		map.put("latmod", new PDLatMod());
	}
	
	private String name = null;
	public String toString()
	{ if(name == null) name = map.getKey(this); return name; }
	
	public abstract void onPlayerRender(RenderPlayerEvent.Specials.Post e);
}