package latmod.ftbu.core.client;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.core.EnumDyeColor;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.gui.friends.LMPComparator;
import latmod.ftbu.mod.player.ChunkType;
import net.minecraft.client.resources.I18n;

@SideOnly(Side.CLIENT)
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
	public static String button_refresh;
	
	// Friends //
	
	public static class Friends
	{
		public static String button_add_friend;
		public static String button_rem_friend;
		public static String button_deny_friend;
		
		public static String label_online;
		public static String label_friend;
		public static String label_pfriend;
		
		public static String notifications;
		public static String claimed_chunks;
		public static String notes;
		
		public static void reload()
		{
			button_add_friend = get("button.addFriend");
			button_rem_friend = get("button.remFriend");
			button_deny_friend = get("button.denyFriend");
			
			label_online = get("label.online");
			label_friend = get("label.friend");
			label_pfriend = get("label.pfriend");
			
			notifications = get("button.notifications");
			claimed_chunks = get("button.claimed_chunks");
			notes = get("button.notes");
			
			//LANG
			notifications = "Notifications";
			claimed_chunks = "Claimed Chunks";
			notes = "Notes";
		}
	}
	
	// Other //
	public static String client_config;
	public static final String[] colors = new String[16];
	public static String feature_disabled;
	public static final String[] chunk_types = new String[ChunkType.VALUES.length];
	
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
		button_refresh = get("button.refresh");
		
		Friends.reload();
		
		client_config = get("client_config");
		for(int i = 0; i < 16; i++)
			colors[i] = get("color." + EnumDyeColor.VALUES[i].name);
		feature_disabled = I18n.format("commands.lmdisabled");
		
		for(int i = 0; i < chunk_types.length; i++)
			chunk_types[i] = FTBU.mod.translateClient("chunktype." + ChunkType.VALUES[i].lang);
		
		for(LMPComparator c : LMPComparator.values())
			c.translatedName = I18n.format("lmp_comparator." + c.ID);
	}
	
	private static String get(String s)
	{ return FTBU.mod.translateClient(s); }
}