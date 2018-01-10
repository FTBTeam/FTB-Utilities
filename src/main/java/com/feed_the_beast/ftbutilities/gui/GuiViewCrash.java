package com.feed_the_beast.ftbutilities.gui;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.gui.Button;
import com.feed_the_beast.ftblib.lib.gui.GuiBase;
import com.feed_the_beast.ftblib.lib.gui.GuiIcons;
import com.feed_the_beast.ftblib.lib.gui.GuiLang;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.PanelScrollBar;
import com.feed_the_beast.ftblib.lib.gui.SimpleButton;
import com.feed_the_beast.ftblib.lib.gui.TextField;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.gui.WidgetLayout;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBULang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author LatvianModder
 */
public class GuiViewCrash extends GuiBase
{
	private final List<String> text;
	private final TextField name;
	private final Panel textPanel;
	private final PanelScrollBar scrollH, scrollV;
	private final Button close, upload, reset;

	public GuiViewCrash(String n, Collection<String> l)
	{
		super(0, 0);
		name = new TextField(this, 8, 12, 0, 20, n);
		text = new ArrayList<>(l);

		textPanel = new Panel(this, 9, 33, 0, 0)
		{
			@Override
			public void addWidgets()
			{
				for (String s : text)
				{
					add(new TextField(gui, 2, 0, 0, 0, StringUtils.fixTabs(s, 2), Widget.UNICODE));
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

				scrollH.setElementSize(wi + 4);
				scrollH.setScrollStep(scrollH.getScrollStep() * 5D);
				scrollV.setElementSize(align(WidgetLayout.VERTICAL));
				scrollV.setScrollStep(scrollV.getScrollStep() * 3D);
			}

			@Override
			public Icon getIcon()
			{
				return gui.getTheme().getContainerSlot();
			}
		};

		textPanel.addFlags(Panel.DEFAULTS | Widget.UNICODE);

		scrollH = new PanelScrollBar(this, 8, 0, 0, 16, 0, textPanel)
		{
			@Override
			public Plane getPlane()
			{
				return Plane.HORIZONTAL;
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

		scrollV = new PanelScrollBar(this, 0, 32, 16, 0, 0, textPanel)
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

		close = new SimpleButton(this, 0, 8, GuiLang.CLOSE, GuiIcons.CLOSE, (gui1, button) -> gui1.closeGui())
		{
			@Override
			public Icon getIcon()
			{
				return getButtonBackground().withBorder(-2).combineWith(super.getIcon());
			}
		};

		upload = new SimpleButton(this, 0, 8, FTBULang.UPLOAD_CRASH, GuiIcons.UP, (gui1, button) ->
		{
			ClientUtils.execClientCommand("/ftb view_crash " + name.text.get(0) + " upload");
			gui1.closeGui(false);
		})
		{
			@Override
			public Icon getIcon()
			{
				return getButtonBackground().withBorder(-2).combineWith(super.getIcon());
			}
		};

		reset = new SimpleButton(this, 0, 0, "", Icon.EMPTY, (gui1, button) ->
		{
			scrollH.setValue(0D);
			scrollV.setValue(0D);
		});
	}

	@Override
	public void onInit()
	{
		setWidth(getScreen().getScaledWidth());
		setHeight(getScreen().getScaledHeight());

		close.setX(width - 24);
		upload.setX(width - 48);

		scrollH.width = width - 32;
		scrollV.height = height - 56;

		textPanel.setWidth(scrollH.width - 2);
		textPanel.setHeight(scrollV.height - 2);

		reset.posX = scrollV.posX = width - 24;
		reset.posY = scrollH.posY = height - 24;
	}

	@Override
	public void addWidgets()
	{
		addAll(textPanel, scrollH, scrollV, close, upload, reset, name);
	}
}