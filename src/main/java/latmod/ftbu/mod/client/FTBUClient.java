package latmod.ftbu.mod.client;
import java.util.UUID;

import latmod.ftbu.core.*;
import latmod.ftbu.core.api.FTBUReloadableRegistry;
import latmod.ftbu.core.client.*;
import latmod.ftbu.core.event.FTBUReadmeEvent;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.tile.TileLM;
import latmod.ftbu.core.util.LatCore;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.*;
import latmod.ftbu.mod.client.badges.ThreadLoadBadges;
import latmod.ftbu.mod.client.minimap.*;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.entity.player.*;
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
	public static final ClientConfig.Property enablePlayerDecorators = new ClientConfig.Property("player_decorators", true);
	public static final ClientConfig.Property addOreNames = new ClientConfig.Property("item_ore_names", false);
	public static final ClientConfig.Property addRegistryNames = new ClientConfig.Property("item_reg_names", false);
	public static final ClientConfig.Property displayDebugInfo = new ClientConfig.Property("debug_info", false);
	public static final ClientConfig.Property optionsButton = new ClientConfig.Property("options_button", true);
	public static final ClientConfig.Property chatLinks = new ClientConfig.Property("chat_links", true);
	
	public static final ClientConfig miscConfig = new ClientConfig("ftbu_misc").setHidden();
	public static final ClientConfig.Property hideArmorFG = new ClientConfig.Property("hide_armor_fg", false);
	public static final ClientConfig.Property openHSB = new ClientConfig.Property("openHSB_cg", false);
	
	private static void initConfig()
	{
		clientConfig.add(enablePlayerDecorators);
		clientConfig.add(addOreNames);
		clientConfig.add(addRegistryNames);
		clientConfig.add(displayDebugInfo);
		clientConfig.add(optionsButton);
		clientConfig.add(chatLinks);
		ClientConfig.Registry.add(clientConfig);
		
		miscConfig.add(hideArmorFG);
		miscConfig.add(openHSB);
		ClientConfig.Registry.add(miscConfig);
	}
	
	public void preInit()
	{
		FTBULang.reload();
		
		LatCoreMC.BusType.FORGE.register(FTBUClientEventHandler.instance);
		LatCoreMC.BusType.FML.register(FTBUClientEventHandler.instance);
		LatCoreMC.BusType.LATMOD.register(FTBUClientEventHandler.instance);
		LatCoreMC.BusType.FORGE.register(FTBURenderHandler.instance);
		LatCoreMC.BusType.FML.register(FTBURenderHandler.instance);
		LatCoreMC.BusType.FORGE.register(FTBUGuiEventHandler.instance);
		FTBUReloadableRegistry.add(FTBUClientEventHandler.instance);
		
		ClientConfig.Registry.init();
		initConfig();
		Waypoints.init();
		Minimap.init();
	}
	
	public void postInit()
	{
		ClientConfig.Registry.load();
		//ThreadLoadBadges.init();
		FTBUGuiHandler.instance.registerClient();
	}
	
	public void addInfo(FTBUReadmeEvent e)
	{
		FTBUReadmeEvent.ReadmeFile.Category waypoints = e.file.get("waypoints");
		waypoints.add("You can create waypoints by opening WaypointsGUI (FriendsGUI > You > Waypoits)");
		waypoints.add("Right click on a waypoint to enable / disable it, Ctrl + right click to delete it, left click to open it's settings");
		waypoints.add("You can select between Marker and Beacon waypoints, change it's color, title and coords");
	}
	
	public boolean isShiftDown() { return GuiScreen.isShiftKeyDown(); }
	public boolean isCtrlDown() { return GuiScreen.isCtrlKeyDown(); }
	public boolean isTabDown() { return Keyboard.isKeyDown(Keyboard.KEY_TAB); }
	public boolean inGameHasFocus() { return LatCoreMCClient.getMinecraft().inGameHasFocus; }
	
	public EntityPlayer getClientPlayer()
	{ return FMLClientHandler.instance().getClientPlayerEntity(); }
	
	public EntityPlayer getClientPlayer(UUID id)
	{ return LatCoreMCClient.getPlayerSP(id); }
	
	public World getClientWorld()
	{ return FMLClientHandler.instance().getWorldClient(); }
	
	public LMWorld<?> getClientWorldLM()
	{ return LMWorldClient.inst; }
	
	public double getReachDist(EntityPlayer ep)
	{
		if(ep == null) return 0D;
		if(ep instanceof EntityPlayerMP) return super.getReachDist(ep);
		PlayerControllerMP c = LatCoreMCClient.getMinecraft().playerController;
		return (c == null) ? 0D : c.getBlockReachDistance();
	}
	
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
		LatCoreMCClient.getMinecraft().effectRenderer.addEffect(fx);
	}
	
	public static void onWorldJoined(LMPlayer p)
	{
		ThreadLoadBadges.init();
		Waypoints.load();
		Minimap.minimaps.clear();
	}
	
	public boolean openClientGui(EntityPlayer ep, String mod, int id, NBTTagCompound data)
	{
		LMGuiHandler h = LMGuiHandler.Registry.getLMGuiHandler(mod);
		
		if(h != null)
		{
			GuiScreen g = h.getGui(ep, id, data);
			
			if(g != null)
			{
				LatCoreMCClient.getMinecraft().displayGuiScreen(g);
				return true;
			}
		}
		
		return false;
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