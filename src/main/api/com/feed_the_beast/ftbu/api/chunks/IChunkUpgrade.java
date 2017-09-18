package com.feed_the_beast.ftbu.api.chunks;

import com.feed_the_beast.ftbl.api.ICustomName;
import com.feed_the_beast.ftbl.lib.LangKey;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.ITextComponent;

/**
 * @author LatvianModder
 */
public interface IChunkUpgrade extends IStringSerializable, ICustomName
{
	//TODO: Move this to a registry someday
	int getId();

	LangKey getLangKey();

	@Override
	default ITextComponent getCustomDisplayName()
	{
		return getLangKey().textComponent();
	}
}