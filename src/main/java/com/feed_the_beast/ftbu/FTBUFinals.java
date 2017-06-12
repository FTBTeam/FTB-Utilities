package com.feed_the_beast.ftbu;

import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FTBUFinals
{
	public static final String MOD_ID = "ftbu";
	public static final Logger LOGGER = LogManager.getLogger("FTBUtilities");

	public static ResourceLocation get(String id)
	{
		return new ResourceLocation(MOD_ID, id);
	}
}