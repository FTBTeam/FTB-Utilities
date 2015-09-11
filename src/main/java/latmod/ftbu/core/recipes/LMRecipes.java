package latmod.ftbu.core.recipes;
import cpw.mods.fml.common.registry.GameRegistry;
import latmod.ftbu.core.item.MaterialItem;
import latmod.ftbu.core.util.FastList;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.item.crafting.*;
import net.minecraftforge.oredict.*;

public class LMRecipes
{
	public boolean enableOreRecipes = true;
	
	public static ItemStack size(ItemStack is, int s)
	{ ItemStack is1 = is.copy(); is1.stackSize = s; return is1; }
	
	@SuppressWarnings("unchecked")
	public IRecipe addIRecipe(IRecipe r)
	{ CraftingManager.getInstance().getRecipeList().add(r); return r; }
	
	public Object[] fixObjects(Object[] in)
	{
		for(int i = 0; i < in.length; i++)
		{
			if(in[i] instanceof MaterialItem)
				in[i] = ((MaterialItem)in[i]).getStack();
			in[i] = in[i];
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
			
			r = addIRecipe(new ShapelessRecipes(out, al));
		}
		else r = addIRecipe(new ShapelessOreRecipe(out, in));
		
		return r;
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