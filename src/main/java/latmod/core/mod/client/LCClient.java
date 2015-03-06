package latmod.core.mod.client;
import latmod.core.*;
import latmod.core.client.playerdeco.ThreadCheckPlayerDecorators;
import latmod.core.mod.LCCommon;
import latmod.core.tile.IGuiTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class LCClient extends LCCommon
{
	public static KeyBinding key;
	
	public void preInit(FMLPreInitializationEvent e)
	{
		super.preInit(e);
		LatCoreMC.addEventHandler(LCClientEventHandler.instance, true, true, true);
		ThreadCheckPlayerDecorators.init();
		key = LatCoreMC.addKeyBinding("key.latcoremc", Keyboard.KEY_GRAVE, "key.categories.gameplay");
	}
	
	public void postInit(FMLPostInitializationEvent e)
	{
		//Minecraft.getMinecraft().getTextureManager().loadTextureMap(iconsTexture, iconsTextureMap);
	}
	
	public boolean isShiftDown() { return GuiScreen.isShiftKeyDown(); }
	public boolean isCtrlDown() { return GuiScreen.isCtrlKeyDown(); }
	public boolean isTabDown() { return Keyboard.isKeyDown(Keyboard.KEY_TAB); }
	
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof IGuiTile)
			return ((IGuiTile)te).getGui(player, ID);
		return null;
	}
	
	public EntityPlayer getClientPlayer()
	{ return FMLClientHandler.instance().getClientPlayerEntity(); }
	
	public World getClientWorld()
	{ return FMLClientHandler.instance().getWorldClient(); }
	
	public double getReachDist(EntityPlayer ep)
	{ return Minecraft.getMinecraft().playerController.getBlockReachDistance(); }
	
	public void openClientGui(EntityPlayer ep, IGuiTile i, int ID)
	{ Minecraft.getMinecraft().displayGuiScreen(i.getGui(ep, ID)); }
	
	public boolean inGameHasFocus()
	{ return Minecraft.getMinecraft().inGameHasFocus; }
	
	public static ResourceLocation getSkinTexture(String username)
	{
		ResourceLocation r = AbstractClientPlayer.getLocationSkin(username);
		AbstractClientPlayer.getDownloadImageSkin(r, username);
		return r;
	}
	
	public void notifyPlayer(Notification n)
	{ LCClientEventHandler.instance.messages.add(new GuiNotification(n)); }
	
	public void spawnDust(World w, double x, double y, double z, int col)
	{
		EntityReddustFX fx = new EntityReddustFX(w, x, y, z, 0F, 0F, 0F);
		
		float alpha = ((col >> 24) & 0xFF) / 255F;
		float red = ((col >> 16) & 0xFF) / 255F;
		float green = ((col >> 8) & 0xFF) / 255F;
		float blue = ((col >> 0) & 0xFF) / 255F;
		
		fx.setRBGColorF(red, green, blue);
		fx.setAlphaF(alpha);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}
}