package latmod.core.client.badges;

import latmod.core.util.FastMap;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class Badges
{
	public static final void init() {  }
	public static final FastMap<String, Badge> registry = new FastMap<String, Badge>();
	
	// Official //
	public static final Badge ftb = new Badge("ftb").register();
	public static final Badge moddev = new Badge("mods").register();
	public static final Badge packdev = new Badge("packs").register();
	
	// Custom //
	public static final Badge latmod = new Badge("latmod").register();
	public static final Badge razz = new Badge("razz").register();
	public static final Badge jaded = new Badge("jaded").register();
}