package latmod.ftbu.recipes;

import net.minecraft.item.ItemStack;

public interface IStackArray
{
	public boolean matches(ItemStack[] ai);

	public IStackArray[] getItems();
}