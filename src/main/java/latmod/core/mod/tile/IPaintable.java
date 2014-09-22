package latmod.core.mod.tile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;

public interface IPaintable extends ITileInterface
{
	public boolean setPaint(EntityPlayer ep, MovingObjectPosition mop, ItemStack paint);
}