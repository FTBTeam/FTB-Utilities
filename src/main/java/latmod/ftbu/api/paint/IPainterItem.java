package latmod.ftbu.api.paint;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IPainterItem
{
	ItemStack getPaintItem(ItemStack is);
	boolean canPaintBlock(ItemStack is);
	void damagePainter(ItemStack is, EntityPlayer ep);
}