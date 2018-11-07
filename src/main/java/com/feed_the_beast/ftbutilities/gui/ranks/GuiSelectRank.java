package com.feed_the_beast.ftbutilities.gui.ranks;

import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.SimpleTextButton;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiButtonListBase;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import net.minecraft.client.resources.I18n;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class GuiSelectRank extends GuiButtonListBase
{
	private final GuiRanks guiRanks;
	private final Predicate<GuiRanks.RankGuiInst> filter;
	private final Consumer<GuiRanks.RankGuiInst> callback;

	public GuiSelectRank(GuiRanks g, Predicate<GuiRanks.RankGuiInst> p, Consumer<GuiRanks.RankGuiInst> c)
	{
		guiRanks = g;
		filter = p;
		callback = c;
		setTitle(I18n.format("admin_panel.ftbutilities.ranks.select_rank"));
		setHasSearchBox(true);
	}

	@Override
	public void addButtons(Panel panel)
	{
		if (filter.test(null))
		{
			panel.add(new SimpleTextButton(panel, "None", Icon.EMPTY)
			{
				@Override
				public void onClicked(MouseButton button)
				{
					getGui().closeGui();
					callback.accept(null);
				}
			});
		}

		for (GuiRanks.RankGuiInst inst : guiRanks.ranks.values())
		{
			if (filter.test(inst))
			{
				panel.add(new SimpleTextButton(panel, StringUtils.firstUppercase(inst.getID()), Icon.EMPTY)
				{
					@Override
					public void onClicked(MouseButton button)
					{
						getGui().closeGui();
						callback.accept(inst);
					}
				});
			}
		}
	}
}