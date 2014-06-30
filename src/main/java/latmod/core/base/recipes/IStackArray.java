package latmod.core.base.recipes;
import net.minecraft.item.*;

public interface IStackArray
{
	public boolean equalsArray(ItemStack[] ai);
	public StackEntry[] getItems();
}