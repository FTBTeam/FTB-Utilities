package latmod.ftbu.api.client;

import cpw.mods.fml.relauncher.*;
import net.minecraft.client.resources.I18n;

public class ClientConfigProperty
{
	public final String id;
	public final int def;
	public final String[] values;
	public final int[] texCol;
	public final int[] texColMO;
	
	int value = -1;
	private boolean translateValues = true;
	
	public ClientConfigProperty(String s, int d, String... v)
	{
		id = s;
		def = d;
		values = v;
		
		texCol = new int[values.length];
		texColMO = new int[values.length];
		
		for(int i = 0; i < values.length; i++)
		{
			texCol[i] = 0xFF999999;
			texColMO[i] = 0xFFFFFFFF;
			
			if(values[i].equals("edit"))
			{
				texCol[i] = 0xFFAA00;
				texColMO[i] = 0xFFFF00;
			}
			else if(values[i].equals("true") || values[i].equals("enabled"))
			{
				texCol[i] = 0xFF339933;
				texColMO[i] = 0xFF33D333;
			}
			else if(values[i].equals("false") || values[i].equals("disabled"))
			{
				texCol[i] = 0xFF993333;
				texColMO[i] = 0xFFD33333;
			}
		}
	}
	
	public ClientConfigProperty(String s, boolean d)
	{ this(s, d ? 1 : 0, "disabled", "enabled"); }
	
	public void onClicked()
	{ setValue(value + 1); }
	
	public void setValue(int i)
	{
		value = i % values.length;
		if(value < 0) value = values.length + value;
	}
	
	public int getI()
	{ return (value == -1) ? def : value; }
	
	public boolean getB()
	{ return getI() == 1; }
	
	public int compareTo(ClientConfigProperty o)
	{ return getIDS().compareTo(o.getIDS()); }
	
	public String toString()
	{ return getIDS() + ": " + getValueS(getI()); }
	
	@SideOnly(Side.CLIENT)
	public String getIDS()
	{ return I18n.format("config.property." + id); }
	
	public String getValueS(int i)
	{ return translateValues ? I18n.format("config.value." + values[i]) : values[i]; }
	
	public ClientConfigProperty setRawValues()
	{ translateValues = false; return this; }
	
	public void initGui() { }
}