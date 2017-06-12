package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.IFTBLibRegistry;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.config.PropertyInt;
import com.feed_the_beast.ftbl.lib.config.PropertyString;
import com.feed_the_beast.ftbl.lib.gui.GuiLang;
import com.feed_the_beast.ftbu.FTBUFinals;

public class FTBUConfigWebAPI
{
	public static final PropertyBool ENABLED = new PropertyBool(false);
	public static final PropertyString FILE_LOCATION = new PropertyString("");
	public static final PropertyInt UPDATE_INTERVAL = new PropertyInt(5);

	public static void init(IFTBLibRegistry reg)
	{
		String id = FTBUFinals.MOD_ID + ".webapi";
		reg.addConfig(id, "enabled", ENABLED).setNameLangKey(GuiLang.LABEL_ENABLED.getName());
		reg.addConfig(id, "file_location", FILE_LOCATION);
		reg.addConfig(id, "update_interval", UPDATE_INTERVAL);
	}
}