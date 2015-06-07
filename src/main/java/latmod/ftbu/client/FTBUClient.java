package latmod.ftbu.client;
import latmod.ftbu.FTBUCommon;
import latmod.ftbu.core.*;
import latmod.ftbu.core.client.badges.ThreadLoadBadges;
import latmod.ftbu.core.event.LMPlayerClientEvent;
import latmod.ftbu.core.net.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class FTBUClient extends FTBUCommon
{
	public void preInit(FMLPreInitializationEvent e)
	{
		super.preInit(e);
		LatCoreMC.addEventHandler(FTBUClientEventHandler.instance, true, true, true);
	}
	
	public void postInit(FMLPostInitializationEvent e)
	{
	}
	
	public void serverStarting(FMLServerStartingEvent e)
	{
		ThreadLoadBadges.init();
	}
	
	public boolean isShiftDown() { return GuiScreen.isShiftKeyDown(); }
	public boolean isCtrlDown() { return GuiScreen.isCtrlKeyDown(); }
	public boolean isTabDown() { return Keyboard.isKeyDown(Keyboard.KEY_TAB); }
	public boolean inGameHasFocus() { return Minecraft.getMinecraft().inGameHasFocus; }
	
	public EntityPlayer getClientPlayer()
	{ return FMLClientHandler.instance().getClientPlayerEntity(); }
	
	public World getClientWorld()
	{ return FMLClientHandler.instance().getWorldClient(); }
	
	public double getReachDist(EntityPlayer ep)
	{ return Minecraft.getMinecraft().playerController.getBlockReachDistance(); }
	
	public static ResourceLocation getSkinTexture(String username)
	{
		ResourceLocation r = AbstractClientPlayer.getLocationSkin(username);
		AbstractClientPlayer.getDownloadImageSkin(r, username);
		return r;
	}
	
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
	
	public void playerLMLoggedIn(LMPlayer p)
	{ new LMPlayerClientEvent.LoggedIn(p, p.getPlayerSP()).post(); }
	
	public void playerLMLoggedOut(LMPlayer p)
	{ new LMPlayerClientEvent.LoggedOut(p, p.getPlayerSP()).post(); }
	
	public void playerLMDataChanged(LMPlayer p, String action)
	{ new LMPlayerClientEvent.DataChanged(p, action); }
	
	public void openClientGui(EntityPlayer ep, String id, NBTTagCompound data)
	{
		ILMGuiHandler h = LatCoreMC.getLMGuiHandler(id);
		
		if(h != null)
		{
			GuiScreen g = h.getGui(ep, id, data);
			if(g != null) Minecraft.getMinecraft().displayGuiScreen(g);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <M extends MessageLM<?>> void handleClientMessage(IClientMessageLM<M> m, MessageContext ctx)
	{ m.onMessageClient((M) m, ctx); }
}