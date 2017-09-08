package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbl.lib.config.ConfigValue;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * @author LatvianModder
 */
public interface IRank extends IStringSerializable, IJsonSerializable
{
	IRank getParent();

	Event.Result hasPermission(String permission);

	ConfigValue getConfig(String id);

	String getSyntax();

	default String getFormattedName(String name)
	{
		return getSyntax().replace("$name", name);
	}
}