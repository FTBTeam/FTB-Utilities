package latmod.ftbu.mod.client.gui.field.color;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.mod.client.FTBUClient;

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
	
	@SideOnly(Side.CLIENT)
	public static void displayGui(IColorCallback cb, int col, int id, boolean instant)
	{
		if(FTBUClient.openHSB.getB())
			LatCoreMCClient.mc.displayGuiScreen(new GuiSelectColorHSB(cb, col, id, instant));
		else LatCoreMCClient.mc.displayGuiScreen(new GuiSelectColorRGB(cb, col, id, instant));
	}
}