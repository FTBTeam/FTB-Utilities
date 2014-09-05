package latmod.core.mod.item;
import latmod.core.*;
import latmod.core.mod.*;
import latmod.core.mod.tile.IPaintable;
import latmod.core.util.FastList;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.*;

public class ItemBlockPainter extends ItemLC
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
	{
		return (is.hasTagCompound() && is.stackTagCompound.hasKey("Paint"))
				? ItemStack.loadItemStackFromNBT(is.stackTagCompound.getCompoundTag("Paint")) : null;
	}
	
	public boolean canPaintBlock(ItemStack is)
	{ return is.getItemDamage() <= getMaxDamage(); }
	
	public void damagePainter(ItemStack is, EntityPlayer ep)
	{ is.damageItem(1, ep); }
	
	public ItemStack onItemRightClick(ItemStack is, World w, EntityPlayer ep)
	{
		if(!w.isRemote && ep.isSneaking() && is.hasTagCompound() && is.stackTagCompound.hasKey("Paint"))
		{
			is = InvUtils.removeTags(is, "Paint");
			LatCoreMC.printChat(ep, "Paint texture cleared");
		}
		
		return is;
	}
	
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World w, int x, int y, int z, int s, float x1, float y1, float z1)
	{
		if(w.isRemote) return true;
		
		TileEntity te = ep.worldObj.getTileEntity(x, y, z);
		
		if(te != null && te instanceof IPaintable)
		{
			ItemStack paint = getPaintItem(is);
			
			if((ep.capabilities.isCreativeMode || canPaintBlock(is)) && ((IPaintable)te).setPaint(paint, ep))
			{
				if(!ep.capabilities.isCreativeMode)
					damagePainter(is, ep);
			}
		}
		else if(te == null && ep.isSneaking())
		{
			Block b = ep.worldObj.getBlock(x, y, z);
			
			if(b != Blocks.air)
			{
				if(b.getBlockBoundsMinX() == 0D && b.getBlockBoundsMinY() == 0D && b.getBlockBoundsMinZ() == 0D
				&& b.getBlockBoundsMaxX() == 1D && b.getBlockBoundsMaxY() == 1D && b.getBlockBoundsMaxZ() == 1D)
				{
					ItemStack paint = new ItemStack(b, 1, ep.worldObj.getBlockMetadata(x, y, z));
					
					if(!is.hasTagCompound())
						is.stackTagCompound = new NBTTagCompound();
					
					NBTTagCompound paintTag = new NBTTagCompound();
					paint.writeToNBT(paintTag);
					is.stackTagCompound.setTag("Paint", paintTag);
					
					LatCoreMC.printChat(ep, "Paint texture set to " + paint.getDisplayName());
				}
			}
		}
		
		//	LMNetHandler.INSTANCE.sendToServer(new MessageClientItemAction(is, ACTION_PAINT, data));
		//else onClientAction(is, ep, ACTION_PAINT, data);
		
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public void addInfo(ItemStack is, EntityPlayer ep, FastList<String> l)
	{
		ItemStack paint = getPaintItem(is);
		if(paint != null) l.add("Paint: " + paint.getDisplayName());
	}
}