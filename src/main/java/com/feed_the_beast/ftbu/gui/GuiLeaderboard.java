package com.feed_the_beast.ftbu.gui;

import com.feed_the_beast.ftbl.lib.gui.Panel;
import com.feed_the_beast.ftbl.lib.gui.SimpleTextButton;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiButtonListBase;
import com.feed_the_beast.ftbl.lib.icon.Icon;
import com.feed_the_beast.ftbl.lib.util.misc.MouseButton;
import net.minecraft.util.text.ITextComponent;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class GuiLeaderboard extends GuiButtonListBase
{
	private final Map<String, ITextComponent> leaderboard;

	public GuiLeaderboard(ITextComponent c, Map<String, ITextComponent> l)
	{
		leaderboard = l;
		setTitle(c.getFormattedText());
	}

	@Override
	public void addButtons(Panel panel)
	{
		for (Map.Entry<String, ITextComponent> entry : leaderboard.entrySet())
		{
			panel.add(new SimpleTextButton(this, 0, 0, entry.getKey() + ": " + entry.getValue().getFormattedText(), Icon.EMPTY)
			{
				@Override
				public void onClicked(MouseButton button)
				{
				}
			});
		}
	}
}