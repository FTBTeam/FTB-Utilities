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
				int wi = 0;

				for (String s : text)
				{
					TextField f = new TextField(gui, StringUtils.fixTabs(s, 2), Widget.UNICODE);
					f.setX(2);
					wi = Math.max(wi, f.width);
					add(f);
				}

				scrollH.setElementSize(wi + 4);
				scrollH.setScrollStep(scrollH.getScrollStep() * 5D);
				scrollV.setElementSize(align(WidgetLayout.VERTICAL));
				scrollV.setScrollStep(scrollV.getScrollStep() * 3D);
			}

			@Override
			public void alignWidgets()
			{

			}

			@Override
			public Icon getIcon()
			{
				return gui.getTheme().getContainerSlot();
			}
		};

		textPanel.setPos(9, 33);
		textPanel.addFlags(Panel.DEFAULTS | Widget.UNICODE);

		scrollH = new PanelScrollBar(this, textPanel)
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
			public boolean shouldDraw()
			{
				return true;
			}
		};

		scrollV = new PanelScrollBar(this, textPanel)
		{
			@Override
			public boolean canMouseScroll()
			{
				return true;
			}

			@Override
			public boolean shouldDraw()
			{
				return true;
			}
		};

		close = new SimpleButton(this, GuiLang.CLOSE, GuiIcons.CLOSE, (gui1, button) -> gui1.closeGui())
		{
			@Override
			public Icon getIcon()
			{
				return getButtonBackground().withBorder(-2).combineWith(super.getIcon());
			}
		};

		upload = new SimpleButton(this, FTBUtilitiesLang.UPLOAD_CRASH, GuiIcons.UP, (gui1, button) ->
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

		reset = new SimpleButton(this, "", Icon.EMPTY, (gui1, button) ->
		{
			scrollH.setValue(0D);
			scrollV.setValue(0D);
		});
	}

	@Override
	public boolean onInit()
	{
		return setFullscreen();
	}

	@Override
	public void onPostInit()
	{
		close.setPos(width - 24, 8);
		upload.setPos(width - 48, 8);
		scrollH.setPosAndSize(8, height - 24, width - 32, 16);
		scrollV.setPosAndSize(width - 24, 32, 16, height - 56);
		textPanel.setSize(scrollH.width - 2, scrollV.height - 2);
		reset.setPos(width - 24, height - 24);
	}

	@Override
	public void addWidgets()
	{
		addAll(textPanel, scrollH, scrollV, close, upload, reset, name);
	}
}