package com.feed_the_beast.ftbu.gui;

import com.feed_the_beast.ftbl.lib.client.ClientUtils;
import com.feed_the_beast.ftbl.lib.gui.Button;
import com.feed_the_beast.ftbl.lib.gui.GuiBase;
import com.feed_the_beast.ftbl.lib.gui.Panel;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiButtonListBase;
import com.feed_the_beast.ftbl.lib.util.misc.MouseButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author LatvianModder
 */
public class GuiViewCrashList extends GuiButtonListBase
{
	private static class ButtonFile extends Button
	{
		public ButtonFile(GuiBase gui, String title)
		{
			super(gui, 0, 0, gui.getStringWidth(title) + 8, 20, title);
		}

		@Override
		public void onClicked(MouseButton button)
		{
			ClientUtils.execClientCommand("/ftb view_crash " + getTitle());
		}

		@Override
		public void addMouseOverText(List<String> list)
		{
		}

		@Override
		public void renderWidget()
		{
			getIcon().draw(this);
			gui.drawString(getTitle(), getAX() + 4, getAY() + (height - gui.getFontHeight()) / 2, SHADOW);
		}
	}

	private final List<String> files;

	public GuiViewCrashList(Collection<String> l)
	{
		files = new ArrayList<>(l);
		files.sort(null);
	}

	@Override
	public void addButtons(Panel panel)
	{
		for (String s : files)
		{
			panel.add(new ButtonFile(this, s));
		}
	}
}