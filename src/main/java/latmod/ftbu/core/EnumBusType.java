package latmod.ftbu.core;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventBus;

public enum EnumBusType
{
	LATMOD,
	FORGE,
	FML;
	
	public static final EnumBusType VALUES[] = values();
	public static final EventBus FTBU_EVENT_BUS = new EventBus();
	
	public EventBus getBus()
	{
		if(this == LATMOD) return FTBU_EVENT_BUS;
		else if(this == FORGE) return MinecraftForge.EVENT_BUS;
		return FMLCommonHandler.instance().bus();
	}
	
	public void register(Object o)
	{ getBus().register(o); }
	
	public void unregister(Object o)
	{ getBus().unregister(o); }
	
	public static void registerAll(Object o)
	{ for(EnumBusType e : VALUES) e.register(o); }
}