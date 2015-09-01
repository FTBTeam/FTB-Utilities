package latmod.ftbu.core;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventBus;

public enum EnumBusType
{
	FORGE(MinecraftForge.EVENT_BUS),
	FML(FMLCommonHandler.instance().bus());
	
	public final EventBus eventBus;
	
	EnumBusType(EventBus eb)
	{ eventBus = eb; }
	
	public static final EnumBusType VALUES[] = { FORGE, FML };
	
	public static void register(Object o)
	{ for(EnumBusType e : VALUES) e.eventBus.register(o); }
	
	public static void unregister(Object o)
	{ for(EnumBusType e : VALUES) e.eventBus.unregister(o); }
}