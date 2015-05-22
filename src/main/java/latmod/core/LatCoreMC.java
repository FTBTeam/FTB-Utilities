package latmod.core;
import java.io.File;
import java.util.UUID;
import java.util.regex.Pattern;

import latmod.core.item.IItemLM;
import latmod.core.mod.*;
import latmod.core.net.*;
import latmod.core.tile.IGuiTile;
import latmod.core.util.*;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.Container;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.*;

import org.apache.logging.log4j.*;

import cpw.mods.fml.client.registry.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.relauncher.*;

/** Made by LatvianModder */
public class LatCoreMC
{
	// Something, Something, Eclipse, Something... \Minecraft\eclipse\.metadata\.plugins\org.eclipse.debug.core\.launches
	public static final String MC_VERSION = Loader.MC_VERSION;
	public static final String DEV_VERSION = "@VERSION@";
	
	public static final Logger logger = LogManager.getLogger("LatCoreMC");
	public static final EventBus EVENT_BUS = new EventBus();
	
	public static final int TOP = 1;
	public static final int BOTTOM = 0;
	public static final int FRONT = 3;
	
	public static final boolean isDevEnv = LC.VERSION.equals(DEV_VERSION);
	
	public static final String FORMATTING = "\u00a7";
	public static final Pattern textFormattingPattern = Pattern.compile("(?i)" + FORMATTING + "[0-9A-FK-OR]");
	
	public static File latmodFolder = null;
	
	private static final FastMap<String, ILMGuiHandler> guiHandlers = new FastMap<String, ILMGuiHandler>();
	
	public static ILMGuiHandler getLMGuiHandler(String id)
	{ return guiHandlers.get(id); }
	
	public static void addLMGuiHandler(String id, ILMGuiHandler i)
	{ guiHandlers.put(id, i); }
	
	// Client //
	
	@SideOnly(Side.CLIENT)
	public static class Client
	{
		public static IIcon blockNullIcon, unknownItemIcon;
		private static float lastBrightnessX, lastBrightnessY;
		
		public static final void addEntityRenderer(Class<? extends Entity> c, Render r)
		{ RenderingRegistry.registerEntityRenderingHandler(c, r); }
		
		public static final void addTileRenderer(Class<? extends TileEntity> c, TileEntitySpecialRenderer r)
		{ ClientRegistry.bindTileEntitySpecialRenderer(c, r); }
		
		public static final int getNewArmorID(String s)
		{ return RenderingRegistry.addNewArmourRendererPrefix(s); }
		
		public static final int getNewBlockRenderID()
		{ return RenderingRegistry.getNextAvailableRenderId(); }
		
		public static final void addBlockRenderer(int i, ISimpleBlockRenderingHandler r)
		{ RenderingRegistry.registerBlockHandler(i, r); }
		
		public static final void addItemRenderer(Item item, IItemRenderer i)
		{ MinecraftForgeClient.registerItemRenderer(item, i); }
		
		public static final void addItemRenderer(Block block, IItemRenderer i)
		{ MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(block), i); }
		
		public static void spawnPart(EntityFX e)
		{ Minecraft.getMinecraft().effectRenderer.addEffect(e); }
		
		public static KeyBinding addKeyBinding(String name, int key, String cat)
		{ KeyBinding k = new KeyBinding(name, key, cat); ClientRegistry.registerKeyBinding(k); return k; }
		
		public static void pushMaxBrightness()
		{
			lastBrightnessX = OpenGlHelper.lastBrightnessX;
			lastBrightnessY = OpenGlHelper.lastBrightnessY;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
		}
		
