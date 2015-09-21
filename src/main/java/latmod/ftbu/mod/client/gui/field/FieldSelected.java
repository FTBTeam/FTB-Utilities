package latmod.ftbu.mod.client.gui.field;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.core.client.LatCoreMCClient;

public class FieldSelected
{
	public final Object ID;
	public final boolean set;
	private final String result;
	public final boolean closeGui;
	
	public FieldSelected(Object id, boolean s, String r, boolean g)
	{
		ID = id;
		set = s;
		result = r;
		closeGui = g;
	}
	
	public String getS()
	{ return result; }
	
	public int getI()
	{ return Integer.parseInt(getS()); }
	
	public float getF()
	{ return Float.parseFloat(getS()); }
	
	@SideOnly(Side.CLIENT)
	public static void displayGui(Object id, FieldType typ, Object d, IFieldCallback c)
	{ LatCoreMCClient.mc.displayGuiScreen(new GuiSelectField(id, typ, String.valueOf(d), c)); }
}