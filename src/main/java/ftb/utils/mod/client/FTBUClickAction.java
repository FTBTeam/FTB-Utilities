package ftb.utils.mod.client;

import com.google.gson.JsonElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.api.notification.ClickActionRegistry;
import ftb.lib.api.notification.ClickActionType;
import ftb.utils.net.MessageManageFriends;
import latmod.lib.LMUtils;

public class FTBUClickAction
{
	public static void init()
	{
		ClickActionRegistry.add(FRIEND_ADD);
		ClickActionRegistry.add(FRIEND_ADD_ALL);
	}
	
	public static final ClickActionType FRIEND_ADD = new ClickActionType("friend_add")
	{
		@Override
		@SideOnly(Side.CLIENT)
		public void onClicked(JsonElement data)
		{ new MessageManageFriends(MessageManageFriends.ID_ADD, LMUtils.fromString(data.getAsString())).sendToServer(); }
	};
	
	public static final ClickActionType FRIEND_ADD_ALL = new ClickActionType("friend_add_all")
	{
		@Override
		@SideOnly(Side.CLIENT)
		public void onClicked(JsonElement data)
		{ new MessageManageFriends(MessageManageFriends.ID_ADD_ALL, null).sendToServer(); }
	};
}