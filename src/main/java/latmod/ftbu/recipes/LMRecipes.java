package latmod.ftbu.recipes;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraftforge.oredict.*;

import java.util.ArrayList;

public class LMRecipes
{
	public static final LMRecipes defaultInstance = new LMRecipes();
	
	public static boolean enableOreRecipes = true;
	
	public static ItemStack size(ItemStack is, int s)
	{
		ItemStack is1 = is.copy();
		is1.stackSize = s;
		return is1;
	}
	
	@SuppressWarnings("unchecked")
	public IRecipe addIRecipe(IRecipe r)
	{
		CraftingManager.getInstance().getRecipeList().add(r);
		return r;
	}
	
	public Object[] fixObjects(Object[] in)
	{
		for(int i = 0; i < in.length; i++)
		{
			Object o = StackArray.getFrom(in[i]);
			if(o != null) in[i] = o;
		}
		
		return in;
	}
	
	public IRecipe addRecipe(ItemStack out, Object... in)
	{
		in = fixObjects(in);
		IRecipe r;
		
		if(!enableOreRecipes) r = GameRegistry.addShapedRecipe(out, in);
		else r = addIRecipe(new ShapedOreRecipe(out, in));
		
		return r;
	}
	
	public IRecipe addShapelessRecipe(ItemStack out, Object... in)
	{
		in = fixObjects(in);
		
		if(!enableOreRecipes)
		{
			ArrayList<ItemStack> al = new ArrayList<>();
			
			for(int j = 0; j < in.length; ++j)
			{
				ItemStack is = StackArray.getFrom(in[j]);
				if(is != null) al.add(is);
				else throw new RuntimeException("Invalid shapeless recipy!");
			}
			
			return addIRecipe(new ShapelessRecipes(out, al));
		}
		
		return addIRecipe(new ShapelessOreRecipe(out, in));
	}
	
	public void addItemBlockRecipe(ItemStack block, ItemStack item, boolean back, boolean small)
	{
		if(small)
		{
			addRecipe(block, "EE", "EE", Character.valueOf('E'), item);
			
			if(back)
			{
				ItemStack out4 = item.copy();
				out4.stackSize = 4;
				addShapelessRecipe(out4, block);
			}
		}
		else
		{
			addRecipe(block, "EEE", "EEE", "EEE", Character.valueOf('E'), item);
			
			if(back)
			{
				ItemStack out9 = item.copy();
				out9.stackSize = 9;
				addShapelessRecipe(out9, block);
			}
		}
	}
	
	public void addSmelting(ItemStack out, ItemStack in, float xp)
	{ FurnaceRecipes.smelting().func_151394_a(in, out, xp); }
	
	public void addSmelting(ItemStack out, ItemStack in)
	{ addSmelting(out, in, 0F); }
	
	public void loadRecipes()
	{
	}
}