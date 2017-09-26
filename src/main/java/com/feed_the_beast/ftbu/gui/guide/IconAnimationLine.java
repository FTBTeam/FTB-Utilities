package com.feed_the_beast.ftbu.gui.guide;

import com.feed_the_beast.ftbl.lib.gui.GuiBase;
import com.feed_the_beast.ftbl.lib.gui.IconAnimationButton;
import com.feed_the_beast.ftbl.lib.gui.Panel;
import com.feed_the_beast.ftbl.lib.gui.Widget;
import com.feed_the_beast.ftbl.lib.icon.Icon;
import com.feed_the_beast.ftbl.lib.icon.IconAnimation;
import com.feed_the_beast.ftbu.api.guide.IGuidePage;
import com.feed_the_beast.ftbu.api.guide.IGuideTextLine;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.math.MathHelper;

import java.util.Collections;

/**
 * @author LatvianModder
 */
public class IconAnimationLine extends EmptyGuidePageLine
{
	private final IconAnimation list;
	private final int cols;

	public IconAnimationLine(IconAnimation l, int columns)
	{
		list = l;
		cols = MathHelper.clamp(columns, 0, 16);
	}

	public IconAnimationLine(JsonElement json)
	{
		list = new IconAnimation(Collections.emptyList());

		if (json.isJsonObject())
		{
			JsonObject o = json.getAsJsonObject();
			cols = MathHelper.clamp(o.has("columns") ? o.get("columns").getAsInt() : 8, 0, 16);

			if (o.has("objects"))
			{
				for (JsonElement e : o.get("objects").getAsJsonArray())
				{
					list.list.add(Icon.getIcon(e));
				}
			}
		}
		else
		{
			cols = 8;

			for (JsonElement e : json.getAsJsonArray())
			{
				list.list.add(Icon.getIcon(e));
			}
		}
	}

	@Override
	public Widget createWidget(GuiBase gui, Panel parent)
	{
		return new IconAnimationButton(0, 0, list, cols);
	}

	@Override
	public IGuideTextLine copy(IGuidePage page)
	{
		return new IconAnimationLine(list, cols);
	}

	@Override
	public boolean isEmpty()
	{
		return list.list.isEmpty();
	}
}