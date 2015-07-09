package latmod.ftbu.mod.client;
import java.util.UUID;

import latmod.ftbu.core.*;
import latmod.ftbu.core.client.*;
import latmod.ftbu.core.client.badges.ThreadLoadBadges;
import latmod.ftbu.core.event.FTBUReadmeEvent;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.tile.TileLM;
import latmod.ftbu.core.util.LatCore;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.FTBUCommon;
import latmod.ftbu.mod.client.minimap.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class FTBUClient extends FTBUCommon
{
	public static final ClientConfig clientConfig = new ClientConfig("ftbu");
	public static final ClientConfig.Property enablePlayerDecorators = new ClientConfig.Property(clientConfig, "player_decorators", true);
	public static final ClientConfig.Property addOreNames = new ClientConfig.Property(clientConfig, "item_ore_names", false);
	public static final ClientConfig.Property addRegistryNames = new ClientConfig.Property(clientConfig, "item_reg_names", false);
	public static final ClientConfig.Property displayDebugInfo = new ClientConfig.Property(clientConfig, "debug_info", false);
	public static final ClientConfig.Property optionsButton = new ClientConfig.Property(clientConfig, "options_button", true);
	public static final ClientConfig.Property chatLinks = new ClientConfig.Property(clientConfig, "chat_links", 1, "disabled", "enabled"); //"replace", "print" });
	
	public void preInit()
	{
		LatCoreMC.BusType.FORGE.register(FTBUClientEventHandler.instance);
		LatCoreMC.BusType.LATMOD.register(FTBUClientEventHandler.instance);
		LatCoreMC.BusType.FORGE.register(FTBURenderHandler.instance);
		LatCoreMC.BusType.FML.register(FTBURenderHandler.instance);
		
		ClientConfig.Registry.init();
		ClientConfig.Registry.add(clientConfig);
		
		Waypoints.init();
		Minimap.init();
	}
	
	public void postInit()
	{
		ClientConfig.Registry.load();
		//ThreadLoadBadges.init();
	}
	
	public void addInfo(FTBUReadmeEvent e)
	{
		FTBUReadmeEvent.ReadmeFile.Category waypoints = e.file.get("waypoints");
		waypoints.add("testing", "test");
	}
	
	public boolean isShiftDown() { return GuiScreen.isShiftKeyDown(); }
	public boolean isCtrlDown() { return GuiScreen.isCtrlKeyDown(); }
	public boolean isTabDown() { return Keyboard.isKeyDown(Keyboard.KEY_TAB); }
	public boolean inGameHasFocus() { return Minecraft.getMinecraft().inGameHasFocus; }
	
	public EntityPlayer getClientPlayer()
	{ return FMLClientHandler.instance().getClientPlayerEntity(); }
	
	public EntityPlayer getClientPlayer(UUID id)
	{ return LatCoreMCClient.getPlayerSP(id); }
	
	public World getClientWorld()
	{ return FMLClientHandler.instance().getWorldClient(); }
	
	public LMWorld<?> getClientWorldLM()
	{ return LMWorld.client; }
	
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
		
		float alpha = LatCore.Colors.getAlpha(col) / 255F;
		float red = LatCore.Colors.getRed(col) / 255F;
		float green = LatCore.Colors.getGreen(col) / 255F;
		float blue = LatCore.Colors.getBlue(col) / 255F;
		if(alpha == 0F) alpha = 1F;
		
		fx.setRBGColorF(red, green, blue);
		fx.setAlphaF(alpha);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}
	
	public static void onWorldJoined(LMPlayer p)
	{
		ThreadLoadBadges.init();
		Waypoints.load();
		Minimap.minimaps.clear();
	}
	
	public void openClientGui(EntityPlayer ep, String id, NBTTagCompound data)
	{
		ILMGuiHandler h = ILMGuiHandler.Registry.getLMGuiHandler(id);
		
		if(h != null)
		{
			GuiScreen g = h.getGui(ep, id, data);
			if(g != null) Minecraft.getMinecraft().displayGuiScreen(g);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <M extends MessageLM<?>> void handleClientMessage(IClientMessageLM<M> m, MessageContext ctx)
	{ m.onMessageClient((M) m, ctx); }
	
	public void readTileData(TileLM t, S35PacketUpdateTileEntity p)
	{
		t.readTileData(p.func_148857_g());
		t.readTileClientData(p.func_148857_g());
		t.onUpdatePacket();
	}
}