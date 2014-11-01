package latmod.core.item;
import net.minecraft.item.Item;

/** Used by tools & items that extends vanilla classes */
public interface IItemLM
{
	public Item getItem();
	public String getItemID();
	public void onPostLoaded();
	public void loadRecipes();
}