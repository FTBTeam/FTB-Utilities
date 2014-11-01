package latmod.core.recipes;
import net.minecraft.item.*;

public interface IStackArray
{
	public boolean matches(ItemStack[] ai);
	public StackEntry[] getItems();
}