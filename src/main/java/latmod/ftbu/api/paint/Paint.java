package latmod.ftbu.api.paint;

import cpw.mods.fml.relauncher.*;
import ftb.lib.LMNBTUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.*;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import java.util.Arrays;

public class Paint implements Cloneable
{
	public final Block block;
	public final int meta;
	
	public Paint(Block b, int m)
	{
		block = b;
		meta = m;
	}
	
	public String toString()
	{ return Block.blockRegistry.getNameForObject(block).replace("minecraft:", "") + "@" + meta; }
	
	public static void readFromNBT(NBTTagCompound tag, String s, Paint[] paint)
	{
		if(paint == null || paint.length == 0) return;
		Arrays.fill(paint, null);
		
		NBTTagList l = (NBTTagList)tag.getTag(s);
		
		if(l != null)
		for(int i = 0; i < l.tagCount(); i++)
		{
			if(l.func_150303_d() == LMNBTUtils.MAP)
			{
				NBTTagCompound tag1 = l.getCompoundTagAt(i);
				int id = tag1.getByte("ID");
				Block b = Block.getBlockById(tag1.getInteger("BlockID"));
				int m = tag1.getInteger("Metadata");
				if(b == null || b == Blocks.air || b.hasTileEntity(m))
				{ b = Blocks.stone; m = 0; }
				paint[id] = new Paint(b, m);
			}
			else
			{
				int[] ai = l.func_150306_c(i);
				
				Block b = Block.getBlockById(ai[1]);
				if(b == null || b == Blocks.air || b.hasTileEntity(ai[2]))
				{ b = Blocks.stone; ai[2] = 0; }
				
				if(l.tagCount() == 1 && ai[0] == -1)
					for(int j = 0; j < paint.length; j++)
						paint[j] = new Paint(b, ai[2]);
				else paint[ai[0]] = new Paint(b, ai[2]);
			}
		}
	}
	
	public static void writeToNBT(NBTTagCompound tag, String s, Paint[] paint)
	{
		if(paint == null || paint.length == 0) return;
		
		NBTTagList l = new NBTTagList();
		
		boolean allEqual = true;
		
		for(int i = 0; i < paint.length; i++)
		{
			if(!(paint[i] != null && paint[i].block == paint[0].block && paint[i].meta == paint[0].meta))
			{ allEqual = false; break; }
		}
		
		if(allEqual)
			l.appendTag(new NBTTagIntArray(new int[] { -1, Block.getIdFromBlock(paint[0].block), paint[0].meta }));
		else for(int i = 0; i < paint.length; i++) if(paint[i] != null)
			l.appendTag(new NBTTagIntArray(new int[] { i, Block.getIdFromBlock(paint[i].block), paint[i].meta }));
		
		if(l.tagCount() > 0) tag.setTag(s, l);
	}
	
	public Paint clone()
	{ return new Paint(block, meta); }
	
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess real, IBlockAccess fake, int x, int y, int z, int side)
	{
		IIcon icon = null;
		Block bo = real.getBlock(x, y, z);
		if(bo != null && bo instanceof ICustomPaintBlockIcon)
			icon = ((ICustomPaintBlockIcon)bo).getCustomPaintIcon(side, this);
		if(icon == null)
		{
			if(block instanceof ICustomPaintBlockIcon)
				icon = ((ICustomPaintBlockIcon)block).getCustomPaintIcon(side, this);
			if(icon == null) icon = block.getIcon(fake, x, y, z, side);
		}
		return icon;
	}
}