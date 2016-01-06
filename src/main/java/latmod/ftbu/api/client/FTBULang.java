package latmod.ftbu.api.client;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.mod.FTBU;
import net.minecraft.client.resources.I18n;

@SideOnly(Side.CLIENT)
public class FTBULang
{
	private static String get(String s, Object... o) { return I18n.format(FTBU.mod.assets + s, o); }
	
	public static String label_friend() { return get("label.friend"); }
	public static String label_pfriend() { return get("label.pfriend"); }
}