package latmod.ftbu.api;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.api.callback.*;
import latmod.ftbu.mod.client.FTBUClient;
import latmod.ftbu.mod.client.gui.*;
import latmod.ftbu.util.client.LatCoreMCClient;

public class LMGuis
{
	@SideOnly(Side.CLIENT)
	public static void displayColorSelector(IColorCallback cb, int col, int id, boolean instant)
	{
		if(FTBUClient.openHSB.getB())
			LatCoreMCClient.mc.displayGuiScreen(new GuiSelectColorHSB(cb, col, id, instant));
		else LatCoreMCClient.mc.displayGuiScreen(new GuiSelectColorRGB(cb, col, id, instant));
	}
	
	@SideOnly(Side.CLIENT)
	public static void displayFieldSelector(Object id, FieldType typ, Object d, IFieldCallback c)
	{ LatCoreMCClient.mc.displayGuiScreen(new GuiSelectField(id, typ, String.valueOf(d), c)); }
}