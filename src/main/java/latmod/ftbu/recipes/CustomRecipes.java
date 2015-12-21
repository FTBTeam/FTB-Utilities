package latmod.ftbu.recipes;
import java.util.Map;

import latmod.lib.FastMap;
import net.minecraft.item.ItemStack;

public class CustomRecipes<Output>
{
	public final LMRecipes parent;
	protected FastMap<IStackArray, Output> recipes;
	
	public CustomRecipes(LMRecipes r)
	{
		parent = r;
		recipes = new FastMap<IStackArray, Output>();
	}
	
	public void clearMap()
	{ recipes.clear(); }
	
	public void addRecipe(Output out, IStackArray in)
	{ recipes.put(in, out); }
	
	public Output getResult(ItemStack[] ai)
	{
		if(ai == null || ai.length == 0) return null;
		
		for(Map.Entry<IStackArray, Output> e : recipes.entrySet())
		{
			if(e.getKey().matches(ai))
				return e.getValue();
		}
		
		return null;
	}
}