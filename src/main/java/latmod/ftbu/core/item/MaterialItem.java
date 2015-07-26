package latmod.ftbu.core.item;

import latmod.ftbu.core.util.FastList;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.*;

public class MaterialItem
{
	public final int damage;
	public final String ID;
	
	public ItemMaterialsLM item;
	public ItemStack stack;
	
	@SideOnly(Side.CLIENT)
	public IIcon icon;
	
	public MaterialItem(int d, String s)
	{ damage = d; ID = s; }
	
	public void init(ItemMaterialsLM i)
	{
		item = i;
		stack = new ItemStack(item, 1, damage);
	}
	
	public void onPostLoaded()
	{ }
	
	public void loadRecipes()
	{ }
	
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