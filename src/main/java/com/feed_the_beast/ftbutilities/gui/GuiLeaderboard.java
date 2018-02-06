package com.feed_the_beast.ftbutilities.gui;

import com.feed_the_beast.ftblib.lib.gui.GuiBase;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiButtonListBase;
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
public class GuiLeaderboard extends GuiButtonListBase
{
	private final List<LeaderboardValue> leaderboard;
	private int rankSize, usernameSize, valueSize;

	private class LeaderboardEntry extends Widget
	{
		private final LeaderboardValue value;
		private final String rank;

		public LeaderboardEntry(GuiBase g, LeaderboardValue v)
		{
			super(g);
			value = v;
			rank = value.color + "#" + StringUtils.add0s(v.rank, leaderboard.size());

			rankSize = Math.max(rankSize, gui.getStringWidth(rank) + 4);
			usernameSize = Math.max(usernameSize, gui.getStringWidth(v.username) + 8);
			valueSize = Math.max(valueSize, gui.getStringWidth(value.value.getFormattedText()) + 8);

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
		setTitle(FTBULang.LEADERBOARDS.translate() + " > " + c.getFormattedText());
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
			panel.add(new LeaderboardEntry(this, value));
		}
	}
}