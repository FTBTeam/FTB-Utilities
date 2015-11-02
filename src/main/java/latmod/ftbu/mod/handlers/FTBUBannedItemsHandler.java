package latmod.ftbu.mod.handlers;

import ftb.lib.FTBLib;
import latmod.lib.FastList;
import net.minecraft.item.ItemStack;

public class FTBUBannedItemsHandler
{
	public static void removeItems(FastList<ItemStack> is)
	{
		FTBLib.logger.info("BannedItems: Removing " + is);
	}
}