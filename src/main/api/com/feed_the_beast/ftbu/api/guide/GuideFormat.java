package com.feed_the_beast.ftbu.api.guide;

import com.feed_the_beast.ftbl.lib.NameMap;
import net.minecraft.util.IStringSerializable;

/**
 * @author LatvianModder
 */
public enum GuideFormat implements IStringSerializable
{
	JSON("json"),
	MD("md"),
	CUSTOM("custom"),
	UNSUPPORTED("unsupported");

	public static final NameMap<GuideFormat> NAME_MAP = NameMap.create(UNSUPPORTED, values());

	private final String name;

	GuideFormat(String s)
	{
		name = s;
	}

	@Override
	public String getName()
	{
		return name;
	}
}