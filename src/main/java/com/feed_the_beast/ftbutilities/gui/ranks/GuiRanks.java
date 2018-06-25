package com.feed_the_beast.ftbutilities.gui.ranks;

import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.GuiIcons;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.SimpleTextButton;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiButtonListBase;
import com.feed_the_beast.ftblib.lib.icon.PlayerHeadIcon;
import com.feed_the_beast.ftblib.lib.util.FinalIDObject;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import net.minecraft.client.resources.I18n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class GuiRanks extends GuiButtonListBase
{
	public static class Tag extends FinalIDObject implements Comparable<Tag>
	{
		public final String displayName;

		private Tag(String tag)
		{
			super(tag);
			displayName = I18n.hasKey("ftbutilities.ranks.tags." + getName()) ? I18n.format("ftbutilities.ranks.tags." + getName()) : getName();
		}

		@Override
		public int compareTo(Tag o)
		{
			return displayName.compareTo(o.displayName);
		}
	}

	public static class RankGuiInst extends FinalIDObject
	{
		public RankGuiInst parent;
		public final HashSet<Tag> tags;
		public final List<Rank.Entry> permissions;

		public RankGuiInst(String id)
		{
			super(id);
			parent = null;
			tags = new HashSet<>();
			permissions = new ArrayList<>();
		}
	}

	public final Map<String, Tag> tags;
	public final Map<String, RankGuiInst> ranks;
	public final Map<String, String> playerRanks;

	public GuiRanks(Collection<RankInst> r, Collection<String> t, Map<String, String> p)
	{
		tags = new LinkedHashMap<>();
		ranks = new LinkedHashMap<>();

		ArrayList<Tag> tagsList = new ArrayList<>();

		for (String tag : t)
		{
			tagsList.add(new Tag(tag));
		}

		tagsList.sort(null);

		for (Tag tag : tagsList)
		{
			tags.put(tag.getName(), tag);
		}

		for (RankInst inst : r)
		{
			RankGuiInst inst1 = new RankGuiInst(inst.getName());

			for (String tag : inst.tags)
			{
				Tag tag1 = tags.get(tag);

				if (tag1 != null)
				{
					inst1.tags.add(tag1);
				}
			}

			inst1.permissions.addAll(inst.permissions);

			ranks.put(inst1.getName(), inst1);
		}

		for (RankInst inst : r)
		{
			ranks.get(inst.getName()).parent = ranks.get(inst.parent);
		}

		playerRanks = p;
	}

	@Override
	public void addButtons(Panel panel)
	{
		panel.add(new SimpleTextButton(panel, I18n.format("admin_panel.ftbutilities.ranks.player_ranks"), new PlayerHeadIcon(null).withBorder(4))
		{
			@Override
			public void onClicked(MouseButton button)
			{
				GuiHelper.playClickSound();
				new GuiPlayerRanks(GuiRanks.this).openGui();
			}
		});

		panel.add(new SimpleTextButton(panel, I18n.format("gui.add"), GuiIcons.ADD)
		{
			@Override
			public void onClicked(MouseButton button)
			{
				GuiHelper.playClickSound();
			}
		});

		for (RankGuiInst inst : ranks.values())
		{
			panel.add(new SimpleTextButton(panel, StringUtils.firstUppercase(inst.getName()), GuiIcons.SETTINGS)
			{
				@Override
				public void onClicked(MouseButton button)
				{
					GuiHelper.playClickSound();
					new GuiEditRank(GuiRanks.this, inst).openGui();
				}
			});
		}
	}
}