		public static void popMaxBrightness()
		{ OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY); }
	}
	
	public static final Configuration loadConfig(FMLPreInitializationEvent e, String s)
	{ return new Configuration(new File(e.getModConfigurationDirectory(), s)); }
	
	// Proxy methods //
	
	/** Prints message to chat (doesn't translate it) */
	public static final void printChat(ICommandSender ep, Object o, boolean broadcast)
	{
		if(ep == null && isDevEnv) ep = LC.proxy.getClientPlayer();
		
		if(ep != null)
		{
			IChatComponent msg = (o != null && o instanceof IChatComponent) ? (IChatComponent)o : new ChatComponentText("" + o);
			
			if(broadcast && ep instanceof MinecraftServer)
			{
				//for(EntityPlayerMP ep1 : getAllOnlinePlayers()) ep1.addChatMessage(msg);
				((MinecraftServer)ep).getConfigurationManager().sendChatMsg(msg);
			}
			else ep.addChatMessage(msg);
		}
		else logger.info(o);
	}
	
	public static final void printChat(ICommandSender ep, Object o)
	{ printChat(ep, o, false); }
	
	// Registry methods //
	
	public static final void addItem(IItemLM i)
	{ addItem((Item)i, i.getItemID()); }
	
	public static final void addItem(Item i, String name)
	{ GameRegistry.registerItem(i, name); }
	
	public static final void addBlock(Block b, Class<? extends ItemBlock> c, String name)
	{ GameRegistry.registerBlock(b, c, name); }
	
	public static final void addBlock(Block b, String name)
	{ addBlock(b, ItemBlock.class, name); }
	
	public static final void addTileEntity(Class<? extends TileEntity> c, String s, String... alt)
	{
		if(alt == null || alt.length == 0) GameRegistry.registerTileEntity(c, s);
		else GameRegistry.registerTileEntityWithAlternatives(c, s, alt);
	}
	
	public static final void addEntity(Class<? extends Entity> c, String s, int id, Object mod)
	{ EntityRegistry.registerModEntity(c, s, id, mod, 50, 1, true); }
	
	public static final int getNewEntityID()
	{ return EntityRegistry.findGlobalUniqueEntityId(); }
	
	public static void addWorldGenerator(IWorldGenerator i, int w)
	{ GameRegistry.registerWorldGenerator(i, w); }
	
	public static void addEventHandler(Object o, boolean forge, boolean cpw, boolean lm)
	{
		if(forge) MinecraftForge.EVENT_BUS.register(o);
		if(cpw) FMLCommonHandler.instance().bus().register(o);
		if(lm) EVENT_BUS.register(o);
	}
	
	public static Fluid addFluid(Fluid f)
	{
		Fluid f1 = FluidRegistry.getFluid(f.getName());
		if(f1 != null) return f1;
		FluidRegistry.registerFluid(f);
		return f;
	}
	
	public static boolean isServer()
	{ return getEffectiveSide().isServer(); }
	
	public static Side getEffectiveSide()
	{ return FMLCommonHandler.instance().getEffectiveSide(); }
	
	public static String getPath(ResourceLocation res)
	{ return "/assets/" + res.getResourceDomain() + "/" + res.getResourcePath(); }
	
	public static boolean hasOnlinePlayers()
	{ return !MinecraftServer.getServer().getConfigurationManager().playerEntityList.isEmpty(); }
	
	public static FastMap<UUID, EntityPlayerMP> getAllOnlinePlayers()
	{
		FastMap<UUID, EntityPlayerMP> m = new FastMap<UUID, EntityPlayerMP>();
		
		for(int i = 0; i < MinecraftServer.getServer().getConfigurationManager().playerEntityList.size(); i++)
		{
			EntityPlayerMP ep = (EntityPlayerMP)MinecraftServer.getServer().getConfigurationManager().playerEntityList.get(i);
			m.put(ep.getUniqueID(), ep);
		}
		
		return m;
	}
	
	public static Vertex getSpawnPoint(World w)
	{ ChunkCoordinates c = w.getSpawnPoint(); return new Vertex(c.posX + 0.5D, c.posY + 0.5D, c.posZ + 0.5D); }
	
	public static boolean remap(MissingMapping m, String id, Item i)
	{
		if(m.type == GameRegistry.Type.ITEM && id.equals(m.name))
		{
			m.remap(i);
			return true;
		}
		
		return false;
	}
	
	public static boolean remap(MissingMapping m, String id, Block b)
	{
		if(id.equals(m.name))
		{
			if(m.type == GameRegistry.Type.BLOCK) m.remap(b);
			else if(m.type == GameRegistry.Type.ITEM) m.remap(Item.getItemFromBlock(b));
			return true;
		}
		
		return false;
	}
	
	public static boolean isModInstalled(String s)
	{ return Loader.isModLoaded(s); }
	
	public static String removeFormatting(String s)
	{
		if(s == null) return null; if(s.isEmpty()) return "";
		return textFormattingPattern.matcher(s).replaceAll("");
	}
	
	public static MinecraftServer getServer()
	{ return FMLCommonHandler.instance().getMinecraftServerInstance(); }
	
	public static Exception executeCommand(ICommandSender ics, String s)
	{
		try { getServer().getCommandManager().executeCommand(ics, s); }
		catch(Exception e) { return e; } return null;
	}
	
	public static Exception executeCommand(ICommandSender ics, String cmd, String[] args)
	{ return executeCommand(ics, cmd + " " + LatCore.unsplit(args, " ")); }
	
	public static void notifyPlayer(EntityPlayerMP ep, Notification n)
	{
		if(ep != null) MessageLM.NET.sendTo(new MessageNotifyPlayer(n), ep);
		else MessageLM.NET.sendToAll(new MessageNotifyPlayer(n));
	}
	
	public static Object invokeStatic(String className, String methodName) throws Exception
	{ Class<?> c = Class.forName(className); return c.getMethod(methodName).invoke(null); }
	
	public static void openGui(EntityPlayer ep, String id, NBTTagCompound data)
	{
		if(ep == null || ep instanceof FakePlayer) return;
		
		ILMGuiHandler h = getLMGuiHandler(id);
		
		if(h == null) return;
		
		if(ep instanceof EntityPlayerMP)
		{
			Container c = h.getContainer(ep, id, data);
			if(c == null) return;
			
			EntityPlayerMP epM = (EntityPlayerMP)ep;
			epM.getNextWindowId();
			epM.closeContainer();
			epM.openContainer = c;
			epM.openContainer.windowId = epM.currentWindowId;
			epM.openContainer.addCraftingToCrafters(epM);
			MessageLM.NET.sendTo(new MessageOpenGui(id, data, epM.currentWindowId), epM);
		}
		else if(getEffectiveSide() == Side.CLIENT)
			LC.proxy.openClientGui(ep, id, data);
	}
	
	public static void openGui(EntityPlayer ep, IGuiTile i, NBTTagCompound data)
	{
		TileEntity te = (TileEntity)i;
		if(data == null) data = new NBTTagCompound();
		data.setIntArray("XYZ", new int[] { te.xCoord, te.yCoord, te.zCoord });
		openGui(ep, LCGuiHandler.TILE, data);
	}
}