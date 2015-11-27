package latmod.ftbu.api.client;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.mod.FTBU;
import net.minecraft.client.resources.I18n;

@SideOnly(Side.CLIENT)
public class FTBULang
{
	private static String get(String s, Object... o) { return I18n.format(FTBU.mod.assets + s, o); }
	
	public static String button_add_friend() { return get("button.add_friend"); }
	public static String button_rem_friend() { return get("button.rem_friend"); }
	public static String button_deny_friend() { return get("button.deny_friend"); }
	public static String button_server_info() { return get("button.server_info"); }
	
	public static String label_friend() { return get("label.friend"); }
	public static String label_pfriend() { return get("label.pfriend"); }
	
	public static String notifications() { return get("button.notifications"); }
	public static String claimed_chunks() { return get("button.claimed_chunks"); }
	public static String notes() { return get("button.notes"); }
	public static String mail() { return get("button.mail"); }
	public static String trade() { return get("button.trade"); }
}