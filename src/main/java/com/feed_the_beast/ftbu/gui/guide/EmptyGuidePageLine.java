package com.feed_the_beast.ftbu.gui.guide;

import com.feed_the_beast.ftbu.api.guide.IGuideTextLine;

/**
 * @author LatvianModder
 */
public abstract class EmptyGuidePageLine implements IGuideTextLine
{
	@Override
	public String getUnformattedText()
	{
		return "";
	}
}