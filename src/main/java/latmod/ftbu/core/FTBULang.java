package latmod.ftbu.core;

import latmod.ftbu.mod.FTBU;

public class FTBULang
{
	public static String button_settings;
	public static String button_back;
	public static String button_up;
	public static String button_down;
	public static String button_prev;
	public static String button_next;
	public static String button_enabled;
	public static String button_disabled;
	public static String button_cancel;
	public static String button_accept;
	public static String button_add;
	public static String button_remove;
	public static String button_close;
	public static String button_save;
	
	public static void reload()
	{
		button_settings = get("button.settings");
		button_back = get("button.back");
		button_up = get("button.up");
		button_down = get("button.down");
		button_prev = get("button.prev");
		button_next = get("button.next");
		button_enabled = get("button.enabled");
		button_disabled = get("button.disabled");
		button_cancel = get("button.cancel");
		button_accept = get("button.accept");
		button_add = get("button.add");
		button_remove = get("button.remove");
		button_close = get("button.close");
		button_save = get("button.save");
	}
	
	private static String get(String s)
	{ return FTBU.mod.translate(s); }
}