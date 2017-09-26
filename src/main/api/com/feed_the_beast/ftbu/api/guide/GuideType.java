package com.feed_the_beast.ftbu.api.guide;

import com.feed_the_beast.ftbl.lib.NameMap;
import net.minecraft.util.IStringSerializable;

/**
 * @author LatvianModder
 */
public enum GuideType implements IStringSerializable
{
	SERVER_INFO("server_info"),
	MOD("mod"),
	MODPACK("modpack"),
	OTHER("other");

	public static final NameMap<GuideType> NAME_MAP = NameMap.create(OTHER, values());

	private final String name;

	GuideType(String s)
	{
		name = s;
	}

	@Override
	public String getName()
	{
		return name;
	}
}