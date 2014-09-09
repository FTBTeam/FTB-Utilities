package latmod.core.mod.tile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IPaintable extends ITileInterface
{
	public ItemStack getPaint(int side);
	public boolean setPaint(ItemStack is, EntityPlayer ep, int side);
}