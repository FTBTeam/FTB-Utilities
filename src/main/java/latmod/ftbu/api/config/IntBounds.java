package latmod.ftbu.api.config;

import latmod.core.util.MathHelperLM;

public final class IntBounds
{
	public final int defValue, minValue, maxValue;
	
	public IntBounds(int def, int min, int max)
	{
		defValue = def;
		minValue = min;
		maxValue = max;
	}
	
	public IntBounds(int def)
	{ this(def, Integer.MIN_VALUE, Integer.MAX_VALUE); }
	
	public int getVal(int v)
	{ return MathHelperLM.clampInt(v, minValue, maxValue); }
}