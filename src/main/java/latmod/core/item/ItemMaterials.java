package latmod.core.item;
import latmod.core.LatCoreMC;
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
	{
		for(int i = 0; i < names.length; i++)
		{
			if(names[i] != null && !names[i].isEmpty())
				itemsAdded.add(new ItemStack(this, 1, i));
		}
	}
	
	public void loadRecipes()
	{
	}
	
	public String getUnlocalizedName(ItemStack is)
	{
		int dmg = is.getItemDamage();
		if(dmg < 0 || dmg >= names.length) return "unknown";
		return mod.getItemName((prefix != null ? (prefix + ".") : "") + names[dmg]);
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister ir)
	{
		icons = new IIcon[names.length];
		for(int i = 0; i < names.length; i++)
		{
			if(names[i] != null && !names[i].isEmpty())
				icons[i] = ir.registerIcon(mod.assets + (prefix != null ? (prefix + "/") : "") + names[i]);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int m, int r)
	{ return (m >= 0 && m < icons.length) ? icons[m] : LatCoreMC.Client.unknownItemIcon; }
	
	public abstract String[] getNames();
	public abstract String getPrefix();
}