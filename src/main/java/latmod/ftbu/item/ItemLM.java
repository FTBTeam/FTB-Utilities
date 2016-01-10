package latmod.ftbu.item;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.api.item.IItemLM;
import latmod.ftbu.util.LMMod;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.IIcon;

import java.util.*;

public abstract class ItemLM extends Item implements IItemLM
{
	private static final ArrayList<String> infoList = new ArrayList<>();
	
	public final String itemName;
	public final List<ItemStack> itemsAdded;
	
	public boolean requiresMultipleRenderPasses = false;
	
	public ItemLM(String s)
	{
		super();
		itemName = s;
		setUnlocalizedName(getMod().getItemName(s));
		itemsAdded = new ArrayList<>();
	}
	
	public abstract LMMod getMod();
	
	@SuppressWarnings("unchecked")
	public final <E> E register()
	{
		getMod().addItem(this);
		return (E) this;
	}
	
	public final Item getItem()
	{ return this; }
	
	public final String getItemID()
	{ return itemName; }
	
	@SideOnly(Side.CLIENT)
	public abstract CreativeTabs getCreativeTab();
	
	public void onPostLoaded()
	{ addAllDamages(1); }
	
	public void loadRecipes()
	{
	}
	
	@SuppressWarnings("all")
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item j, CreativeTabs c, List l)
	{
		for(ItemStack is : itemsAdded)
			if(isVisible(is)) l.add(is);
	}
	
	public String getUnlocalizedName(ItemStack is)
	{ return getMod().getItemName(itemName); }
	
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
	{ return requiresMultipleRenderPasses; }
	
	public int getRenderPasses(int m)
	{ return 1; }
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister ir)
	{ itemIcon = ir.registerIcon(getMod().assets + itemName); }
	
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int i, int r)
	{ return itemIcon; }
	
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack is, int r)
	{ return getIconFromDamageForRenderPass(is.getItemDamage(), r); }
	
	@SideOnly(Side.CLIENT)
	public final IIcon getIconFromDamage(int i)
	{ return getIconFromDamageForRenderPass(i, 0); }
	
	@SideOnly(Side.CLIENT)
	public final IIcon getIconIndex(ItemStack is)
	{ return getIcon(is, 0); }
	
	@SuppressWarnings("all")
	@SideOnly(Side.CLIENT)
	public final void addInformation(ItemStack is, EntityPlayer ep, List l, boolean b)
	{
		infoList.clear();
		addInfo(is, ep, infoList);
		l.addAll(infoList);
	}
	
	@SideOnly(Side.CLIENT)
	public void addInfo(ItemStack is, EntityPlayer ep, List<String> l)
	{
	}
	
	public boolean isVisible(ItemStack is)
	{ return true; }
}