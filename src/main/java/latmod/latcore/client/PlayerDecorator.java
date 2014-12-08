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
		register(new PDLatMod());
	}
	
	private static void register(PlayerDecorator p)
	{ map.put(p.ID, p); }
	
	public final String ID;
	
	public PlayerDecorator(String s)
	{
		ID = s;
	}
	
	public boolean equals(Object o)
	{ return (o + "").equals(ID); }
	
	public String toString()
	{ return ID; }

	public abstract void onPlayerRender(RenderPlayerEvent.Specials.Post e);
}