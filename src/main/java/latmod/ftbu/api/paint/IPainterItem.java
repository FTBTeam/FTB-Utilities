package latmod.ftbu.api.paint;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IPainterItem
{
	public ItemStack getPaintItem(ItemStack is);
	public boolean canPaintBlock(ItemStack is);
	public void damagePainter(ItemStack is, EntityPlayer ep);
}