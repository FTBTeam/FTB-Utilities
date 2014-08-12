package latmod.core;
import java.io.File;
import java.util.ArrayList;

import latmod.core.util.FastList;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.item.crafting.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import net.minecraftforge.oredict.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.*;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.relauncher.*;

public class LatCore
{
	public static boolean enableOreRecipes = true;
	public static final int ANY = OreDictionary.WILDCARD_VALUE;
	public static final int TOP = ForgeDirection.UP.ordinal();
	public static final int BOTTOM = ForgeDirection.DOWN.ordinal();
	
	public static boolean debug = false;
	
	public static final int NBT_INT = 3;
	public static final int NBT_STRING = 8;
	public static final int NBT_LIST = 9;
	public static final int NBT_MAP = 10;
	public static final int NBT_INT_ARRAY = 11;
	
	public static final Configuration loadConfig(FMLPreInitializationEvent e, String s)
	{ return new Configuration(new File(e.getModConfigurationDirectory(), s)); }
	
	public static final CreativeTabs createTab(final String s, final ItemStack icon)
	{
		CreativeTabs tab = new CreativeTabs(s)
		{
			@SideOnly(Side.CLIENT)
			public ItemStack getIconItemStack()
			{ return icon; }
			
			@SideOnly(Side.CLIENT)
			public Item getTabIconItem()
			{ return icon.getItem(); }
		};
		
		return tab;
	}
	
	/** Prints message to chat (doesn't translate it) */
	public static final void printChat(ICommandSender ep, Object... o)
	{
		String s = ""; for(Object o1 : o) s += o1;
		if(ep == null) System.out.println(s);
		else ep.addChatMessage(new ChatComponentText(s));
	}
	
	// Registry methods //
	
	public static final void addItem(Item i, String name)
	{ GameRegistry.registerItem(i, name); }
	
	public static final void addBlock(Block b, Class<? extends ItemBlock> c, String name)
	{ GameRegistry.registerBlock(b, c, name); }
	
	public static final void addBlock(Block b, String name)
	{ addBlock(b, ItemBlock.class, name); }
	
	public static final void addTileEntity(Class<? extends TileEntity> c, String s)
	{ GameRegistry.registerTileEntity(c, s); }
	
	public static final void addEntity(Class<? extends Entity> c, String s, int id, Object mod)
	{ EntityRegistry.registerModEntity(c, s, id, mod, 50, 1, true); }
	
	public static final int getNewEntityID()
	{ return EntityRegistry.findGlobalUniqueEntityId(); }

	public static void addSmeltingRecipe(ItemStack out, ItemStack in, float xp)
	{ FurnaceRecipes.smelting().func_151394_a(in, out, xp); }
	
	@SuppressWarnings("all")
	public static IRecipe addRecipe(IRecipe r)
	{ CraftingManager.getInstance().getRecipeList().add(r); return r; }
	
	
	public static IRecipe addRecipe(ItemStack out, Object... in)
	{
		if(!enableOreRecipes) return GameRegistry.addShapedRecipe(out, in);
		else return addRecipe(new ShapedOreRecipe(out, in));
	}
	
	public static IRecipe addShapelessRecipe(ItemStack out, Object... in)
	{
		if(!enableOreRecipes)
		{
			ArrayList<ItemStack> al = new ArrayList<ItemStack>();
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
			
			return addRecipe(new ShapelessRecipes(out, al));
		}
		else return addRecipe(new ShapelessOreRecipe(out, in));
	}
	
	public static void addOreDictionary(String name, ItemStack is)
	{
		ItemStack is1 = InvUtils.singleCopy(is);
		if(!getOreDictionary(name).contains(is1))
		OreDictionary.registerOre(name, is1);
	}
	
	public static FastList<ItemStack> getOreDictionary(String name)
	{
		FastList<ItemStack> l = new FastList<ItemStack>();
		l.addAll(OreDictionary.getOres(name));
		return l;
	}
	
	public static void addWorldGenerator(IWorldGenerator i, int w)
	{ GameRegistry.registerWorldGenerator(i, w); }
	
	public static void addGuiHandler(Object mod, IGuiHandler i)
	{ NetworkRegistry.INSTANCE.registerGuiHandler(mod, i); }
	
	public static Fluid addFluid(Fluid f)
	{
		Fluid f1 = FluidRegistry.getFluid(f.getName());
		if(f1 != null) return f1;
		FluidRegistry.registerFluid(f);
		return f;
	}
	
	public static boolean canUpdate()
	{ return (!(getEffectiveSide().isClient())); }
	
	public static Side getEffectiveSide()
	{ return FMLCommonHandler.instance().getEffectiveSide(); }
}