package com.feed_the_beast.ftbu.api.guide;

import com.feed_the_beast.ftbu.gui.guide.GuidePage;
import com.google.gson.JsonElement;

/**
 * @author LatvianModder
 */
public interface IGuideTextLineProvider
{
	IGuideTextLine create(GuidePage page, JsonElement json);
}