package com.feed_the_beast.ftbutilities.gui;

import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.SimpleTextButton;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiButtonListBase;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import com.feed_the_beast.ftbutilities.net.MessageLeaderboard;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class GuiLeaderboardList extends GuiButtonListBase
{
	private final Map<ResourceLocation, ITextComponent> leaderboards;

	public GuiLeaderboardList(Map<ResourceLocation, ITextComponent> l)
	{
		leaderboards = l;
		setTitle(I18n.format("sidebar_button.ftbutilities.leaderboards"));
	}

	@Override
	public void addButtons(Panel panel)
	{
		for (Map.Entry<ResourceLocation, ITextComponent> entry : leaderboards.entrySet())
		{
			panel.add(new SimpleTextButton(panel, entry.getValue().getFormattedText(), Icon.EMPTY)
			{
				@Override
				public void onClicked(MouseButton button)
				{
					GuiHelper.playClickSound();
					new MessageLeaderboard(entry.getKey()).sendToServer();
				}
			});
		}
	}
}