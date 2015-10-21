package latmod.ftbu.api.client;

import ftb.lib.client.FTBLibClient;
import latmod.ftbu.api.client.callback.*;
import latmod.ftbu.mod.client.FTBUClient;
import latmod.ftbu.mod.client.gui.*;

public class LMGuis
{
	public static void displayColorSelector(IColorCallback cb, int col, int id, boolean instant)
	{
		if(FTBUClient.openHSB.getB())
			FTBLibClient.mc.displayGuiScreen(new GuiSelectColorHSB(cb, col, id, instant));
		else FTBLibClient.mc.displayGuiScreen(new GuiSelectColorRGB(cb, col, id, instant));
	}
	
	public static void displayFieldSelector(Object id, FieldType typ, Object d, IFieldCallback c)
	{ FTBLibClient.mc.displayGuiScreen(new GuiSelectField(id, typ, String.valueOf(d), c)); }
}