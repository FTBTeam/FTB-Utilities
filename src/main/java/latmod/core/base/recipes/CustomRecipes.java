package latmod.core.base.recipes;
import latmod.core.*;

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
	
	public Output getResult(IStackArray is)
	{ return recipes.get(is); }
}