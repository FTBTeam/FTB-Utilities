package com.feed_the_beast.ftbutilities.gui;

import com.feed_the_beast.ftblib.lib.gui.GuiBase;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.PanelScrollBar;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.gui.WidgetLayout;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBULang;
import com.feed_the_beast.ftbutilities.data.LeaderboardValue;
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
	private int rankSize, usernameSize, valueSize;

	private class LeaderboardEntry extends Widget
	{
		private final LeaderboardValue value;
		private final String rank;

		public LeaderboardEntry(GuiBase g, LeaderboardValue v)
		{
			super(g);
			setHeight(14);
			value = v;
			rank = value.color + "#" + StringUtils.add0s(v.rank, leaderboard.size());

			rankSize = Math.max(rankSize, gui.getStringWidth(rank) + 4);
			usernameSize = Math.max(usernameSize, gui.getStringWidth(v.username) + 8);
			valueSize = Math.max(valueSize, gui.getStringWidth(value.value.getFormattedText()) + 8);

			setWidth(rankSize + usernameSize + valueSize);
		}

		@Override
		public void addMouseOverText(List<String> list)
		{
		}

		@Override
		public void draw()
		{
			int ax = getAX();
			int ay = getAY();

			Icon widget = value.color == TextFormatting.DARK_GRAY ? gui.getTheme().getDisabledButton() : gui.getTheme().getButton(gui.isMouseOver(this));
			int textY = ay + (height - gui.getFontHeight() + 1) / 2;
			widget.draw(ax, ay, rankSize, height);
			gui.drawString(rank, ax + 2, textY, SHADOW);

			widget.draw(ax + rankSize, ay, usernameSize, height);
			gui.drawString(value.color + value.username, ax + 4 + rankSize, textY, SHADOW);

			widget.draw(ax + rankSize + usernameSize, ay, valueSize, height);
			String formattedText = value.value.getFormattedText();
			gui.drawString(value.color + formattedText, ax + rankSize + usernameSize + valueSize - gui.getStringWidth(formattedText) - 4, textY, SHADOW);
		}
	}

	public GuiLeaderboard(ITextComponent c, List<LeaderboardValue> l)
	{
		leaderboard = l;
		title = FTBULang.LEADERBOARDS.translate() + " > " + c.getFormattedText();

		panelButtons = new Panel(gui)
		{
			@Override
			public void addWidgets()
			{
				int i = 0;
				rankSize = 0;
				usernameSize = 0;
				valueSize = 0;

				for (LeaderboardValue value : leaderboard)
				{
					value.rank = ++i;
					add(new LeaderboardEntry(gui, value));
				}
			}

			@Override
			public void alignWidgets()
			{
				width = 0;

				for (Widget w : widgets)
				{
					setWidth(Math.max(width, w.width));
				}

				for (Widget w : widgets)
				{
					w.setWidth(width);
				}

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

		panelButtons.setPosAndSize(9, 9, 0, 146);
		panelButtons.addFlags(Panel.DEFAULTS);

		scrollBar = new PanelScrollBar(this, panelButtons)
		{
			@Override
			public boolean shouldDraw()
			{
				return true;
			}

			@Override
			public boolean canMouseScroll()
			{
				return true;
			}
		};

		scrollBar.setPosAndSize(0, 8, 16, 146);
	}

	@Override
	public void addWidgets()
	{
		add(panelButtons);

		if (panelButtons.widgets.size() > 10)
		{
			add(scrollBar);
		}
	}

	@Override
	public void alignWidgets()
	{
		scrollBar.setX(panelButtons.posX + panelButtons.width + 6);
		setWidth(scrollBar.posX + (panelButtons.widgets.size() > 10 ? scrollBar.width + 8 : 4));
		posX = (getScreen().getScaledWidth() - width) / 2;
	}

	@Override
	public void drawBackground()
	{
		drawString(title, getAX() + (width - gui.getStringWidth(title)) / 2, getAY() - getFontHeight() - 2, SHADOW);
	}
}