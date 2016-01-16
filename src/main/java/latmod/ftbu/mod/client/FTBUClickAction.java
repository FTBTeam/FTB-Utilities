package latmod.ftbu.mod.client;

import ftb.lib.notification.*;
import latmod.ftbu.net.ClientAction;
import latmod.lib.PrimitiveType;
import net.minecraftforge.fml.relauncher.*;

public class FTBUClickAction
{
	public static void init()
	{
		ClickActionRegistry.add(FRIEND_ADD);
		ClickActionRegistry.add(FRIEND_ADD_ALL);
	}
	
	public static final ClickAction FRIEND_ADD = new ClickAction("friend_add", PrimitiveType.INT)
	{
		@SideOnly(Side.CLIENT)
		public void onClicked(MouseAction c)
		{ ClientAction.ADD_FRIEND.send(c.intVal()); }
	};
	
	public static final ClickAction FRIEND_ADD_ALL = new ClickAction("friend_add_all", PrimitiveType.NULL)
	{
		@SideOnly(Side.CLIENT)
		public void onClicked(MouseAction c)
		{ ClientAction.ADD_FRIEND.send(0); }
	};
}