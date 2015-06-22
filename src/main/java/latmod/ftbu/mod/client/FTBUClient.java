package latmod.ftbu.mod.client;
import latmod.ftbu.core.*;
import latmod.ftbu.core.client.ClientConfig;
import latmod.ftbu.core.client.badges.ThreadLoadBadges;
import latmod.ftbu.core.event.LMPlayerEvent;
import latmod.ftbu.core.net.*;
import latmod.ftbu.mod.FTBUCommon;
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
	public static final ClientConfig clientConfig = new ClientConfig("ftbu");
	public static final ClientConfig.Property enablePlayerDecorators = new ClientConfig.Property("player_decorators", true);
	public static final ClientConfig.Property addOreNames = new ClientConfig.Property("item_ore_names", false);
	public static final ClientConfig.Property addRegistryNames = new ClientConfig.Property("item_reg_names", false);
	public static final ClientConfig.Property displayDebugInfo = new ClientConfig.Property("debug_info", false);
	public static final ClientConfig.Property optionsButton = new ClientConfig.Property("options_button", true);
	
	public void preInit(FMLPreInitializationEvent e)
	{
		super.preInit(e);
		LatCoreMC.addEventHandler(FTBUClientEventHandler.instance, true, false, true);
		LatCoreMC.addEventHandler(FTBURenderHandler.instance, true, true, false);
		ClientConfig.Registry.init();
		
		clientConfig.add(enablePlayerDecorators);
		clientConfig.add(addOreNames);
		clientConfig.add(addRegistryNames);
		clientConfig.add(displayDebugInfo);
		clientConfig.add(optionsButton);
		ClientConfig.Registry.add(clientConfig);
		
		Waypoints.init();
	}
	
	public void postInit(FMLPostInitializationEvent e)
	{
		ClientConfig.Registry.load();
		//ThreadLoadBadges.init();
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
	
	public void playerLMLoggedIn(LMPlayer p, boolean firstTime)
	{
		boolean isSelf = p.getUUID().equals(getClientPlayer().getUniqueID());
		if(isSelf)
		{
			LatCoreMC.logger.info("Joined the server with PlayerID " + p.playerID + " in world " + LMWorld.getIDS());
			LMPlayer.currentClientPlayerID = p.playerID;
		}
		
		new LMPlayerEvent.LoggedIn(p, Side.CLIENT, p.getPlayerMP(), firstTime).post();
		
		if(isSelf)
		{
			ThreadLoadBadges.init();
			Waypoints.load();
		}
	}
	
	public void playerLMLoggedOut(LMPlayer p)
	{ new LMPlayerEvent.LoggedOut(p, Side.CLIENT, p.getPlayerMP()).post(); }
	
	public void playerLMDataChanged(LMPlayer p, String action)
	{ new LMPlayerEvent.DataChanged(p, Side.CLIENT, action).post(); }
	
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