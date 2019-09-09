package com.feed_the_beast.ftbutilities.gui.ranks;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.gui.Button;
import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.Theme;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.gui.WidgetType;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiButtonListBase;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import net.minecraft.client.resources.I18n;

import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class GuiPlayerRanks extends GuiButtonListBase
{
	private final GuiRanks guiRanks;
	private int usernameSize, valueSize;

	private class PlayerEntry extends Button implements Comparable<PlayerEntry>
	{
		private final String username;
		private String rank;

		public PlayerEntry(Panel panel, String u, String r)
		{
			super(panel);
			username = u;
			rank = StringUtils.firstUppercase(r.isEmpty() ? "none" : r);

			Theme theme = getTheme();
			usernameSize = Math.max(usernameSize, theme.getStringWidth(username) + 8);
			valueSize = Math.max(valueSize, theme.getStringWidth(rank) + 8);

			setSize(usernameSize + valueSize, 14);
		}

		@Override
		public void onClicked(MouseButton button)
		{
			GuiHelper.playClickSound();
			new GuiSelectRank(guiRanks, rank -> true, rank -> {

				String s = rank == null ? "" : rank.getId();

				if (!s.equals(guiRanks.playerRanks.put(username, s)))
				{
					ClientUtils.execClientCommand("/ranks set " + username + " " + (rank == null ? "none" : rank.getId()));
					getGui().refreshWidgets();
				}

			}).openGui();
		}

		@Override
		public void addMouseOverText(List<String> list)
		{
		}

		@Override
		public void draw(Theme theme, int x, int y, int w, int h)
		{
			WidgetType type = WidgetType.mouseOver(isMouseOver());
			int textY = y + (h - theme.getFontHeight() + 1) / 2;

			theme.drawButton(x, y, usernameSize, h, type);
			theme.drawString(username, x + 4, textY, Theme.SHADOW);

			theme.drawButton(x + usernameSize, y, valueSize, h, type);
			theme.drawString(rank, x + usernameSize + 4, textY, Theme.SHADOW);
		}

		@Override
		public int compareTo(PlayerEntry o)
		{
			return username.compareToIgnoreCase(o.username);
		}
	}

	public GuiPlayerRanks(GuiRanks g)
	{
		guiRanks = g;
		setTitle(I18n.format("admin_panel.ftbutilities.ranks.player_ranks"));
		setHasSearchBox(true);
	}

	@Override
	public void addButtons(Panel panel)
	{
		usernameSize = 0;
		valueSize = 0;

		for (Map.Entry<String, String> entry : guiRanks.playerRanks.entrySet())
		{
			panel.add(new PlayerEntry(panel, entry.getKey(), entry.getValue()));
		}

		panel.widgets.sort(null);
	}

	@Override
	public String getFilterText(Widget widget)
	{
		return ((PlayerEntry) widget).username.toLowerCase();
	}
}