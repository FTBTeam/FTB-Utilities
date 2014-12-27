package latmod.core.event;

import latmod.core.FastList;
import latmod.core.mod.LC;

public enum Key
{
	SHIFT,
	CRTL,
	TAB;
	
	public static final Key[] VALUES = values();
	
	public boolean isPressed()
	{
		if(this == SHIFT) return LC.proxy.isShiftDown();
		if(this == CRTL) return LC.proxy.isCtrlDown();
		if(this == TAB) return LC.proxy.isTabDown();
		return false;
	}
	
	public static FastList<Key> toList(int[] ai)
	{
		FastList<Key> l = new FastList<Key>();
		
		if(ai == null)
		{
			for(int i = 0; i < VALUES.length; i++)
				if(VALUES[i].isPressed()) l.add(VALUES[i]);
		}
		else
		{
			for(int i = 0; i < ai.length; i++)
				l.add(VALUES[ai[i]]);
		}
		return l;
	}
	
	public static int[] fromList(FastList<Key> l)
	{
		int[] ai = new int[l.size()];
		for(int i = 0; i < ai.length; i++)
			ai[i] = l.get(i).ordinal();
		return ai;
	}
}