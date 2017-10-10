package com.feed_the_beast.ftbu.gui;

import com.feed_the_beast.ftbl.lib.gui.GuiBase;
import com.feed_the_beast.ftbl.lib.gui.Panel;
import com.feed_the_beast.ftbl.lib.gui.PanelScrollBar;
import com.feed_the_beast.ftbl.lib.gui.TextField;
import com.feed_the_beast.ftbl.lib.gui.Widget;
import com.feed_the_beast.ftbl.lib.gui.WidgetLayout;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author LatvianModder
 */
public class GuiViewCrash extends GuiBase
{
	private final List<String> text;
	private final Panel textPanel;
	private final PanelScrollBar scrollH, scrollV;

	public GuiViewCrash(Collection<String> l)
	{
		super(0, 0);
		text = new ArrayList<>(l);

		textPanel = new Panel(this, 8, 8, 0, 0)
		{
			@Override
			public void addWidgets()
			{
				for (String s : text)
				{
					add(new TextField(gui, 0, 0, gui.getStringWidth(s) + 2, 11, s));
				}

				updateWidgetPositions();
			}

			@Override
			public void updateWidgetPositions()
			{
				int wi = 0;
				for (Widget w : widgets)
				{
					wi = Math.max(wi, w.width);
				}

				scrollH.setElementSize(wi);
				scrollV.setElementSize(align(new WidgetLayout.Vertical(8, 0, 8)));
			}
		};

		textPanel.addFlags(Panel.DEFAULTS);

		scrollH = new PanelScrollBar(this, 0, 0, 0, 16, 0, textPanel)
		{
			@Override
			public EnumFacing.Plane getPlane()
			{
				return EnumFacing.Plane.HORIZONTAL;
			}

			@Override
			public boolean canMouseScroll()
			{
				return true;
			}

			@Override
			public boolean shouldRender()
			{
				return true;
			}
		};

		scrollV = new PanelScrollBar(this, 0, 0, 16, 0, 0, textPanel)
		{
			@Override
			public boolean canMouseScroll()
			{
				return true;
			}

			@Override
			public boolean shouldRender()
			{
				return true;
			}
		};
	}

	@Override
	public void onInit()
	{
		setWidth(getScreen().getScaledWidth() - 16);
		setHeight(getScreen().getScaledHeight() - 16);
		textPanel.setWidth(width - 16);
		textPanel.setHeight(height - 16);
		scrollH.posY = scrollV.height = height;
		scrollV.posX = scrollH.width = width;
	}

	@Override
	public void addWidgets()
	{
		addAll(textPanel, scrollH, scrollV);
	}
}