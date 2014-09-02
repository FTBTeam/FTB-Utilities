package latmod.core.mod.recipes;
import java.util.Map;

import latmod.core.LatCoreMC;
import latmod.core.util.*;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.item.crafting.*;
import net.minecraftforge.oredict.*;
import cpw.mods.fml.common.registry.GameRegistry;

public class LMRecipes
{
	public boolean enableOreRecipes = true;
	
	public final boolean storeRecipes;
	public FastList<IRecipe> craftingRecipes;
	public FastMap<ItemStack, ItemStack> furnaceRecipes;
	public FastList<CustomRecipes<?>> customRecipes;
	
	public LMRecipes(boolean b)
	{
		storeRecipes = b;
		craftingRecipes = new FastList<IRecipe>();
		furnaceRecipes = new FastMap<ItemStack, ItemStack>();
		customRecipes = new FastList<CustomRecipes<?>>();
	}
	
	public void addCustomRecipes(CustomRecipes<?> c)
	{ customRecipes.add(c); }
	
	public ItemStack size(ItemStack is, int s)
	{ ItemStack is1 = is.copy(); is1.stackSize = s; return is1; }
	
	@SuppressWarnings("all")
	public void clearRecipes()
	{
		CraftingManager.getInstance().getRecipeList().removeAll(craftingRecipes);
		craftingRecipes.clear();
		
		Map m = FurnaceRecipes.smelting().getSmeltingList();
		for(ItemStack is : furnaceRecipes.keys) m.remove(is);
		furnaceRecipes.clear();
		
		for(CustomRecipes<?> c : customRecipes)
		c.clearMap();
	}
	
	@SuppressWarnings("unchecked")
	public IRecipe addRecipe(IRecipe r)
	{ CraftingManager.getInstance().getRecipeList().add(r); return r; }

	public IRecipe addRecipe(ItemStack out, Object... in)
	{
		IRecipe r;
		
		if(!enableOreRecipes) r = GameRegistry.addShapedRecipe(out, in);
		else r = addRecipe(new ShapedOreRecipe(out, in));
		
		if(storeRecipes) craftingRecipes.add(r);
		
		return r;
	}
	
	public IRecipe addShapelessRecipe(ItemStack out, Object... in)
	{
		IRecipe r;
		
		if(!enableOreRecipes)
		{
			FastList<ItemStack> al = new FastList<ItemStack>();
			
			int i = in.length;
			
			for (int j = 0; j < i; ++j)
			{
				Object o = in[j];
				
				if (o instanceof ItemStack)
				al.add(((ItemStack)o).copy());
				
				else if (o instanceof Item)
				al.add(new ItemStack((Item)o));
				
				else
				{
					if (!(o instanceof Block))
					throw new RuntimeException("Invalid shapeless recipy!");
					al.add(new ItemStack((Block)o));
				}
			}
			
			r = addRecipe(new ShapelessRecipes(out, al));
		}
		else r = addRecipe(new ShapelessOreRecipe(out, in));
		
		if(storeRecipes) craftingRecipes.add(r);
		
		return r;
	}
	
	public void addItemBlockRecipe(ItemStack item, ItemStack block, boolean back)
	{
		addRecipe(block, "EEE", "EEE", "EEE", Character.valueOf('E'), item);
		if(back)
		{
			ItemStack out9 = item.copy();
			out9.stackSize = 9;
			addShapelessRecipe(out9, block);
		}
	}
	
	public void addOre(String s, ItemStack is)
	{ LatCoreMC.addOreDictionary(s, is); }
	
	public void addSmelting(ItemStack in, ItemStack out, float xp)
	{
		if(storeRecipes) furnaceRecipes.put(in, out);
		FurnaceRecipes.smelting().func_151394_a(in, out, xp);
	}
	
	public void addSmelting(ItemStack in, ItemStack out)
	{ addSmelting(in, out, 0F); }
}