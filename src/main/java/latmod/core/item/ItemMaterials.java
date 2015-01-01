package latmod.core.item;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.*;

public abstract class ItemMaterials extends ItemLM
{
	private final String[] names;
	private final String prefix;
	
	@SideOnly(Side.CLIENT)
	public IIcon[] icons;
	
	public ItemMaterials(String s)
	{
		super(s);
		setMaxDamage(0);
		setHasSubtypes(true);
		
		names = getNames();
		prefix = getPrefix();
	}
	
	public void onPostLoaded()
	{ addAllDamages(names.length); }
	
	public void loadRecipes()
	{
	}
	
	public String getUnlocalizedName(ItemStack is)
	{ return mod.getItemName((prefix != null ? (prefix + ".") : "") + names[is.getItemDamage()]); }
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister ir)
	{
		icons = new IIcon[names.length];
		for(int i = 0; i < icons.length; i++)
		icons[i] = ir.registerIcon(mod.assets + (prefix != null ? (prefix + "/") : "") + names[i]);
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int m, int r)
	{ return icons[m]; }
	
	public abstract String[] getNames();
	public abstract String getPrefix();
}