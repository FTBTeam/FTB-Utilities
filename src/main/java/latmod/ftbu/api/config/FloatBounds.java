package latmod.ftbu.api.config;

import latmod.core.util.MathHelperLM;

public final class FloatBounds
{
	public final float defValue, minValue, maxValue;
	
	public FloatBounds(float def, float min, float max)
	{
		defValue = MathHelperLM.clampFloat(def, min, max);
		minValue = min; maxValue = max;
	}
	
	public FloatBounds(float def)
	{ this(def, Float.MIN_VALUE, Float.MAX_VALUE); }
	
	public float getVal(float v)
	{ return MathHelperLM.clampFloat(v, minValue, maxValue); }
}