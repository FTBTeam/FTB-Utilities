package latmod.ftbu.item;

import cpw.mods.fml.relauncher.*;
import latmod.lib.FastList;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class MaterialItem
{
	public final ItemMaterialsLM item;
	public final int damage;
	public final String ID;
	
	@SideOnly(Side.CLIENT)
	public IIcon icon;
	
	public MaterialItem(ItemMaterialsLM i, int d, String s)
	{
		item = i;
		damage = d;
		ID = s;
	}
	
	public ItemStack getStack(int s)
	{ return new ItemStack(item, s, damage); }
	
	public ItemStack getStack()
	{ return getStack(1); }
	
	public void onPostLoaded() { }
	public void loadRecipes() { }
	
	public int getRenderPasses()
	{ return 1; }
	
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int r)
	{ return icon; }
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister ir)
	{
		if(item.folder.isEmpty())
			icon = ir.registerIcon(item.mod.assets + ID);
		else
			icon = ir.registerIcon(item.mod.assets + item.folder + "/" + ID);
	}
	
	@SideOnly(Side.CLIENT)
	public void addInfo(EntityPlayer ep, FastList<String> l)
	{ }

	public String getUnlocalizedName()
	{ return item.mod.getItemName(item.folder.isEmpty() ? ID : (item.folder + "." + ID)); }
}