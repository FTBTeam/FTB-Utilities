package ftb.utils.api.paint;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.*;

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
	{ return Block.blockRegistry.getNameForObject(block).toString().replace("minecraft:", "") + "@" + meta; }
	
	public static void readFromNBT(NBTTagCompound tag, String s, Paint[] paint)
	{
		if(paint == null || paint.length == 0) return;
		Arrays.fill(paint, null);
		
		NBTTagList l = (NBTTagList) tag.getTag(s);
		
		if(l != null) for(int i = 0; i < l.tagCount(); i++)
		{
			int[] ai = l.getIntArrayAt(i);
			
			Block b = Block.getBlockById(ai[1]);
			if(b == null || b == Blocks.air || b.hasTileEntity(b.getStateFromMeta(ai[2])))
			{
				b = Blocks.stone;
				ai[2] = 0;
			}
			
			if(l.tagCount() == 1 && ai[0] == -1) for(int j = 0; j < paint.length; j++)
				paint[j] = new Paint(b, ai[2]);
			else paint[ai[0]] = new Paint(b, ai[2]);
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
			{
				allEqual = false;
				break;
			}
		}
		
		if(allEqual)
			l.appendTag(new NBTTagIntArray(new int[] {-1, Block.getIdFromBlock(paint[0].block), paint[0].meta}));
		else for(int i = 0; i < paint.length; i++)
			if(paint[i] != null)
				l.appendTag(new NBTTagIntArray(new int[] {i, Block.getIdFromBlock(paint[i].block), paint[i].meta}));
		
		if(l.tagCount() > 0) tag.setTag(s, l);
	}
	
	public Paint clone()
	{ return new Paint(block, meta); }
}