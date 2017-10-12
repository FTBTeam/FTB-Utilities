package com.feed_the_beast.ftbu.gui.guide;

import com.feed_the_beast.ftbl.lib.gui.GuiBase;
import com.feed_the_beast.ftbl.lib.gui.Panel;
import com.feed_the_beast.ftbl.lib.gui.Widget;
import com.feed_the_beast.ftbl.lib.icon.Color4I;
import com.feed_the_beast.ftbl.lib.icon.MutableColor4I;
import com.feed_the_beast.ftbu.api.guide.IGuidePage;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author LatvianModder
 */
public class GuideHrLine extends EmptyGuidePageLine
{
	public final int height;
	public final MutableColor4I color;

	public GuideHrLine(int h, Color4I c)
	{
		height = h;
		color = c.mutable();
	}

	public GuideHrLine(JsonElement e)
	{
		JsonObject o = e.getAsJsonObject();
		height = o.has("height") ? Math.max(1, o.get("height").getAsInt()) : 1;
		color = Color4I.fromJson(o.get("color")).mutable();
	}

	@Override
	public Widget createWidget(GuiBase gui, Panel parent)
	{
		return new WidgetGuideHr(gui, parent);
	}

	@Override
	public GuideHrLine copy(IGuidePage page)
	{
		return new GuideHrLine(height, color);
	}

	private class WidgetGuideHr extends Widget
	{
		private WidgetGuideHr(GuiBase gui, Panel parent)
		{
			super(gui, 0, 1, parent.width, GuideHrLine.this.height + 2);
		}

		@Override
		public void renderWidget()
		{
			(color.isEmpty() ? gui.getTheme().getContentColor() : color).draw(getAX(), getAY() + 1, width, height - 2);
		}
	}

	@Override
	public boolean isEmpty()
	{
		return false;
	}
}