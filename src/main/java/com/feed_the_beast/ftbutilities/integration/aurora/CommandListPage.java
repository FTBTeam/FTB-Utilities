package com.feed_the_beast.ftbutilities.integration.aurora;

import com.feed_the_beast.ftbutilities.ranks.CommandOverride;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import dev.latvian.mods.aurora.page.HTTPWebPage;
import dev.latvian.mods.aurora.tag.Style;
import dev.latvian.mods.aurora.tag.Tag;
import net.minecraft.server.MinecraftServer;

/**
 * @author LatvianModder
 */
public class CommandListPage extends HTTPWebPage
{
	private final MinecraftServer server;

	public CommandListPage(MinecraftServer s)
	{
		server = s;
	}

	@Override
	public String getTitle()
	{
		return "FTB Utilities";
	}

	@Override
	public String getDescription()
	{
		return "Command List";
	}

	@Override
	public String getIcon()
	{
		return "https://i.imgur.com/aIuCGYZ.png";
	}

	@Override
	public void head(Tag head)
	{
		super.head(head);
		Style s = head.style();
		s.add("p").set("margin", "0");
	}

	@Override
	public void body(Tag body)
	{
		body.h1("Command List");

		Tag nodeTable = body.table();
		Tag firstRow = nodeTable.tr();
		firstRow.th().text("Available command nodes");
		firstRow.th().text("Usage");

		for (CommandOverride c : Ranks.INSTANCE.commands.values())
		{
			Tag row = nodeTable.tr();
			row.td().paired("code", c.node.toString());
			Tag n = row.td();
			boolean first = true;

			for (String s : Tag.fixHTML(c.usage.getUnformattedText()).split(" OR "))
			{
				if (first)
				{
					first = false;
				}
				else
				{
					n.br();
				}

				n.text(s);
			}
		}
	}
}