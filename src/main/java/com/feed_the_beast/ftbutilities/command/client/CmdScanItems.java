package com.feed_the_beast.ftbutilities.command.client;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.item.ItemEntry;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.feed_the_beast.ftblib.lib.util.FileUtils;
import com.feed_the_beast.ftbutilities.client.FTBUtilitiesClientConfig;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;
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
		super("scan_items", Level.OP);
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
		new Thread("Scanning Items")
		{
			@Override
			public void run()
			{
				boolean md = args.length > 0 && args[0].equals("md");

				List<String> list = new ArrayList<>();
				list.add("# Item format: mod:item metadata {nbt}");
				list.add("");
				list.add("## Items with duplicate display names:");

				list.add("");

				StringBuilder builder = new StringBuilder();
				NonNullList<ItemStack> stacks = NonNullList.create();

				LinkedHashMap<String, LinkedHashSet<ItemEntry>> itemToDisplayNameMap = new LinkedHashMap<>();
				LinkedHashMap<ItemEntry, Boolean> hasRecipe = new LinkedHashMap<>();

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
							hasRecipe.put(itemEntry, false);

							ItemEntry itemEntry1 = ItemEntry.get(FurnaceRecipes.instance().getSmeltingResult(stack));

							if (itemEntry.equalsEntry(itemEntry1))
							{
								hasRecipe.put(itemEntry, true);
							}
							else
							{
								for (IRecipe recipe : CraftingManager.REGISTRY)
								{
									itemEntry1 = ItemEntry.get(recipe.getRecipeOutput());

									if (itemEntry.equalsEntry(itemEntry1))
									{
										hasRecipe.put(itemEntry, true);
										break;
									}
								}
							}
						}
					}
				}

				for (Map.Entry<String, LinkedHashSet<ItemEntry>> entry : itemToDisplayNameMap.entrySet())
				{
					if (entry.getValue().size() > 1)
					{
						list.add(entry.getKey());

						if (md)
						{
							list.add("");
						}

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

				list.add("");
				list.add("");
				list.add("## Ore Dictionary names with more than one item:");
				list.add("");

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

									if (md)
									{
										list.add("");
									}
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

				list.add("");
				list.add("");
				list.add("## Items without crafting table or furnace recipe (can be inaccurate):");
				list.add("");

				for (Map.Entry<ItemEntry, Boolean> entry : hasRecipe.entrySet())
				{
					if (!entry.getValue())
					{
						builder.append('-');
						builder.append(' ');
						entry.getKey().toString(builder);
						list.add(builder.toString());
						builder.setLength(0);
					}
				}

				FileUtils.saveSafe(new File(CommonUtils.folderLocal, "client/scanneditems." + (md ? "md" : "txt")), list);
				ClientUtils.MC.player.sendStatusMessage(new TextComponentString("Duplicate items have been exported in ./local/client/scanneditems." + (md ? "md" : "txt") + "!"), false);
			}
		}.start();
	}
}