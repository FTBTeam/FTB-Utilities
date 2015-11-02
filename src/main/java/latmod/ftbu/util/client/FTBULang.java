package latmod.ftbu.util.client;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.mod.FTBU;
import net.minecraft.client.resources.I18n;

@SideOnly(Side.CLIENT)
public class FTBULang
{
	private static String get(String s, Object... o) { return FTBU.mod.translateClient(s, o); }
	
	// Buttons //
	public static String button_settings() { return get("button.settings"); }
	public static String button_back() { return get("button.back"); }
	public static String button_up() { return get("button.up"); }
	public static String button_down() { return get("button.down"); }
	public static String button_prev() { return get("button.prev"); }
	public static String button_next() { return get("button.next"); }
	public static String button_enabled() { return get("button.enabled"); }
	public static String button_disabled() { return get("button.disabled"); }
	public static String button_cancel() { return get("button.cancel"); }
	public static String button_accept() { return get("button.accept"); }
	public static String button_add() { return get("button.add"); }
	public static String button_remove() { return get("button.remove"); }
	public static String button_close() { return get("button.close"); }
	public static String button_save() { return get("button.save"); }
	public static String button_refresh() { return get("button.refresh"); }
	
	public static String client_config() { return get("client_config"); }
	public static String feature_disabled() { return I18n.format("commands.lmdisabled"); }
	public static String delete_item(String s) { return get("delete_item", s); }
	public static String label_server_forced(String s) { return get("label.server_forced", s); }
	
	public static class Guis
	{
		public static String button_add_friend() { return get("button.add_friend"); }
		public static String button_rem_friend() { return get("button.rem_friend"); }
		public static String button_deny_friend() { return get("button.deny_friend"); }
		public static String button_server_info() { return get("button.server_info"); }
		
		public static String label_online() { return get("label.online"); }
		public static String label_friend() { return get("label.friend"); }
		public static String label_pfriend() { return get("label.pfriend"); }
		
		public static String notifications() { return get("button.notifications"); }
		public static String claimed_chunks() { return get("button.claimed_chunks"); }
		public static String notes() { return get("button.notes"); }
		public static String mail() { return get("button.mail"); }
		public static String trade() { return get("button.trade"); }
	}
}