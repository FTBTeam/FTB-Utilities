package com.feed_the_beast.ftbutilities.cmd.client;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.item.ItemEntry;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.feed_the_beast.ftblib.lib.util.FileUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemPotion;
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

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		boolean md = args.length > 0 && args[0].equals("md");

		List<String> list = new ArrayList<>();

		if (md)
		{
			list.add("# Item format: mod:item metadata {nbt}");
		}
		else
		{
			list.add("Item format: mod:item metadata {nbt}");
		}

		list.add("");

		if (md)
		{
			list.add("## Items with duplicate display names:");
		}
		else
		{
			list.add("Items with duplicate display names:");
		}

		list.add("");

		StringBuilder builder = new StringBuilder();

		LinkedHashMap<String, LinkedHashSet<ItemEntry>> itemToDisplayNameMap = new LinkedHashMap<>();
		LinkedHashMap<ItemEntry, Boolean> hasRecipe = new LinkedHashMap<>();

		for (Item item : Item.REGISTRY)
		{
			if (item instanceof ItemEnchantedBook || item instanceof ItemArrow || item instanceof ItemPotion)
			{
				continue;
			}

			NonNullList<ItemStack> stacks = NonNullList.create();
			item.getSubItems(CreativeTabs.SEARCH, stacks);

			for (ItemStack stack : stacks)
			{
				if (!stack.isEmpty())
				{
					String displayName = stack.getDisplayName();
					LinkedHashSet<ItemEntry> set = itemToDisplayNameMap.get(displayName);

					if (set == null)
					{
						set = new LinkedHashSet<>();
						itemToDisplayNameMap.put(displayName, set);
					}

					ItemEntry itemEntry = ItemEntry.get(stack);
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

		if (md)
		{
			list.add("## Items with duplicate Ore Dictionary names:");
		}
		else
		{
			list.add("Items with duplicate Ore Dictionary names:");
		}

		list.add("");

		for (String ore : OreDictionary.getOreNames())
		{
			NonNullList<ItemStack> oreItems = OreDictionary.getOres(ore);

			if (oreItems.size() > 1)
			{
				list.add(ore);

				if (md)
				{
					list.add("");
				}

				for (ItemStack stack : oreItems)
				{
					ItemEntry itemEntry = ItemEntry.get(stack);

					if (!itemEntry.isEmpty())
					{
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

		if (md)
		{
			list.add("## Items without recipe:");
		}
		else
		{
			list.add("Items without crafting table or furnace recipe:");
		}

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

		try
		{
			FileUtils.save(new File(CommonUtils.folderLocal, "client/scanneditems." + (md ? "md" : "txt")), list);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		ClientUtils.MC.player.sendStatusMessage(new TextComponentString("Duplicate items have been exported in ./local/client/scanneditems." + (md ? "md" : "txt") + "!"), true);
	}
}