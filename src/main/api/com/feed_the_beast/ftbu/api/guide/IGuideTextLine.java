package com.feed_the_beast.ftbu.api.guide;

import com.feed_the_beast.ftbl.lib.gui.GuiBase;
import com.feed_the_beast.ftbl.lib.gui.Panel;
import com.feed_the_beast.ftbl.lib.gui.Widget;

/**
 * @author LatvianModder
 */
public interface IGuideTextLine
{
	String getUnformattedText();

	Widget createWidget(GuiBase gui, Panel parent);

	IGuideTextLine copy(IGuidePage page);

	default boolean isEmpty()
	{
		return getUnformattedText().isEmpty();
	}
}