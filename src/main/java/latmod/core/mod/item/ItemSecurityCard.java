package latmod.core.mod.item;
import latmod.core.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemSecurityCard extends ItemLC
{
	public static final String NBT_KEY = "Security";
	
	public ItemSecurityCard(String s)
	{
		super(s);
		setMaxStackSize(1);
	}
	
	public LMSecurity getSecurity(ItemStack is)
	{
		if(is == null || !is.hasTagCompound() || !is.stackTagCompound.hasKey(NBT_KEY)) return null;
		
		LMSecurity s = new LMSecurity("");
		s.readFromNBT(is.stackTagCompound.getCompoundTag(NBT_KEY));
		return s;
	}
	
	public void setSecurity(ItemStack is, LMSecurity s)
	{
		if(is == null) return;
		
		if(s == null)
		{
			InvUtils.removeTags(is, NBT_KEY);
		}
		else
		{
			if(!is.hasTagCompound())
				is.stackTagCompound = new NBTTagCompound();
			
			NBTTagCompound tag1 = new NBTTagCompound();
			s.writeToNBT(tag1);
			is.stackTagCompound.setTag(NBT_KEY, tag1);
		}
	}
	
	public ItemStack onItemRightClick(ItemStack is, World w, EntityPlayer ep)
	{
		if(!w.isRemote)
		{
			//Display security GUI
		}
		
		return is;
	}
}