package com.feed_the_beast.ftbutilities.integration.aurora;

import com.feed_the_beast.ftblib.lib.config.ConfigBoolean;
import com.feed_the_beast.ftblib.lib.config.ConfigDouble;
import com.feed_the_beast.ftblib.lib.config.ConfigInt;
import com.feed_the_beast.ftblib.lib.config.ConfigTimer;
import com.feed_the_beast.ftblib.lib.config.ConfigValue;
import com.feed_the_beast.ftblib.lib.config.RankConfigAPI;
import com.feed_the_beast.ftblib.lib.config.RankConfigValueInfo;
import com.feed_the_beast.ftblib.lib.math.Ticks;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.feed_the_beast.ftbutilities.FTBUtilitiesCommon;
import com.feed_the_beast.ftbutilities.data.NodeEntry;
import dev.latvian.mods.aurora.page.HTTPWebPage;
import dev.latvian.mods.aurora.tag.Tag;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.server.permission.DefaultPermissionHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class PermissionListPage extends HTTPWebPage
{
	public PermissionListPage()
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
		return "Permission List";
	}

	@Override
	public String getIcon()
	{
		return "https://i.imgur.com/m8KTq4s.png";
	}

	@Override
	public void head(Tag head)
	{
		super.head(head);
		List<String> style = new ArrayList<>();
		style.add("p{margin:0;}");
		head.paired("style", String.join("\r\n", style));
	}

	private String classOf(ConfigValue value)
	{
		if (value instanceof ConfigBoolean)
		{
			return value.getBoolean() ? "yes" : "no";
		}
		else
		{
			return "other";
		}
	}

	private String fixHTML(String string)
	{
		return string.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}

	@Override
	public void body(Tag body)
	{
		List<NodeEntry> allNodes = new ArrayList<>(FTBUtilitiesCommon.CUSTOM_PERM_PREFIX_REGISTRY);

		for (String s : PermissionAPI.getPermissionHandler().getRegisteredNodes())
		{
			DefaultPermissionLevel level = DefaultPermissionHandler.INSTANCE.getDefaultPermissionLevel(s);
			String desc = PermissionAPI.getPermissionHandler().getNodeDescription(s);
			Node node = Node.get(s);

			boolean printNode = true;

			for (NodeEntry cprefix : FTBUtilitiesCommon.CUSTOM_PERM_PREFIX_REGISTRY)
			{
				if (cprefix.getNode().matches(node))
				{
					if (cprefix.level != null && level == cprefix.level && desc.isEmpty())
					{
						printNode = false;
					}

					break;
				}
			}

			if (printNode)
			{
				allNodes.add(new NodeEntry(node, level, desc));
			}
		}

		for (RankConfigValueInfo info : RankConfigAPI.getHandler().getRegisteredConfigs())
		{
			String desc = new TextComponentTranslation("permission." + info.node).getUnformattedText();
			allNodes.add(new NodeEntry(info.node, info.defaultValue, info.defaultOPValue, desc.equals(info.node.toString()) ? "" : desc, null));
		}

		allNodes.sort(null);

		Tag nodeTable = body.table();
		Tag firstRow = nodeTable.tr();
		firstRow.th().text("Node");
		firstRow.th().text("Type");
		firstRow.th().text("Player");
		firstRow.th().text("OP");
		firstRow.th().text("Info (Mouse over for variants)");

		for (NodeEntry entry : allNodes)
		{
			Tag row = nodeTable.tr();
			row.td().paired("code", entry.getNode().toString());
			row.td().paired("code", entry.player.getId());

			String playerText = fixHTML(entry.player.getStringForGUI().getUnformattedText());
			String opText = fixHTML(entry.op.getStringForGUI().getUnformattedText());

			if (playerText.equals(opText))
			{
				row.td().attr("colspan", "2").span("", classOf(entry.player)).paired("code", playerText);
			}
			else
			{
				row.td().span("", classOf(entry.player)).paired("code", playerText);
				row.td().span("", classOf(entry.op)).paired("code", opText);
			}

			List<String> variants = new ArrayList<>();

			if (entry.player instanceof ConfigBoolean)
			{
				variants.add("true");
				variants.add("false");
			}
			else if (entry.player instanceof ConfigInt)
			{
				int min = ((ConfigInt) entry.player).getMin();
				int max = ((ConfigInt) entry.player).getMax();
				variants.add(String.format("%s to %s", min == Integer.MIN_VALUE ? "-&infin;" : String.valueOf(min), max == Integer.MAX_VALUE ? "&infin;" : String.valueOf(max)));
			}
			else if (entry.player instanceof ConfigDouble)
			{
				double min = ((ConfigDouble) entry.player).getMin();
				double max = ((ConfigDouble) entry.player).getMax();

				variants.add(String.format("%s to %s", min == Double.NEGATIVE_INFINITY ? "-&infin;" : StringUtils.formatDouble(min), max == Double.POSITIVE_INFINITY ? "&infin;" : StringUtils.formatDouble(max)));
			}
			else if (entry.player instanceof ConfigTimer)
			{
				Ticks max = ((ConfigTimer) entry.player).getMax();
				variants.add(String.format("0s to %s", !max.hasTicks() ? "&infin;" : max.toString()));
			}
			else
			{
				variants = new ArrayList<>(entry.player.getVariants());
				variants.sort(StringUtils.IGNORE_CASE_COMPARATOR);
			}


			Tag info = row.td();

			if (!variants.isEmpty())
			{
				StringBuilder sb = new StringBuilder();

				for (int i = 0; i < variants.size(); i++)
				{
					String s = StringUtils.unformatted(variants.get(i));

					if (i > 0)
					{
						sb.append('\n');
					}

					sb.append(s);
					sb.append(' ');
				}

				info.title(sb.toString());
			}

			if (!entry.desc.isEmpty())
			{
				for (String s1 : entry.desc.split("\n"))
				{
					info.p(s1);
				}
			}
		}
	}
}