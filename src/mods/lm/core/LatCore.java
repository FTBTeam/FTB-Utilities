package mods.lm.core;
import java.io.*;
import java.util.*;

import mods.lm.core.mod.*;
import net.minecraft.block.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.item.crafting.*;
import net.minecraft.network.INetHandler;
import net.minecraft.tileentity.*;
import net.minecraftforge.common.*;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.*;
import net.minecraftforge.oredict.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.*;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LatCore
{
	public static boolean enableOreRecipes = true;
	public static final int ANY = OreDictionary.WILDCARD_VALUE;
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
	public static final void printChat(String s) { LC.proxy.printChat(s); }
	
	// Registry methods //
	
	public static final void addItem(Item i, String name, String modid)
	{ GameRegistry.registerItem(i, modid + "_" + name, modid); }
	
	public static final void addBlock(Block b, Class<? extends ItemBlock> c, String name, String modid)
	{ GameRegistry.registerBlock(b, c, modid + "_" + name, modid); }
	
	public static final void addBlock(Block b, String name, String modid)
	{ addBlock(b, ItemBlock.class, name, modid); }
	
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
		ItemStack is1 = InvUtils.single(is);
		if(!OreDictionary.getOres(name).contains(is1))
		OreDictionary.registerOre(name, is1);
	}
	
	public static ArrayList<ItemStack> getOreDictionary(String name)
	{ return OreDictionary.getOres(name); }
	
	public static void addWorldGenerator(IWorldGenerator i)
	{ GameRegistry.registerWorldGenerator(i, 5); }
	
	public static void addGuiHandler(Object mod, IGuiHandler i)
	{ NetworkRegistry.INSTANCE.registerGuiHandler(mod, i); }
	
	public static void addPacketHandler(INetHandler i, String channel)
	//FIXME: { NetworkRegistry.INSTANCE.re(i, channel); }
	{ }
	
	public static void addTool(Item tool, String customClass, int level)
	//FIXME: { MinecraftForge.setToolClass(tool, customClass, level); }
	{  }
	
	public static void addTool(Item tool, EnumToolClass e, int level)
	{ addTool(tool, e.toolClass, level); }
	
	public static void addHarvestLevel(ItemStack block, String customClass, int level)
	//FIXME: { MinecraftForge.setBlockHarvestLevel(Block.blocksList[block.itemID], block.getItemDamage(), customClass, level); }
	{ }
	
	public static void addHarvestLevel(ItemStack block, EnumToolClass e, int level)
	{ addHarvestLevel(block, e.toolClass, level); }
	
	public static Fluid addFluid(Fluid f)
	{
		Fluid f1 = FluidRegistry.getFluid(f.getName());
		if(f1 != null) return f1;
		FluidRegistry.registerFluid(f);
		return f;
	}
	
	public static boolean canUpdate()
	{ return (!(FMLCommonHandler.instance().getEffectiveSide().isClient())); }
	
	public static void openGui(IGuiTile t, int ID, EntityPlayer ep)
	{ TileEntity te = (TileEntity)t;
	ep.openGui(LC.inst, ID, te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord); }
}