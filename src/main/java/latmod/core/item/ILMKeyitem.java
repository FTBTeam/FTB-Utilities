package latmod.core.item;

import latmod.core.FastList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface ILMKeyitem extends IItemLM
{
	public void onLMKeyPressed(ItemStack is, EntityPlayer ep, FastList<String> otherKeys);
}