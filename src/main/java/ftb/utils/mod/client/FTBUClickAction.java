package ftb.utils.mod.client;

import com.google.gson.JsonElement;
import ftb.lib.notification.*;
import ftb.utils.net.ClientAction;
import net.minecraftforge.fml.relauncher.*;

public class FTBUClickAction
{
	public static void init()
	{
		ClickActionRegistry.add(FRIEND_ADD);
		ClickActionRegistry.add(FRIEND_ADD_ALL);
	}
	
	public static final ClickAction FRIEND_ADD = new ClickAction("friend_add")
	{
		@SideOnly(Side.CLIENT)
		public void onClicked(JsonElement data)
		{ ClientAction.ADD_FRIEND.send(data.getAsInt()); }
	};
	
	public static final ClickAction FRIEND_ADD_ALL = new ClickAction("friend_add_all")
	{
		@SideOnly(Side.CLIENT)
		public void onClicked(JsonElement data)
		{ ClientAction.ADD_FRIEND.send(0); }
	};
}