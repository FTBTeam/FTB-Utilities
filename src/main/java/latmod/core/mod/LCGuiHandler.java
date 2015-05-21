package latmod.core.mod;

import latmod.core.*;
import latmod.core.gui.ContainerEmpty;
import latmod.core.mod.client.*;
import latmod.core.tile.IGuiTile;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.*;

public class LCGuiHandler implements ILMGuiHandler
{
	public static final LCGuiHandler instance = new LCGuiHandler();
	
	public static final String TILE = "lmc.tile";
	public static final String FRIENDS = "lmc.friends";
	public static final String SECURITY = "lmc.security";
	public static final String ITEM_DISPLAY = "lmc.itemdisplay";
	public static final String[] IDs = { TILE, FRIENDS, SECURITY, ITEM_DISPLAY };
	
	public Container getContainer(EntityPlayer ep, String id, NBTTagCompound data)
	{
		if(id.equals(TILE))
		{
			int[] xyz = data.getIntArray("XYZ");
			TileEntity te = ep.worldObj.getTileEntity(xyz[0], xyz[1], xyz[2]);
			if(te != null && !te.isInvalid() && te instanceof IGuiTile)
				return ((IGuiTile)te).getContainer(ep, data);
		}
		else return new ContainerEmpty(ep, null);
		
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer ep, String id, NBTTagCompound data)
	{
		if(id.equals(TILE))
		{
			int[] xyz = data.getIntArray("XYZ");
			TileEntity te = ep.worldObj.getTileEntity(xyz[0], xyz[1], xyz[2]);
			if(te != null && !te.isInvalid() && te instanceof IGuiTile)
				return ((IGuiTile)te).getGui(ep, data);
		}
		else if(id.equals(FRIENDS)) return new GuiFriends(ep);
		else if(id.equals(SECURITY)) return new GuiSecurity(ep);
		else if(id.equals(ITEM_DISPLAY))
		{
			ItemStack item = ItemStack.loadItemStackFromNBT(data.getCompoundTag("I"));
			String title = data.getString("T");
			String desc = data.getString("D");
			return new GuiDisplayBlock(ep, item, title, desc);
		}
		
		return null;
	}
}