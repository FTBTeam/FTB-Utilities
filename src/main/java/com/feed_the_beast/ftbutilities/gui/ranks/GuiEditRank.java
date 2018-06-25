package com.feed_the_beast.ftbutilities.gui.ranks;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.GuiIcons;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.SimpleTextButton;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiButtonListBase;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.resources.I18n;

/**
 * @author LatvianModder
 */
public class GuiEditRank extends GuiButtonListBase
{
	public final GuiRanks guiRanks;
	public final GuiRanks.RankGuiInst rank;

	public GuiEditRank(GuiRanks g, GuiRanks.RankGuiInst r)
	{
		setTitle(StringUtils.firstUppercase(r.getName()));
		guiRanks = g;
		rank = r;
	}

	@Override
	public void addButtons(Panel panel)
	{
		panel.add(new SimpleTextButton(panel, I18n.format("selectServer.delete"), GuiIcons.REMOVE)
		{
			@Override
			public void onClicked(MouseButton button)
			{
				GuiHelper.playClickSound();
				ClientUtils.MC.displayGuiScreen(new GuiYesNo((result, id) -> {
					if (result)
					{
						guiRanks.ranks.remove(rank.getName());

						for (GuiRanks.RankGuiInst r : guiRanks.ranks.values())
						{
							if (r.parent == rank)
							{
								r.parent = null;
							}
						}
					}

					guiRanks.openGui();
				}, "", "", 0));
			}
		});
	}
}