package com.feed_the_beast.ftbu.gui.guide;

import com.feed_the_beast.ftbl.lib.Color4I;
import com.feed_the_beast.ftbl.lib.NameMap;
import com.feed_the_beast.ftbl.lib.gui.GuiBase;
import com.feed_the_beast.ftbl.lib.gui.Panel;
import com.feed_the_beast.ftbl.lib.gui.PanelScrollBar;
import com.feed_the_beast.ftbl.lib.gui.Widget;
import com.feed_the_beast.ftbl.lib.gui.WidgetLayout;
import com.feed_the_beast.ftbl.lib.icon.Icon;
import com.feed_the_beast.ftbl.lib.icon.IconBullet;
import com.feed_the_beast.ftbl.lib.icon.TexturelessRectangle;
import com.feed_the_beast.ftbu.api.guide.IGuidePage;
import com.feed_the_beast.ftbu.api.guide.IGuideTextLine;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class GuideListLine extends EmptyGuidePageLine
{
	private static final Icon CODE_BACKGROUND = new TexturelessRectangle(0x33AAAAAA);
	private static final Icon SCROLL_BAR_BACKGROUND = new TexturelessRectangle(0x33333333);

	public enum Ordering implements IStringSerializable
	{
		NONE("none", 0),
		BULLET("bullet", 8),
		NUMBER("number", 12),
		LETTER("letter", 10);

		public static final NameMap<Ordering> NAME_MAP = NameMap.create(BULLET, values());

		private final String name;
		public final int size;

		Ordering(String n, int s)
		{
			name = n;
			size = s;
		}

		@Override
		public String getName()
		{
			return name;
		}
	}

	public enum Type implements IStringSerializable
	{
		NONE("none", EnumFacing.Plane.VERTICAL, Ordering.BULLET),
		CODE("code", EnumFacing.Plane.VERTICAL, Ordering.NONE),
		HORIZONTAL("horizontal", EnumFacing.Plane.HORIZONTAL, Ordering.NONE);

		public static final NameMap<Type> NAME_MAP = NameMap.create(NONE, values());

		private final String name;
		public final EnumFacing.Plane plane;
		public final Ordering defaultOrdering;

		Type(String s, EnumFacing.Plane d, Ordering o)
		{
			name = s;
			plane = d;
			defaultOrdering = o;
		}

		@Override
		public String getName()
		{
			return name;
		}
	}

	private final List<IGuideTextLine> textLines;
	private final Type type;
	private final Ordering ordering;
	private final int spacing;

	public GuideListLine(GuidePage p, JsonElement json)
	{
		textLines = new ArrayList<>();

		if (json.isJsonObject())
		{
			JsonObject o = json.getAsJsonObject();

			if (o.has("list"))
			{
				for (JsonElement element : o.get("list").getAsJsonArray())
				{
					IGuideTextLine line = p.createLine(element);

					if (line != null)
					{
						textLines.add(line);
					}
				}
			}

			type = o.has("type") ? Type.NAME_MAP.get(o.get("type").getAsString()) : Type.NONE;
			ordering = (type != Type.HORIZONTAL && o.has("ordering")) ? Ordering.NAME_MAP.get(o.get("ordering").getAsString()) : type.defaultOrdering;
			spacing = o.has("spacing") ? o.get("spacing").getAsInt() : 0;
		}
		else
		{
			for (JsonElement element : json.getAsJsonArray())
			{
				IGuideTextLine line = p.createLine(element);

				if (line != null)
				{
					textLines.add(line);
				}
			}

			type = Type.NONE;
			ordering = type.defaultOrdering;
			spacing = 0;
		}
	}

	public GuideListLine(List<IGuideTextLine> l, Type t, Ordering o, int s)
	{
		textLines = l;
		type = t;
		ordering = type == Type.HORIZONTAL ? Ordering.NONE : o;
		spacing = s;
	}

	@Override
	public Widget createWidget(GuiBase gui, Panel parent)
	{
		return new PanelList(gui, parent.hasFlag(Panel.FLAG_UNICODE_FONT));
	}

	@Override
	public GuideListLine copy(IGuidePage page)
	{
		GuideListLine line = new GuideListLine(new ArrayList<>(textLines.size()), type, ordering, spacing);
		for (IGuideTextLine line1 : textLines)
		{
			line.textLines.add(line1.copy(page));
		}
		return line;
	}

	@Override
	public boolean isEmpty()
	{
		for (IGuideTextLine line : textLines)
		{
			if (!line.isEmpty())
			{
				return false;
			}
		}

		return true;
	}

	private class PanelList extends Panel
	{
		private final GuiBase gui;
		private final PanelScrollBar scrollBar;
		private final WidgetLayout layout;
		private IconBullet bullet;

		private PanelList(GuiBase g, boolean unicodeFont)
		{
			super(ordering.size, 0, 0, 0);
			gui = g;
			//addFlags(FLAG_DEFAULTS);

			scrollBar = new PanelScrollBar(0, 0, 1, 4, 0, this)
			{
				@Override
				public EnumFacing.Plane getPlane()
				{
					return EnumFacing.Plane.HORIZONTAL;
				}
			};

			scrollBar.background = SCROLL_BAR_BACKGROUND;

			if (unicodeFont)
			{
				addFlags(FLAG_UNICODE_FONT);
			}

			layout = type.plane == EnumFacing.Plane.VERTICAL ? new WidgetLayout.Vertical(0, spacing, 0) : new WidgetLayout.Horizontal(0, spacing, 0);
			bullet = new IconBullet().setColor(g.getContentColor());
		}

		@Override
		public void addWidgets()
		{
			if (isEmpty())
			{
				setWidth(0);
				setHeight(0);
				return;
			}

			setWidth((type == Type.CODE) ? 10000 : (getParentPanel().width - ordering.size));

			for (IGuideTextLine line : textLines)
			{
				add(line.createWidget(gui, this));
			}

			getParentPanel().add(scrollBar);
			updateWidgetPositions();
		}

		@Override
		public void updateWidgetPositions()
		{
			if (widgets.isEmpty())
			{
				setWidth(0);
				setHeight(0);
				return;
			}

			align(layout);
			Widget last = widgets.get(widgets.size() - 1);
			int s;

			if (type.plane == EnumFacing.Plane.HORIZONTAL)
			{
				setHeight(0);

				for (Widget widget : widgets)
				{
					setHeight(Math.max(height, widget.height));
				}

				s = last.posX + last.width;
			}
			else
			{
				setHeight(last.posY + last.height);
				s = 0;

				for (Widget widget : widgets)
				{
					s = Math.max(s, widget.width);
				}

				s += ordering.size;
			}

			setWidth(Math.min(s, getParentPanel().width) - ordering.size);
			scrollBar.setWidth(width + ordering.size);
			scrollBar.setElementSize(s - ordering.size);
			scrollBar.setSrollStepFromOneElementSize(10);
			scrollBar.sliderSize = scrollBar.width / 10;
		}

		@Override
		protected void renderWidget(GuiBase gui, Widget widget, int index, int ax, int ay, int w, int h)
		{
			widget.renderWidget(gui);

			if (ordering.size > 0 && widget.getClass() != Widget.class && !(widget instanceof PanelList))
			{
				String n;
				switch (ordering)
				{
					case BULLET:
						bullet.draw(ax - 7, widget.getAY() + 3, 4, 4, Color4I.NONE);
						break;
					case NUMBER:
						n = Integer.toString(index + 1);
						gui.drawString(n, ax - 1 - gui.getFont().getStringWidth(n), widget.getAY() + 1, gui.getContentColor());
						break;
					case LETTER:
						char c = (char) ('a' + index);
						if (c > 'z')
						{
							c = (char) ('A' + index);
						}
						if (c > 'Z')
						{
							c = '-';
						}

						n = Character.toString(c);
						gui.drawString(n, ax - 1 - gui.getFont().getStringWidth(n), widget.getAY() + 1, gui.getContentColor());
						break;
				}
			}
		}

		@Override
		protected void renderPanelBackground(GuiBase gui, int ax, int ay)
		{
			if (type == Type.CODE)
			{
				CODE_BACKGROUND.draw(ax - ordering.size, ay, width + ordering.size, height, Color4I.NONE);
			}
		}
	}
}