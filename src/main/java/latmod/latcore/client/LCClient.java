package latmod.latcore.client;
import latmod.core.tile.IGuiTile;
import latmod.latcore.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class LCClient extends LCCommon
{
	public void preInit(FMLPreInitializationEvent e)
	{
		super.preInit(e);
		MinecraftForge.EVENT_BUS.register(LCClientEventHandler.instance);
		FMLCommonHandler.instance().bus().register(LCClientEventHandler.instance);
		ThreadCheckPlayerDecorators.init();
	}
	
	public int getKeyID(String s) { return Keyboard.getKeyIndex(s); }
	public boolean isKeyDown(int id) { return Keyboard.isKeyDown(id); }
	public boolean isShiftDown() { return GuiScreen.isShiftKeyDown(); }
	public boolean isCtrlDown() { return GuiScreen.isCtrlKeyDown(); }
	
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof IGuiTile)
			return ((IGuiTile)te).getGui(player, ID);
		return null;
	}
	
	public EntityPlayer getClientPlayer()
	{ return Minecraft.getMinecraft().thePlayer; }
	
	public World getClientWorld()
	{ return Minecraft.getMinecraft().theWorld; }
	
	public double getReachDist(EntityPlayer ep)
	{ return Minecraft.getMinecraft().playerController.getBlockReachDistance(); }
	
	public void openClientGui(EntityPlayer ep, IGuiTile i, int ID)
	{ Minecraft.getMinecraft().displayGuiScreen(i.getGui(ep, ID)); }
}