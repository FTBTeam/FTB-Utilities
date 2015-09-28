package latmod.ftbu.api.callback;

public class ColorSelected
{
	public final Object ID;
	public final boolean set;
	public final int color;
	public final boolean closeGui;
	
	public ColorSelected(Object id, boolean s, int c, boolean g)
	{
		ID = id;
		set = s;
		color = c;
		closeGui = g;
	}
}