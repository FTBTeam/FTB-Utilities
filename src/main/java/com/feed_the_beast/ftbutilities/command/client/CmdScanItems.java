package com.feed_the_beast.ftbutilities.command.client;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.item.ItemEntry;
import com.feed_the_beast.ftblib.lib.util.FileUtils;
import com.feed_the_beast.ftbutilities.client.FTBUtilitiesClientConfig;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.oredict.OreDictionary;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class CmdScanItems extends CmdBase
{
	public CmdScanItems()
	{
		super("scan_items", Level.ALL);
	}

	public static boolean filter(ItemEntry entry)
	{
		String s = entry.item.getRegistryName().toString();

		for (String s1 : FTBUtilitiesClientConfig.general.scan_items_whitelist)
		{
			if (s.startsWith(s1))
			{
				return true;
			}
		}

		for (String s1 : FTBUtilitiesClientConfig.general.scan_items_blacklist)
		{
			if (s.startsWith(s1))
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, final String[] args) throws CommandException
	{
		sender.sendMessage(new TextComponentString("Exporting debug files..."));

		new Thread(() -> {
			List<String> list = new ArrayList<>();
			for (Fluid fluid : FluidRegistry.getRegisteredFluids().values())
			{
				FluidStack stack = new FluidStack(fluid, 1000);
				ModContainer mod = Loader.instance().getIndexedModList().get(fluid.getStill(stack).getNamespace());
				list.add(fluid.getName() + " ;; " + stack.getLocalizedName() + " ;; " + (mod == null ? "Unknown" : mod.getName()));
			}
			saveFile(list, "fluids.txt");
		}).start();

		new Thread(() -> {
			List<String> list = new ArrayList<>();

			StringBuilder builder = new StringBuilder();
			NonNullList<ItemStack> stacks = NonNullList.create();

			LinkedHashMap<String, LinkedHashSet<ItemEntry>> itemToDisplayNameMap = new LinkedHashMap<>();

			for (Item item : Item.REGISTRY)
			{
				stacks.clear();
				item.getSubItems(CreativeTabs.SEARCH, stacks);

				for (ItemStack stack : stacks)
				{
					ItemEntry itemEntry = ItemEntry.get(stack);

					if (!stack.isEmpty() && filter(itemEntry))
					{
						String displayName = stack.getDisplayName();
						LinkedHashSet<ItemEntry> set = itemToDisplayNameMap.get(displayName);

						if (set == null)
						{
							set = new LinkedHashSet<>();
							itemToDisplayNameMap.put(displayName, set);
						}

						set.add(itemEntry);
					}
				}
			}

			for (Map.Entry<String, LinkedHashSet<ItemEntry>> entry : itemToDisplayNameMap.entrySet())
			{
				if (entry.getValue().size() > 1)
				{
					list.add(entry.getKey());

					for (ItemEntry itemEntry : entry.getValue())
					{
						builder.append('-');
						builder.append(' ');
						itemEntry.toString(builder);
						list.add(builder.toString());
						builder.setLength(0);
					}

					list.add("");
				}
			}

			saveFile(list, "dupe_display_names.txt");
		}).start();

		new Thread(() -> {
			List<String> list = new ArrayList<>();
			StringBuilder builder = new StringBuilder();

			for (String ore : OreDictionary.getOreNames())
			{
				NonNullList<ItemStack> oreItems = OreDictionary.getOres(ore);

				if (oreItems.size() > 1)
				{
					boolean added = false;

					for (ItemStack stack : oreItems)
					{
						ItemEntry itemEntry = ItemEntry.get(stack);

						if (!itemEntry.isEmpty() && filter(itemEntry))
						{
							if (!added)
							{
								added = true;
								list.add(ore);
							}

							builder.append('-');
							builder.append(' ');
							itemEntry.toString(builder);
							list.add(builder.toString());
							builder.setLength(0);
						}
					}

					list.add("");
				}
			}

			saveFile(list, "ore_name_dupes.txt");
		}).start();
	}

	private static void saveFile(List<String> list, String file)
	{
		File f = new File(ClientUtils.MC.gameDir, "local/client/ftbjanitor/" + file);
		FileUtils.saveSafe(f, list);
		ITextComponent component = new TextComponentString(file + " saved! Click here to open.");

		try
		{
			component.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, f.getCanonicalFile().getAbsolutePath()));
		}
		catch (Exception ex)
		{
			component.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, f.getAbsolutePath()));
		}

		ClientUtils.MC.player.sendMessage(component);
	}
}