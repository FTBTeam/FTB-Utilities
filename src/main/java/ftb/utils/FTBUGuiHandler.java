package ftb.utils;

import ftb.lib.api.gui.*;
import ftb.utils.client.gui.claims.GuiClaimChunks;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.*;

public class FTBUGuiHandler extends LMGuiHandler
{
	public static final FTBUGuiHandler instance = new FTBUGuiHandler(FTBUFinals.MOD_ID);
	
	public static final int ADMIN_CLAIMS = 1;
	
	public FTBUGuiHandler(String s)
	{ super(s); }
	
	public Container getContainer(EntityPlayer ep, int id, NBTTagCompound data)
	{
		return new ContainerEmpty(ep, null);
	}
	
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer ep, int id, NBTTagCompound data)
	{
		if(id == ADMIN_CLAIMS) return new GuiClaimChunks(data.getLong("T"));
		return null;
	}
}