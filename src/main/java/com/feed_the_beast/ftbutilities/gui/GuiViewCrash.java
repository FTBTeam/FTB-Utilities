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
import com.feed_the_beast.ftbutilities.FTBUtilitiesLang;

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
		name = new TextField(this, n);
		name.setPosAndSize(8, 12, 0, 20);
		text = new ArrayList<>(l);

		textPanel = new Panel(this)
		{
			@Override
			public void addWidgets()
			{
				for (String s : text)
				{
					add(new TextField(this, StringUtils.fixTabs(s, 2), Widget.UNICODE));
				}
			}

			@Override
			public void alignWidgets()
			{
				int wi = 0;

				for (Widget w : widgets)
				{
					w.setX(2);
					wi = Math.max(w.width, wi);
				}

				scrollH.setMaxValue(wi + 4);
				scrollV.setMaxValue(align(WidgetLayout.VERTICAL));
			}

			@Override
			public Icon getIcon()
			{
				return getTheme().getContainerSlot();
			}
		};

		textPanel.setPos(9, 33);
		textPanel.addFlags(Panel.DEFAULTS | Widget.UNICODE);

		scrollH = new PanelScrollBar(this, PanelScrollBar.Plane.HORIZONTAL, textPanel);
		scrollH.setCanAlwaysScroll(true);
		scrollH.setCanAlwaysScrollPlane(false);
		scrollH.setScrollStep(30);

		scrollV = new PanelScrollBar(this, textPanel);
		scrollV.setCanAlwaysScroll(true);
		scrollV.setCanAlwaysScrollPlane(false);
		scrollV.setScrollStep(10);

		close = new SimpleButton(this, GuiLang.CLOSE, GuiIcons.CLOSE, (widget, button) -> widget.getGui().closeGui())
		{
			@Override
			public Icon getIcon()
			{
				return getButtonBackground().withBorder(-2).combineWith(super.getIcon());
			}
		};

		upload = new SimpleButton(this, FTBUtilitiesLang.UPLOAD_CRASH, GuiIcons.UP, (widget, button) ->
		{
			ClientUtils.execClientCommand("/ftb view_crash " + name.text.get(0) + " upload");
			widget.getGui().closeGui(false);
		})
		{
			@Override
			public Icon getIcon()
			{
				return getButtonBackground().withBorder(-2).combineWith(super.getIcon());
			}
		};

		reset = new SimpleButton(this, "", Icon.EMPTY, (widget, button) ->
		{
			scrollH.setValue(0);
			scrollV.setValue(0);
		});
	}

	@Override
	public boolean onInit()
	{
		return setFullscreen();
	}

	@Override
	public void addWidgets()
	{
		add(textPanel);
		add(scrollH);
		add(scrollV);
		add(close);
		add(upload);
		add(reset);
		add(name);
	}

	@Override
	public void alignWidgets()
	{
		close.setPos(width - 24, 8);
		upload.setPos(width - 48, 8);
		reset.setPos(width - 24, height - 24);
		scrollH.setPosAndSize(8, height - 24, width - 32, 16);
		scrollV.setPosAndSize(width - 24, 32, 16, height - 56);
		textPanel.setSize(scrollH.width - 2, scrollV.height - 2);
		textPanel.alignWidgets();
	}
}