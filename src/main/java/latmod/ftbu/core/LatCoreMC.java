package latmod.ftbu.core;
import java.io.File;
import java.util.UUID;
import java.util.regex.Pattern;

import latmod.ftbu.core.item.IItemLM;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.tile.IGuiTile;
import latmod.ftbu.core.util.*;
import latmod.ftbu.mod.*;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.Container;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.*;

import org.apache.logging.log4j.*;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.relauncher.Side;

/** Made by LatvianModder */
public final class LatCoreMC // LatCoreMCClient
{
	// Something, Something, Eclipse, Something... \Minecraft\eclipse\.metadata\.plugins\org.eclipse.debug.core\.launches
	public static final String MC_VERSION = Loader.MC_VERSION;
	public static final String DEV_VERSION = "@VERSION@";
	
	public static final Logger logger = LogManager.getLogger("FTBUtilities");
	public static final EventBus EVENT_BUS = new EventBus();
	
	public static final boolean isDevEnv = FTBUFinals.VERSION.equals(DEV_VERSION);
	
	public static final String FORMATTING = "\u00a7";
	public static final Pattern textFormattingPattern = Pattern.compile("(?i)" + FORMATTING + "[0-9A-FK-OR]");
	
	public static File latmodFolder = null;
	
	private static final FastMap<String, ILMGuiHandler> guiHandlers = new FastMap<String, ILMGuiHandler>();
	
	public static ILMGuiHandler getLMGuiHandler(String id)
	{ return guiHandlers.get(id); }
	
	public static void addLMGuiHandler(String id, ILMGuiHandler i)
	{ guiHandlers.put(id, i); }
	
	public static final Configuration loadConfig(FMLPreInitializationEvent e, String s)
	{ return new Configuration(new File(e.getModConfigurationDirectory(), s)); }
	
	// Proxy methods //
	
	public static IChatComponent getChatComponent(Object o)
	{ return (o != null && o instanceof IChatComponent) ? (IChatComponent)o : new ChatComponentText("" + o); }
	
	/** Prints message to chat (doesn't translate it) */
	public static void printChat(ICommandSender ep, Object o)
	{
		if(ep == null && isDevEnv) ep = FTBU.proxy.getClientPlayer();
		if(ep != null) ep.addChatMessage(getChatComponent(o));
		else logger.info(o);
	}
	
	public static void printChatAll(Object o)
	{ getServer().getConfigurationManager().sendChatMsgImpl(getChatComponent(o), true); }
	
	// Registry methods //
	
	public static void addItem(IItemLM i)
	{ addItem((Item)i, i.getItemID()); }
	
	public static void addItem(Item i, String name)
	{ GameRegistry.registerItem(i, name); }
	
	public static void addBlock(Block b, Class<? extends ItemBlock> c, String name)
	{ GameRegistry.registerBlock(b, c, name); }
	
	public static void addBlock(Block b, String name)
	{ addBlock(b, ItemBlock.class, name); }
	
	public static void addTileEntity(Class<? extends TileEntity> c, String s, String... alt)
	{
		if(alt == null || alt.length == 0) GameRegistry.registerTileEntity(c, s);
		else GameRegistry.registerTileEntityWithAlternatives(c, s, alt);
	}
	
	public static void addEntity(Class<? extends Entity> c, String s, int id, Object mod)
	{ EntityRegistry.registerModEntity(c, s, id, mod, 50, 1, true); }
	
	public static int getNewEntityID()
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
	
	public static boolean resourceExists(ResourceLocation res)
	{ return LatCoreMC.class.getResource(getPath(res)) != null; }
	
	public static boolean hasOnlinePlayers()
	{ return !MinecraftServer.getServer().getConfigurationManager().playerEntityList.isEmpty(); }
	
	public static FastMap<UUID, EntityPlayerMP> getAllOnlinePlayers()
	{
		FastMap<UUID, EntityPlayerMP> m = new FastMap<UUID, EntityPlayerMP>();
		
		if(!hasOnlinePlayers()) return m;
		
		for(int i = 0; i < getServer().getConfigurationManager().playerEntityList.size(); i++)
		{
			EntityPlayerMP ep = (EntityPlayerMP)getServer().getConfigurationManager().playerEntityList.get(i);
			m.put(ep.getUniqueID(), ep);
		}
		
		return m;
	}
	
	public static EntityPlayerMP getPlayerMP(UUID id)
	{
		if(id == null || !hasOnlinePlayers()) return null;
		return getAllOnlinePlayers().get(id);
	}
	
	public static ChunkCoordinates getSpawnPoint(int dim)
	{
		WorldServer w = DimensionManager.getWorld(dim);
		if(w == null) return null;
		return w.getSpawnPoint();
	}
	
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
	{ MessageLM.sendTo(ep, new MessageNotifyPlayer(n)); }
	
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
			FTBU.proxy.openClientGui(ep, id, data);
	}
	
	public static void openGui(EntityPlayer ep, IGuiTile i, NBTTagCompound data)
	{
		TileEntity te = (TileEntity)i;
		if(data == null) data = new NBTTagCompound();
		data.setIntArray("XYZ", new int[] { te.xCoord, te.yCoord, te.zCoord });
		openGui(ep, FTBUGuiHandler.TILE, data);
	}

	public static UUID getUUIDFromString(String s)
	{
		if(s == null || s.isEmpty()) return null;
		try
		{
			if(s.contains("-")) return UUID.fromString(s);
			else return com.mojang.util.UUIDTypeAdapter.fromString(s);
		}
		catch(Exception e) { }
		return null;
	}
	
	public static String toShortUUID(UUID uuid)
	{
		if(uuid == null) return "";
		return com.mojang.util.UUIDTypeAdapter.fromUUID(uuid);
	}
	
	public static boolean isDedicatedServer()
	{ return getServer().isDedicatedServer(); }
	
	public static String getDimName(World w)
	{ return w.provider.getDimensionName(); }
	
	public static String getDimName(int dim)
	{
		WorldServer w = DimensionManager.getWorld(dim);
		return (w == null) ? "" : getDimName(w);
	}
	
	public static double getWorldScale(World w)
	{
		if(w == null || w.provider.dimensionId == 0) return 1D;
		return 1D / w.provider.getMovementFactor();
	}
	
	public static double getWorldScale(int dim)
	{
		if(dim == 0) return 1D;
		return getWorldScale(DimensionManager.getWorld(dim));
	}
}