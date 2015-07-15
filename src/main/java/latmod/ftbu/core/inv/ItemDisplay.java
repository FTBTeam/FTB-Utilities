package latmod.ftbu.core.inv;

import latmod.ftbu.core.NBTHelper;
import latmod.ftbu.core.util.FastList;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;

public class ItemDisplay
{
	public final ItemStack item;
	public final String title;
	public final FastList<String> desc;
	public final float scale;
	
	public ItemDisplay(ItemStack is, String t, FastList<String> d, float s)
	{
		item = (is == null) ? new ItemStack(Blocks.stone) : is;
		title = (t == null) ? "" : t;
		desc = (d == null) ? new FastList<String>() : d;
		scale = MathHelper.clamp_float(s, 1F, 8F);
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		NBTTagCompound tag1 = new NBTTagCompound();
		item.writeToNBT(tag1);
		tag.setTag("I", tag1);
		tag.setString("T", title);
		tag.setTag("D", NBTHelper.fromStringList(desc));
		tag.setFloat("S", scale);
	}
	
	public static ItemDisplay readFromNBT(NBTTagCompound tag)
	{ return new ItemDisplay(ItemStack.loadItemStackFromNBT(tag.getCompoundTag("I")), tag.getString("T"), NBTHelper.toStringList(tag.getTagList("D", NBTHelper.STRING)), tag.getFloat("S")); }
}