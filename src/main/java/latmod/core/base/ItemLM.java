package latmod.core.base;
import java.util.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.creativetab.*;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;

public abstract class ItemLM extends Item
{
	public final String itemName;
	public ArrayList<ItemStack> itemsAdded = new ArrayList<ItemStack>();
	public final LMMod mod;

	public ItemLM(LMMod m, String s)
	{
		super();
		mod = m;
		itemName = s;
		setUnlocalizedName(mod.getItemName(s));
	}
	
	@SideOnly(Side.CLIENT)
	public abstract CreativeTabs getCreativeTab();
	
	public void onPostLoaded()
	{ itemsAdded.add(new ItemStack(this)); }
	
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item j, CreativeTabs c, List l)
	{
		for(ItemStack is : itemsAdded)
		{
			if(is != null && is.getItem() != null)
			{
				ItemStack is1 = new ItemStack(j, 1, is.getItemDamage());
				if(is.stackTagCompound != null) is1.stackTagCompound = (NBTTagCompound) is.stackTagCompound.copy();
				l.add(is1);
			}
		}
	}

	public String getUnlocalizedName(ItemStack is)
	{ return mod.getItemName(itemName); }

	public void addAllDamages(int until)
	{
		for(int i = 0; i < until; i++)
		itemsAdded.add(new ItemStack(this, 1, i));
	}
	
	public void addAllDamages(int[] dmg)
	{
		for(int i = 0; i < dmg.length; i++)
		itemsAdded.add(new ItemStack(this, 1, dmg[i]));
	}
	
	public final boolean requiresMultipleRenderPasses()
	{ return true; }

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister ir)
	{ itemIcon = ir.registerIcon(mod.assets + itemName); }

	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int i, int r)
	{ return itemIcon; }

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack is, int r)
	{ return getIconFromDamageForRenderPass(is.getItemDamage(), r); }

	public void loadRecipes()
	{
	}
}