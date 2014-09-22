package latmod.core.mod.item;
import latmod.core.ODItems;
import latmod.core.mod.LCItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBlockPainter extends ItemLC implements PainterHelper.IPainterItem
{
	public static final String ACTION_PAINT = "paint";
	
	public ItemBlockPainter(String s)
	{
		super(s);
		setMaxStackSize(1);
		setMaxDamage(128);
		setFull3D();
	}
	
	public void loadRecipes()
	{
		addRecipe(new ItemStack(this), "SCS", "SPS", " P ",
				'S', ODItems.STICK,
				'C', LCItems.b_paintable,
				'P', ODItems.IRON);
	}
	
	public ItemStack getPaintItem(ItemStack is)
	{ return PainterHelper.getPaintItem(is); }
	
	public boolean canPaintBlock(ItemStack is)
	{ return is.getItemDamage() <= getMaxDamage(); }
	
	public void damagePainter(ItemStack is, EntityPlayer ep)
	{ is.damageItem(1, ep); }
	
	public ItemStack onItemRightClick(ItemStack is, World w, EntityPlayer ep)
	{ return PainterHelper.onItemRightClick(this, is, w, ep); }
	
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World w, int x, int y, int z, int s, float x1, float y1, float z1)
	{ return PainterHelper.onItemUse(this, is, ep, w, x, y, z, s, x1, y1, z1); }
}