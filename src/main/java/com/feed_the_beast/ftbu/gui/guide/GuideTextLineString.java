package com.feed_the_beast.ftbu.gui.guide;

import com.feed_the_beast.ftbl.lib.gui.GuiBase;
import com.feed_the_beast.ftbl.lib.gui.Panel;
import com.feed_the_beast.ftbl.lib.gui.TextField;
import com.feed_the_beast.ftbl.lib.gui.Widget;
import com.feed_the_beast.ftbu.api.guide.IGuidePage;
import com.feed_the_beast.ftbu.api.guide.IGuideTextLine;
import com.google.gson.JsonElement;

/**
 * @author LatvianModder
 */
public class GuideTextLineString extends EmptyGuidePageLine
{
	private final String text;

	public GuideTextLineString(String t)
	{
		text = t;
	}

	public GuideTextLineString(JsonElement e)
	{
		text = e.getAsString();
	}

	@Override
	public String getUnformattedText()
	{
		return text;
	}

	@Override
	public Widget createWidget(GuiBase gui, Panel parent)
	{
		return new TextField(gui, 0, 0, parent.width, -1, text);
	}

	@Override
	public IGuideTextLine copy(IGuidePage page)
	{
		return new GuideTextLineString(text);
	}
}