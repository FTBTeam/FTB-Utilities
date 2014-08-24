package latmod.core.mod;
import latmod.core.client.LatCoreClient;
import latmod.core.mod.block.BlockPaintable;
import latmod.core.mod.client.render.block.RenderPaintable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class LCClient extends LCCommon
{
	public void preInit()
	{
		MinecraftForge.EVENT_BUS.register(new LCClientEventHandler());
	}
	
	public void init() { }
	
	public void postInit()
	{
		BlockPaintable.renderID = LatCoreClient.getNewBlockRenderID();
		LatCoreClient.addBlockRenderer(BlockPaintable.renderID, new RenderPaintable());
	}
	
	public int getKeyID(String s) { return Keyboard.getKeyIndex(s); }
	public boolean isKeyDown(int id) { return Keyboard.isKeyDown(id); }
	public boolean isShiftDown() { return GuiScreen.isShiftKeyDown(); }
	public boolean isCtrlDown() { return GuiScreen.isCtrlKeyDown(); }
	
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}
	
	public EntityPlayer getClientPlayer()
	{ return Minecraft.getMinecraft().thePlayer; }
	
	public double getReachDist(EntityPlayer ep)
	{ return Minecraft.getMinecraft().playerController.getBlockReachDistance(); }
}