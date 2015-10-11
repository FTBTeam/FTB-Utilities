package latmod.ftbu.util;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.inv.ItemStackTypeAdapter;
import latmod.ftbu.notification.*;
import latmod.lib.LMJsonUtils;
import latmod.lib.config.ConfigList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

public class JsonHelper
{
	public static void init()
	{
		LMJsonUtils.register(IChatComponent.class, new IChatComponent.Serializer());
		LMJsonUtils.register(ChatStyle.class, new ChatStyle.Serializer());
		LMJsonUtils.register(ItemStack.class, new ItemStackTypeAdapter());
		LMJsonUtils.register(Notification.class, new Notification.Serializer());
		LMJsonUtils.register(ClickAction.class, new ClickAction.Serializer());
		LMJsonUtils.register(ConfigList.class, new ConfigList.Serializer());
	}
	
	@SideOnly(Side.CLIENT)
	public static void initClient()
	{
	}
}