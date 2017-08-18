package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.events.registry.RegisterConfigEvent;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.gui.GuiLang;
import com.feed_the_beast.ftbu.FTBUFinals;

public class FTBUConfigRanks
{
	public static final PropertyBool ENABLED = new PropertyBool(true);
	public static final PropertyBool OVERRIDE_CHAT = new PropertyBool(true);
	public static final PropertyBool OVERRIDE_COMMANDS = new PropertyBool(true);

	public static void init(RegisterConfigEvent event)
	{
		String id = FTBUFinals.MOD_ID + ".ranks";
		event.register(id, "enabled", ENABLED).setNameLangKey(GuiLang.LABEL_ENABLED.getName());
		event.register(id, "override_chat", OVERRIDE_CHAT);
		event.register(id, "override_commands", OVERRIDE_COMMANDS);
	}
}