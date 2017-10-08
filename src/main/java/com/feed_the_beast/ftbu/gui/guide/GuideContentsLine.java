package com.feed_the_beast.ftbu.gui.guide;

import com.feed_the_beast.ftbl.lib.gui.GuiBase;
import com.feed_the_beast.ftbl.lib.gui.Panel;
import com.feed_the_beast.ftbl.lib.gui.Widget;
import com.feed_the_beast.ftbl.lib.gui.WidgetLayout;
import com.feed_the_beast.ftbu.api.guide.IGuidePage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class GuideContentsLine extends EmptyGuidePageLine
{
	private final IGuidePage page;

	public GuideContentsLine(IGuidePage p)
	{
		page = p;
	}

	@Override
	public Widget createWidget(GuiBase gui, Panel parent)
	{
		return new PanelGuideContents(gui);
	}

	@Override
	public GuideContentsLine copy(IGuidePage page)
	{
		return new GuideContentsLine(page);
	}

	private class PanelGuideContents extends Panel
	{
		private final List<Widget> buttons;

		private PanelGuideContents(GuiBase gui)
		{
			super(gui, 0, 0, 10, 10);
			buttons = new ArrayList<>();
			addButtons(page, 0);
		}

		private void addButtons(IGuidePage from, int level)
		{
			setWidth(10000);

			for (IGuidePage p : from.getChildren().values())
			{
				Widget w = new ButtonGuidePage(gui, p, true);
				w.posX = level * 12;
				w.width = 1000;
				buttons.add(w);
				addButtons(p, level + 1);
			}
		}

		@Override
		public void addWidgets()
		{
			addAll(buttons);
			updateWidgetPositions();
		}

		@Override
		public void updateWidgetPositions()
		{
			setHeight(align(WidgetLayout.VERTICAL));
		}
	}

	@Override
	public boolean isEmpty()
	{
		return page.getChildren().isEmpty();
	}
}