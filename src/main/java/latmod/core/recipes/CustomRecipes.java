package latmod.core.recipes;
import net.minecraft.item.*;
import latmod.core.util.*;

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
		
		for(int i = 0; i < recipes.size(); i++)
		{
			if(recipes.keys.get(i).matches(ai))
				return recipes.values.get(i);
		}
		
		return null;
	}
}