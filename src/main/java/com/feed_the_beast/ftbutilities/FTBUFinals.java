package com.feed_the_beast.ftbutilities;

import com.feed_the_beast.ftblib.lib.util.LangKey;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FTBUFinals
{
	public static final String MOD_ID = "ftbutilities";
	public static final String MOD_NAME = "FTBUtilities";
	public static final String VERSION = "@VERSION@";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	public static ResourceLocation get(String id)
	{
		return new ResourceLocation(MOD_ID, id);
	}

	public static LangKey lang(String key, Class... args)
	{
		return LangKey.of(MOD_ID + '.' + key, args);
	}
}