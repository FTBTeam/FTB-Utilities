package com.feed_the_beast.ftbutilities.integration.aurora;

import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import dev.latvian.mods.aurora.page.HTTPWebPage;
import dev.latvian.mods.aurora.tag.Style;
import dev.latvian.mods.aurora.tag.Tag;

/**
 * @author LatvianModder
 */
public class RankPage extends HTTPWebPage
{
	public RankPage()
	{
	}

	@Override
	public String getTitle()
	{
		return "FTB Utilities";
	}

	@Override
	public String getDescription()
	{
		return "Ranks";
	}

	@Override
	public String getIcon()
	{
		return "https://i.imgur.com/3o2sHns.png";
	}

	@Override
	public boolean getRequiresAuth()
	{
		return true;
	}

	@Override
	public void head(Tag head)
	{
		super.head(head);
		Style s = head.style();
		s.add("p").set("margin", "0");
		s.add("th").set("font-weight", "normal");
	}

	@Override
	public void body(Tag body)
	{
		body.h1("Ranks");

		for (Rank rank : Ranks.INSTANCE.ranks.values())
		{
			Tag table = body.table();
			Tag name = table.tr().th().attr("colspan", "2");
			name.span(rank.getId(), "other");

			if (!rank.parent.isNone())
			{
				name.text(" extends ");
				name.span(rank.parent.getId(), "other");
			}

			if (!rank.tags.isEmpty())
			{
				for (String tag : rank.tags)
				{
					name.text(" is ");
					name.span(tag, "other");
				}
			}

			for (Rank.Entry entry : rank.permissions)
			{
				Tag row = table.tr();
				row.td().text(entry.node.toString());

				if (entry.value.equals("true") || entry.value.equals("false"))
				{
					row.td().span(entry.value, entry.value.equals("true") ? "yes" : "no");
				}
				else
				{
					row.td().span(entry.value, "other");
				}
			}

			body.br();
		}
	}
}