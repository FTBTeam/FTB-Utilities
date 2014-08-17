package latmod.core.mod.item;

import latmod.core.*;
import latmod.core.mod.LC;
import latmod.core.mod.tile.ILinkable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.*;

public class ItemLinkCard extends ItemLC
{
	public static final String NBT_KEY = "Link";
	
	public ItemLinkCard(String s)
	{
		super(s);
		setMaxStackSize(1);
	}
	
	public void loadRecipes()
	{
		LC.recipes.addRecipe(new ItemStack(this), "RGR", "PEP", "RGR",
				'R', ODItems.REDSTONE,
				'G', ODItems.NUGGET_GOLD,
				'P', Items.paper,
				'E', Items.ender_pearl);
	}
	
	public ItemStack onItemRightClick(ItemStack is, World w, EntityPlayer ep)
	{
		if(!w.isRemote && is.hasTagCompound() && ep.isSneaking())
			is = InvUtils.removeTags(is, NBT_KEY);
		return is;
	}
	
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World w, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if(is.hasTagCompound() && is.stackTagCompound.hasKey(NBT_KEY))
		{
			TileEntity te = w.getTileEntity(x, y, z);
			
			if(te != null && te instanceof ILinkable)
			{
				NBTTagCompound link = is.stackTagCompound.getCompoundTag(NBT_KEY);
				
				int[] pos = link.getIntArray("Pos");
				int linkSide = link.getByte("Side");
				int[] linkHit = link.getIntArray("Hit");
				float linkHitX = Float.intBitsToFloat(linkHit[0]);
				float linkHitY = Float.intBitsToFloat(linkHit[1]);
				float linkHitZ = Float.intBitsToFloat(linkHit[2]);
				
				MovingObjectPosition tilePos = new MovingObjectPosition(x, y, z, side, Vec3.createVectorHelper(hitX, hitY, hitZ));
				MovingObjectPosition linkPos = new MovingObjectPosition(pos[0], pos[1], pos[2], linkSide, Vec3.createVectorHelper(linkHitX, linkHitY, linkHitZ));
				
				if(((ILinkable)te).onLinked(ep, tilePos, linkPos))
				{
					if(!w.isRemote)
					{
						is = InvUtils.removeTags(is, NBT_KEY);
						ep.inventory.markDirty();
					}
					
					return true;
				}
			}
		}
		else
		{
			NBTTagCompound link = new NBTTagCompound();
			
			link.setIntArray("Pos", new int[] { x, y, z });
			link.setByte("Side", (byte)side);
			
			int[] linkHit =
			{
				Float.floatToIntBits(hitX),
				Float.floatToIntBits(hitY),
				Float.floatToIntBits(hitZ),
			};
			
			link.setIntArray("Hit", linkHit);
			
			if(!is.hasTagCompound())
				is.stackTagCompound = new NBTTagCompound();
			
			is.stackTagCompound.setTag(NBT_KEY, link);
		}
		
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack is, int pass)
	{ return is.hasTagCompound() && is.stackTagCompound.hasKey(NBT_KEY); }
}