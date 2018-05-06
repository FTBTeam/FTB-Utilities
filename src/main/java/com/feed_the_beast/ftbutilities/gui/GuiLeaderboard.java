package com.feed_the_beast.ftbutilities.gui;

import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.gui.WidgetType;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiButtonListBase;
import com.feed_the_beast.ftblib.lib.icon.Icon;
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

			rankSize = Math.max(rankSize, getStringWidth(rank) + 4);
			usernameSize = Math.max(usernameSize, getStringWidth(v.username) + 8);
			valueSize = Math.max(valueSize, getStringWidth(value.value.getFormattedText()) + 8);

			setSize(rankSize + usernameSize + valueSize, 14);
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

			Icon widget = value.color == TextFormatting.DARK_GRAY ? getTheme().getButton(WidgetType.DISABLED) : getTheme().getButton(WidgetType.mouseOver(isMouseOver()));
			int textY = ay + (height - getFontHeight() + 1) / 2;
			widget.draw(ax, ay, rankSize, height);
			drawString(rank, ax + 2, textY, SHADOW);

			widget.draw(ax + rankSize, ay, usernameSize, height);
			drawString(value.color + value.username, ax + 4 + rankSize, textY, SHADOW);

			widget.draw(ax + rankSize + usernameSize, ay, valueSize, height);
			String formattedText = value.value.getFormattedText();
			drawString(value.color + formattedText, ax + rankSize + usernameSize + valueSize - getStringWidth(formattedText) - 4, textY, SHADOW);
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
		return ((LeaderboardEntry) widget).value.username;
	}
}