package ftb.utils.mod.client;

import com.google.gson.JsonElement;
import cpw.mods.fml.relauncher.*;
import ftb.lib.api.notification.*;
import ftb.utils.net.ClientAction;

public class FTBUClickAction
{
	public static void init()
	{
		ClickActionRegistry.add(FRIEND_ADD);
		ClickActionRegistry.add(FRIEND_ADD_ALL);
	}
	
	public static final ClickActionType FRIEND_ADD = new ClickActionType("friend_add")
	{
		@SideOnly(Side.CLIENT)
		public void onClicked(JsonElement data)
		{ ClientAction.ADD_FRIEND.send(data.getAsInt()); }
	};
	
	public static final ClickActionType FRIEND_ADD_ALL = new ClickActionType("friend_add_all")
	{
		@SideOnly(Side.CLIENT)
		public void onClicked(JsonElement data)
		{ ClientAction.ADD_FRIEND.send(0); }
	};
}