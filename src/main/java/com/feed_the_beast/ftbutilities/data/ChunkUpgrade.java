package com.feed_the_beast.ftbutilities.data;

import com.feed_the_beast.ftblib.lib.ICustomName;
import com.feed_the_beast.ftblib.lib.util.FinalIDObject;
import com.feed_the_beast.ftblib.lib.util.LangKey;
import net.minecraft.util.text.ITextComponent;

/**
 * @author LatvianModder
 */
public final class ChunkUpgrade extends FinalIDObject implements ICustomName
{
	private LangKey langKey;
	private boolean internal;

	public ChunkUpgrade(String id, boolean i)
	{
		super(id);
		langKey = LangKey.of("ftbutilities.lang.chunks.upgrade." + getName());
		internal = i;
	}

	public ChunkUpgrade(String id)
	{
		this(id, false);
	}

	public LangKey getLangKey()
	{
		return langKey;
	}

	public boolean isInternal()
	{
		return internal;
	}

	@Override
	public ITextComponent getCustomDisplayName()
	{
		return getLangKey().textComponent(null);
	}
}