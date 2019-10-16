package com.feed_the_beast.ftbutilities.integration.aurora;

import com.feed_the_beast.ftbutilities.ranks.CommandOverride;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import dev.latvian.mods.aurora.page.HTTPWebPage;
import dev.latvian.mods.aurora.tag.Tag;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

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
		List<String> style = new ArrayList<>();
		style.add("p{margin:0;}");
		head.paired("style", String.join("\r\n", style));
	}

	private String fixHTML(String string)
	{
		return string.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}

	@Override
	public void body(Tag body)
	{
		Tag nodeTable = body.table();
		Tag firstRow = nodeTable.tr();
		firstRow.th().text("Available command nodes");
		firstRow.th().text("Usage");

		for (CommandOverride c : Ranks.INSTANCE.commands.values())
		{
			Tag row = nodeTable.tr();
			row.td().paired("code", c.node.toString());
			row.td().text(fixHTML(c.usage.getUnformattedText()).replace(" OR ", "<br>"));
		}
	}
}