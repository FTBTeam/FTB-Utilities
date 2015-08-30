package latmod.ftbu.core.waila;

import latmod.ftbu.core.tile.IWailaTile;

public enum WailaType
{
	STACK(IWailaTile.Stack.class),
	HEAD(IWailaTile.Head.class),
	BODY(IWailaTile.Body.class),
	TAIL(IWailaTile.Tail.class);
	
	public final Class<? extends IWailaTile> typeClass;
	
	WailaType(Class<? extends IWailaTile> c)
	{ typeClass = c; }
}