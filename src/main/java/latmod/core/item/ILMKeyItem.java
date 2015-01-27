package latmod.core.item;

import latmod.core.FastList;
import latmod.core.event.Key;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface ILMKeyItem extends IItemLM
{
	public void onLMKeyPressed(ItemStack is, EntityPlayer ep, FastList<Key> otherKeys);
}