package latmod.ftbu.mod.client;
import java.util.UUID;

import latmod.ftbu.core.*;
import latmod.ftbu.core.api.*;
import latmod.ftbu.core.client.*;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.tile.TileLM;
import latmod.ftbu.core.util.LMColorUtils;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.*;
import latmod.ftbu.mod.client.badges.*;
import latmod.ftbu.mod.client.minimap.*;
import latmod.ftbu.mod.player.ClientNotifications;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class FTBUClient extends FTBUCommon implements IFTBUReloadable
{
	public static final ClientConfig clientConfig = new ClientConfig("ftbu");
	public static final ClientConfig.Property enablePlayerDecorators = new ClientConfig.Property("player_decorators", true);
	public static final ClientConfig.Property addOreNames = new ClientConfig.Property("item_ore_names", false);
	public static final ClientConfig.Property addRegistryNames = new ClientConfig.Property("item_reg_names", false);
	public static final ClientConfig.Property displayDebugInfo = new ClientConfig.Property("debug_info", false);
	public static final ClientConfig.Property optionsButton = new ClientConfig.Property("options_button", true);
	
	public static final ClientConfig.Property chatLinks = new ClientConfig.Property("chat_links", true)
	{
		public void initGui()
		{
			setValue(LMWorldClient.inst.clientPlayer.chatLinks ? 1 : 0);
		}
		
		public void onClicked()
		{
			LMWorldClient.inst.clientPlayer.chatLinks = !LMWorldClient.inst.clientPlayer.chatLinks;
			setValue(LMWorldClient.inst.clientPlayer.chatLinks ? 1 : 0);
			LMNetHelper.sendToServer(new MessageClientGuiAction(MessageClientGuiAction.ACTION_CHAT_LINKS, LMWorldClient.inst.clientPlayer.chatLinks ? 1 : 0));
		}
	};
	
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
		
		EnumBusType.FORGE.register(FTBUClientEventHandler.instance);
		EnumBusType.FML.register(FTBUClientEventHandler.instance);
		EnumBusType.LATMOD.register(FTBUClientEventHandler.instance);
		EnumBusType.FORGE.register(FTBURenderHandler.instance);
		EnumBusType.FML.register(FTBURenderHandler.instance);
		EnumBusType.FORGE.register(FTBUGuiEventHandler.instance);
		
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
	
	public void onReloaded(Side s, ICommandSender sender) throws Exception
	{
		super.onReloaded(s, sender);
		if(s.isClient()) ThreadLoadBadges.init();
	}
	
	public static void onWorldJoined()
	{
		Badge.init();
		ClientNotifications.init();
		Waypoints.load();
		Minimap.load();
	}
	
	public static void onWorldClosed()
	{
		Minimap.save();
		ClientNotifications.init();
	}
	
	public void onReadmeEvent(ReadmeFile file)
	{
		ReadmeCategory waypoints = file.get("waypoints");
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
		LatCoreMCClient.getMinecraft().effectRenderer.addEffect(fx);
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
	
	public void chunkChanged(EntityEvent.EnteringChunk e)
	{
		super.chunkChanged(e);
		
		/*
		if(e.entity.worldObj.isRemote && Minimap.renderIngame.getB() && e.entity.getUniqueID().equals(getClientPlayer().getUniqueID()))
		{
			int rd = Math.max(5, LatCoreMCClient.getMinecraft().gameSettings.renderDistanceChunks);
			Minimap m = Minimap.get(e.entity.dimension);
			m.reloadArea(e.entity.worldObj, e.newChunkX - MathHelperLM.floor(rd / 2D), e.newChunkZ - MathHelperLM.floor(rd / 2D), rd, rd);
		}*/
	}
}