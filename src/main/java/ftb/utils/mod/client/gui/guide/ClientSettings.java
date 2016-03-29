package ftb.utils.mod.client.gui.guide;

import ftb.lib.api.config.*;
import latmod.lib.LMColor;
import latmod.lib.annotations.NumberBounds;

/**
 * Created by LatvianModder on 22.03.2016.
 */
public class ClientSettings
{
	public static final ConfigEntryBool unicode = new ConfigEntryBool("unicode", true);
	public static final ConfigEntryColor text_color = new ConfigEntryColor("text_color", new LMColor.RGB(123, 101, 52));
	public static final ConfigEntryColor bg_color = new ConfigEntryColor("bg_color", new LMColor.RGB(247, 244, 218));
	
	@NumberBounds(min = 100, max = 255)
	public static final ConfigEntryInt transparency = new ConfigEntryInt("transparency", 240);
}