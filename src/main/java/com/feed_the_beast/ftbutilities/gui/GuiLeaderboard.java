package com.feed_the_beast.ftbutilities.gui;

import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.Theme;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.gui.WidgetType;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiButtonListBase;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.data.LeaderboardValue;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

/**
 * @author LatvianModder
 */
public class GuiLeaderboard extends GuiButtonListBase
{
	private final List<LeaderboardValue> leaderboard;
	private int rankSize, usernameSize, valueSize;

	private class LeaderboardEntry extends Widget
	{
		private final LeaderboardValue value;
		private final String rank;

		public LeaderboardEntry(Panel panel, LeaderboardValue v)
		{
			super(panel);
			value = v;
			rank = value.color + "#" + StringUtils.add0s(v.rank, leaderboard.size());

			Theme theme = getGui().getTheme();
			rankSize = Math.max(rankSize, theme.getStringWidth(rank) + 4);
			usernameSize = Math.max(usernameSize, theme.getStringWidth(v.username) + 8);
			valueSize = Math.max(valueSize, theme.getStringWidth(value.value.getFormattedText()) + 8);

			setSize(rankSize + usernameSize + valueSize, 14);
		}

		@Override
		public void addMouseOverText(List<String> list)
		{
		}

		@Override
		public void draw(Theme theme, int x, int y, int w, int h)
		{
			WidgetType type = value.color == TextFormatting.DARK_GRAY ? WidgetType.DISABLED : WidgetType.mouseOver(isMouseOver());
			int textY = y + (h - theme.getFontHeight() + 1) / 2;
			theme.drawButton(x, y, rankSize, h, type);
			theme.drawString(rank, x + 2, textY, Theme.SHADOW);

			theme.drawButton(x + rankSize, y, usernameSize, h, type);
			theme.drawString(value.color + value.username, x + 4 + rankSize, textY, Theme.SHADOW);

			theme.drawButton(x + rankSize + usernameSize, y, valueSize, h, type);
			String formattedText = value.value.getFormattedText();
			theme.drawString(value.color + formattedText, x + rankSize + usernameSize + valueSize - theme.getStringWidth(formattedText) - 4, textY, Theme.SHADOW);
		}
	}

	public GuiLeaderboard(ITextComponent c, List<LeaderboardValue> l)
	{
		setTitle(I18n.format("sidebar_button.ftbutilities.leaderboards") + " > " + c.getFormattedText());
		setHasSearchBox(true);
		leaderboard = l;
	}

	@Override
	public void addButtons(Panel panel)
	{
		int i = 0;
		rankSize = 0;
		usernameSize = 0;
		valueSize = 0;

		for (LeaderboardValue value : leaderboard)
		{
			value.rank = ++i;
			panel.add(new LeaderboardEntry(panel, value));
		}
	}

	@Override
	public String getFilterText(Widget widget)
	{
		return ((LeaderboardEntry) widget).value.username.toLowerCase();
	}
}