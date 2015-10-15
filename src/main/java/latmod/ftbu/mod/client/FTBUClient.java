package latmod.ftbu.mod.client;
import java.util.UUID;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.*;
import latmod.ftbu.api.client.*;
import latmod.ftbu.api.guide.*;
import latmod.ftbu.badges.*;
import latmod.ftbu.mod.*;
import latmod.ftbu.net.ClientAction;
import latmod.ftbu.tile.TileLM;
import latmod.ftbu.util.*;
import latmod.ftbu.util.client.*;
import latmod.ftbu.world.*;
import latmod.lib.LMColorUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class FTBUClient extends FTBUCommon
{
	public static final ClientConfig clientConfig = new ClientConfig("ftbu");
	public static final ClientConfigProperty renderBadges = new ClientConfigProperty("player_decorators", true);
	
	public static final ClientConfigProperty renderMyBadge = new ClientConfigProperty("player_decorators_self", true)
	{
		public void initGui()
		{ setValue(LMWorldClient.inst.clientPlayer.settings.renderBadge ? 1 : 0); }
		
		public void onClicked()
		{ ClientAction.ACTION_RENDER_BADGE.send(LMWorldClient.inst.clientPlayer.settings.renderBadge ? 0 : 1); }
	};
	
	public static final ClientConfigProperty addOreNames = new ClientConfigProperty("item_ore_names", false);
	public static final ClientConfigProperty addRegistryNames = new ClientConfigProperty("item_reg_names", false);
	public static final ClientConfigProperty displayDebugInfo = new ClientConfigProperty("debug_info", false);
	public static final ClientConfigProperty optionsButton = new ClientConfigProperty("options_button", true);
	
	public static final ClientConfigProperty chatLinks = new ClientConfigProperty("chat_links", true)
	{
		public void initGui()
		{ setValue(LMWorldClient.inst.clientPlayer.settings.chatLinks ? 1 : 0); }
		
		public void onClicked()
		{ ClientAction.ACTION_CHAT_LINKS.send(LMWorldClient.inst.clientPlayer.settings.chatLinks ? 0 : 1); }
	};
	
	public static final ClientConfig miscConfig = new ClientConfig("ftbu_misc").setHidden();
	public static final ClientConfigProperty hideArmorFG = new ClientConfigProperty("hide_armor_fg", false);
	public static final ClientConfigProperty openHSB = new ClientConfigProperty("openHSB_cg", false);
	
	private static void initConfig()
	{
		if(FTBUFinals.DEV) clientConfig.add(displayDebugInfo);
		else displayDebugInfo.setValue(0);
		
		clientConfig.add(renderBadges);
		clientConfig.add(renderMyBadge);
		clientConfig.add(addOreNames);
		clientConfig.add(addRegistryNames);
		clientConfig.add(optionsButton);
		clientConfig.add(chatLinks);
		ClientConfigRegistry.add(clientConfig);
		
		miscConfig.add(hideArmorFG);
		miscConfig.add(openHSB);
		ClientConfigRegistry.add(miscConfig);
	}
	
	public static void onWorldJoined()
	{
		Badge.init();
		ThreadLoadBadges.init();
		ClientNotifications.init();
	}
	
	public static void onWorldClosed()
	{
		ClientNotifications.init();
	}
	
	public void preInit()
	{
		JsonHelper.initClient();
		EventBusHelper.register(FTBUClientEventHandler.instance);
		EventBusHelper.register(FTBURenderHandler.instance);
		EventBusHelper.register(FTBUGuiEventHandler.instance);
		
		ClientConfigRegistry.init();
		initConfig();
		
		FTBUBadgeRenderer.instance.enable(true);
	}
	
	public void postInit()
	{
		ClientConfigRegistry.load();
		FTBUGuiHandler.instance.registerClient();
	}
	
	public void onReadmeEvent(GuideFile file)
	{
		GuideCategory waypoints = new GuideCategory("Waypoints");
		waypoints.add("You can create waypoints by opening WaypointsGUI (FriendsGUI > You > Waypoits)");
		waypoints.add("Right click on a waypoint to enable / disable it, Ctrl + right click to delete it, left click to open it's settings");
		waypoints.add("You can select between Marker and Beacon waypoints, change it's color, title and coords");
	}
	
	public boolean isShiftDown() { return GuiScreen.isShiftKeyDown(); }
	public boolean isCtrlDown() { return GuiScreen.isCtrlKeyDown(); }
	public boolean isTabDown() { return Keyboard.isKeyDown(Keyboard.KEY_TAB); }
	public boolean inGameHasFocus() { return LatCoreMCClient.mc.inGameHasFocus; }
	
	public EntityPlayer getClientPlayer()
	{ return FMLClientHandler.instance().getClientPlayerEntity(); }
	
	public EntityPlayer getClientPlayer(UUID id)
	{ return LatCoreMCClient.getPlayerSP(id); }
	
	public World getClientWorld()
	{ return FMLClientHandler.instance().getWorldClient(); }
	
	public LMWorld getClientWorldLM()
	{ return LMWorldClient.inst; }
	
	public double getReachDist(EntityPlayer ep)
	{
		if(ep == null) return 0D;
		else if(ep instanceof EntityPlayerMP) return super.getReachDist(ep);
		PlayerControllerMP c = LatCoreMCClient.mc.playerController;
		return (c == null) ? 0D : c.getBlockReachDistance();
	}
	
	public void spawnDust(World w, double x, double y, double z, int col)
	{
		EntityReddustFX fx = new EntityReddustFX(w, x, y, z, 0F, 0F, 0F);
		
		float alpha = LMColorUtils.getAlpha(col) / 255F;
		float red = LMColorUtils.getRed(col) / 255F;
		float green = LMColorUtils.getGreen(col) / 255F;
		float blue = LMColorUtils.getBlue(col) / 255F;
		if(alpha == 0F) alpha = 1F;
		
		fx.setRBGColorF(red, green, blue);
		fx.setAlphaF(alpha);
		LatCoreMCClient.mc.effectRenderer.addEffect(fx);
	}
	
	public boolean openClientGui(EntityPlayer ep, String mod, int id, NBTTagCompound data)
	{
		LMGuiHandler h = LMGuiHandler.Registry.getLMGuiHandler(mod);
		
		if(h != null)
		{
			GuiScreen g = h.getGui(ep, id, data);
			
			if(g != null)
			{
				LatCoreMCClient.mc.displayGuiScreen(g);
				return true;
			}
		}
		
		return false;
	}
	
	public void readTileData(TileLM t, S35PacketUpdateTileEntity p)
	{
		NBTTagCompound data = p.func_148857_g();
		t.readTileData(data);
		t.readTileClientData(data);
		t.onUpdatePacket();
		LatCoreMCClient.onGuiClientAction();
	}

	public static void onReloaded()
	{
		LatCoreMCClient.clearCachedData();
		ThreadLoadBadges.init();
		
		if(LMWorldClient.inst != null)
		{
			for(int i = 0; i < LMWorldClient.inst.players.size(); i++)
				LMWorldClient.inst.players.get(i).toPlayerSP().onReloaded();
		}
	}
}