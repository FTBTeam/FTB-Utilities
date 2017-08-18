package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.events.registry.RegisterConfigEvent;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.config.PropertyItemStack;
import com.feed_the_beast.ftbl.lib.config.PropertyList;
import com.feed_the_beast.ftbl.lib.config.PropertyTextComponent;
import com.feed_the_beast.ftbu.FTBUFinals;

public class FTBUConfigLogin
{
	public static final PropertyBool ENABLE_MOTD = new PropertyBool(true);
	public static final PropertyBool ENABLE_STARTING_ITEMS = new PropertyBool(true);
	public static final PropertyList MOTD = new PropertyList(PropertyTextComponent.ID);
	public static final PropertyList STARTING_ITEMS = new PropertyList(PropertyItemStack.ID);

	public static void init(RegisterConfigEvent event)
	{
		String id = FTBUFinals.MOD_ID + ".login";
		event.register(id, "enable_motd", ENABLE_MOTD);
		event.register(id, "enable_starting_items", ENABLE_STARTING_ITEMS);
		event.register(id, "motd", MOTD);
		event.register(id, "starting_items", STARTING_ITEMS);
	}
}