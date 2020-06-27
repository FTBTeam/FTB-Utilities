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
import com.feed_the_beast.ftbutilities.FTBUtilitiesCommon;
import com.feed_the_beast.ftbutilities.data.NodeEntry;
import dev.latvian.mods.aurora.page.HTTPWebPage;
import dev.latvian.mods.aurora.tag.Style;
import dev.latvian.mods.aurora.tag.Tag;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.server.permission.DefaultPermissionHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
		Style s = head.style();
		s.add("p").set("margin", "0");
		s.add("code.variants:hover").set("cursor", "default").set("text-decoration", "underline dotted");
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
		body.h1("Permission List");

		List<NodeEntry> allNodes = new ArrayList<>(FTBUtilitiesCommon.CUSTOM_PERM_PREFIX_REGISTRY);

		for (String s : PermissionAPI.getPermissionHandler().getRegisteredNodes())
		{
			DefaultPermissionLevel level = DefaultPermissionHandler.INSTANCE.getDefaultPermissionLevel(s);
			String desc = PermissionAPI.getPermissionHandler().getNodeDescription(s);

			boolean printNode = true;

			for (NodeEntry cprefix : FTBUtilitiesCommon.CUSTOM_PERM_PREFIX_REGISTRY)
			{
				if (s.startsWith(cprefix.getNode()))
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
				allNodes.add(new NodeEntry(s, level, desc));
			}
		}

		for (RankConfigValueInfo info : RankConfigAPI.getHandler().getRegisteredConfigs())
		{
			String desc = new TextComponentTranslation("permission." + info.node).getUnformattedText();
			allNodes.add(new NodeEntry(info.node, info.defaultValue, info.defaultOPValue, desc.equals(info.node) ? "" : desc, null));
		}

		allNodes.sort(null);

		Tag nodeTable = body.table();
		Tag firstRow = nodeTable.tr();
		firstRow.th().text("Node");
		firstRow.th().text("Type");
		firstRow.th().text("Player");
		firstRow.th().text("OP");

		List<List<String>> export = new ArrayList<>();
		export.add(Arrays.asList("Node", "Type", "Player default", "OP default", "Variants"));
		export.add(Arrays.asList("", "", "", "", ""));

		for (NodeEntry entry : allNodes)
		{
			Tag row = nodeTable.tr();
			Tag n = row.td();
			n.paired("code", entry.getNode());

			if (!entry.desc.isEmpty())
			{
				n.text(" ");
				n.icon("info").title(Arrays.asList(entry.desc.split("\n")));
			}

			List<String> variants = new ArrayList<>();

			if (entry.player instanceof ConfigBoolean)
			{
				variants.add("Variants:");
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
				variants.add(0, "Variants:");
			}

			if (variants.isEmpty())
			{
				row.td().paired("code", entry.player.getId());
			}
			else
			{
				row.td().paired("code", entry.player.getId()).addClass("variants").title(variants);
			}

			String playerText = fixHTML(entry.player.getStringForGUI().getUnformattedText());
			String opText = fixHTML(entry.op.getStringForGUI().getUnformattedText());

			if (playerText.equals(opText))
			{
				row.td().addClass("center-text").attr("colspan", "2").span("", classOf(entry.player)).paired("code", playerText);
			}
			else
			{
				row.td().addClass("center-text").span("", classOf(entry.player)).paired("code", playerText);
				row.td().addClass("center-text").span("", classOf(entry.op)).paired("code", opText);
			}

			List<String> variants2 = new ArrayList<>(variants);

			if (variants2.get(0).equals("Variants:"))
			{
				variants2.remove(0);
			}

			export.add(Arrays.asList(entry.getNode(), entry.player.getId(), entry.player.getStringForGUI().getUnformattedText(), entry.op.getStringForGUI().getUnformattedText(), variants2.toString()));
		}

		try
		{
			List<String> export2 = new ArrayList<>();
			StringBuilder builder = new StringBuilder();

			int[] maxLength = new int[5];

			for (List<String> l : export)
			{
				for (int i = 0; i < maxLength.length; i++)
				{
					maxLength[i] = Math.max(maxLength[i], l.get(i).length());
				}
			}

			for (List<String> l : export)
			{
				builder.setLength(0);

				for (int i = 0; i < maxLength.length; i++)
				{
					if (i > 0)
					{
						builder.append(" | ");
					}

					fillWhitespace(builder, l.get(i), maxLength[i]);
				}

				export2.add(builder.toString());
			}

			Files.write(Paths.get("ftb-utilities-permissions.txt"), export2);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void fillWhitespace(StringBuilder builder, String s, int l)
	{
		int sl = s.length();
		int m = Math.max(sl, l);

		for (int i = 0; i < m; i++)
		{
			if (i >= sl)
			{
				builder.append(' ');
			}
			else
			{
				builder.append(s.charAt(i));
			}
		}
	}
}