package latmod.ftbu.mod;

import cpw.mods.fml.relauncher.*;
import ftb.lib.item.ItemDisplay;
import latmod.ftbu.api.tile.IGuiTile;
import latmod.ftbu.mod.client.gui.GuiDisplayItem;
import latmod.ftbu.mod.client.gui.friends.GuiFriends;
import latmod.ftbu.util.LMGuiHandler;
import latmod.ftbu.util.gui.ContainerEmpty;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class FTBUGuiHandler extends LMGuiHandler
{
	public static final FTBUGuiHandler instance = new FTBUGuiHandler(FTBUFinals.MOD_ID);
	
	public static final int TILE = 1;
	public static final int FRIENDS = 2;
	public static final int SECURITY = 3;
	public static final int DISPLAY_ITEM = 4;
	
	public FTBUGuiHandler(String s)
	{ super(s); }
	
	public Container getContainer(EntityPlayer ep, int id, NBTTagCompound data)
	{
		if(id == TILE)
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
	public GuiScreen getGui(EntityPlayer ep, int id, NBTTagCompound data)
	{
		if(id == TILE)
		{
			int[] xyz = data.getIntArray("XYZ");
			TileEntity te = ep.worldObj.getTileEntity(xyz[0], xyz[1], xyz[2]);
			if(te != null && !te.isInvalid() && te instanceof IGuiTile)
				return ((IGuiTile)te).getGui(ep, data);
		}
		else if(id == FRIENDS) return new GuiFriends(null);
		else if(id == DISPLAY_ITEM)
			return new GuiDisplayItem(ItemDisplay.readFromNBT(data));
		
		return null;
	}
}