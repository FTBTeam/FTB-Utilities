package com.feed_the_beast.ftbu.gui;

import com.feed_the_beast.ftbl.lib.gui.GuiBase;
import com.feed_the_beast.ftbl.lib.gui.Panel;
import com.feed_the_beast.ftbl.lib.gui.PanelScrollBar;
import com.feed_the_beast.ftbl.lib.gui.Widget;
import com.feed_the_beast.ftbl.lib.gui.WidgetLayout;
import com.feed_the_beast.ftbl.lib.icon.Icon;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.api.LeaderboardValue;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

/**
 * @author LatvianModder
 */
public class GuiLeaderboard extends GuiBase
{
	private final Panel panelButtons;
	private final PanelScrollBar scrollBar;
	private final String title;
	private final List<LeaderboardValue> leaderboard;

	private class LeaderboardEntry extends Widget
	{
		private final LeaderboardValue value;
		private String title;

		public LeaderboardEntry(GuiBase g, LeaderboardValue v)
		{
			super(g, 0, 0, 0, 14);
			value = v;
			title = value.color + "#" + StringUtils.add0s(v.rank, leaderboard.size()) + " " + value.username + ": ";
			setWidth(gui.getStringWidth(title + value.value.getFormattedText()) + 8);
		}

		public boolean renderTitleInCenter()
		{
			return false;
		}

		@Override
		public void addMouseOverText(List<String> list)
		{
		}

		@Override
		public void renderWidget()
		{
			int ax = getAX();
			int ay = getAY();

			(value.color == TextFormatting.DARK_GRAY ? gui.getTheme().getDisabledButton() : gui.getTheme().getButton(gui.isMouseOver(this))).draw(ax, ay, width, height);
			int textX = ax;
			int textY = ay + (height - gui.getFontHeight() + 1) / 2;

			String text = title + value.value.getFormattedText();

			if (renderTitleInCenter())
			{
				textX += (width - gui.getStringWidth(text)) / 2;
			}
			else
			{
				textX += 4;
			}

			gui.drawString(text, textX, textY, SHADOW);
		}
	}

	public GuiLeaderboard(ITextComponent c, List<LeaderboardValue> l)
	{
		super(0, 0);
		leaderboard = l;
		title = FTBULang.LEADERBOARDS.translate() + " > " + c.getFormattedText();

		panelButtons = new Panel(gui, 9, 9, 0, 146)
		{
			@Override
			public void addWidgets()
			{
				width = 0;
				int i = 0;

				for (LeaderboardValue value : leaderboard)
				{
					value.rank = ++i;
					add(new LeaderboardEntry(gui, value));
				}

				for (Widget w : widgets)
				{
					setWidth(Math.max(width, w.width));
				}

				for (Widget w : widgets)
				{
					w.setWidth(width);
				}

				updateWidgetPositions();
			}

			@Override
			public void updateWidgetPositions()
			{
				int size = align(WidgetLayout.VERTICAL);
				scrollBar.setElementSize(size);
				scrollBar.setSrollStepFromOneElementSize(14);
				setHeight(widgets.size() > 10 ? 144 : size);
				gui.setHeight(height + 18);
			}

			@Override
			public Icon getIcon()
			{
				return gui.getTheme().getPanelBackground();
			}
		};

		panelButtons.addFlags(Panel.DEFAULTS);

		scrollBar = new PanelScrollBar(this, 0, 8, 16, 146, 0, panelButtons)
		{
			@Override
			public boolean shouldRender()
			{
				return true;
			}

			@Override
			public boolean canMouseScroll()
			{
				return true;
			}
		};
	}

	@Override
	public void addWidgets()
	{
		add(panelButtons);

		if (panelButtons.widgets.size() > 7)
		{
			add(scrollBar);
		}

		scrollBar.setX(panelButtons.posX + panelButtons.width + 6);
		setWidth(scrollBar.posX + (panelButtons.widgets.size() > 7 ? scrollBar.width + 8 : 4));
		posX = (getScreen().getScaledWidth() - width) / 2;
	}

	@Override
	public void drawBackground()
	{
		drawString(title, getAX() + (width - gui.getStringWidth(title)) / 2, getAY() - getFontHeight() - 2, SHADOW);
	}
}