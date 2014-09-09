package latmod.core.mod;
import latmod.core.client.LatCoreMCClient;
import latmod.core.mod.block.BlockPaintable;
import latmod.core.mod.client.render.block.RenderPaintable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class LCClient extends LCCommon
{
	public void preInit()
	{
	}
	
	public void init() { }
	
	public void postInit()
	{
		BlockPaintable.renderID = LatCoreMCClient.getNewBlockRenderID();
		LatCoreMCClient.addBlockRenderer(BlockPaintable.renderID, new RenderPaintable());
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
	
	public void setSkinAndCape(EntityPlayer ep)
	{
		if(ep instanceof AbstractClientPlayer)
		{
			/* FIXME
			AbstractClientPlayer p = (AbstractClientPlayer)ep;
			
			if(p.getUniqueID().equals(Minecraft.getMinecraft().thePlayer.getUniqueID()))
			{
				p = Minecraft.getMinecraft().thePlayer;
			}
			
			ResourceLocation customSkinLocation = LC.mod.getLocation("custom/skin/" + p.getUniqueID() + ".png");
			p.func_152121_a(MinecraftProfileTexture.Type.SKIN, customSkinLocation);
			LatCoreMC.printChat(ep, "Set skint to " + p.getLocationSkin());
			AbstractClientPlayer.getDownloadImageSkin(p.getLocationSkin(), "http://i.imgur.com/yFSexm0.png");
			*/
		}
	}
}