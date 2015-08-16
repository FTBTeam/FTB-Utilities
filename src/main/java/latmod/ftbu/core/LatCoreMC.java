package latmod.ftbu.core;
import java.io.File;
import java.util.*;
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
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.*;

import org.apache.logging.log4j.*;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.relauncher.Side;

/** Made by LatvianModder */
public final class LatCoreMC // LatCoreMCClient
{
	public static final String MC_VERSION = Loader.MC_VERSION;
	public static final String DEV_VERSION = "@VERSION@";
	
	public static final Logger logger = LogManager.getLogger("FTBUtilities");
	public static final Random rand = new Random();
	
	public static final boolean isDevEnv = FTBUFinals.VERSION.equals(DEV_VERSION);
	
	public static final String FORMATTING = "\u00a7";
	public static final Pattern textFormattingPattern = Pattern.compile("(?i)" + FORMATTING + "[0-9A-FK-OR]");
	
	public static File latmodFolder = null;
	public static File configFolder = null;
	
	public static final Configuration loadConfig(FMLPreInitializationEvent e, String s)
	{ return new Configuration(new File(e.getModConfigurationDirectory(), s)); }
	
	public static IChatComponent getChatComponent(Object o)
	{ return (o != null && o instanceof IChatComponent) ? (IChatComponent)o : new ChatComponentText("" + o); }
	
	/** Prints message to chat (doesn't translate it) */
	public static void printChat(ICommandSender ep, Object o)
	{
		if(ep == null && isDevEnv) ep = FTBU.proxy.getClientPlayer();
		if(ep != null) ep.addChatMessage(getChatComponent(o));
		else logger.info(o);
	}
	
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
	
	@SuppressWarnings("unchecked")
	public static FastList<EntityPlayerMP> getAllOnlinePlayers()
	{
		FastList<EntityPlayerMP> l = new FastList<EntityPlayerMP>();
		if(hasOnlinePlayers()) l.addAll(getServer().getConfigurationManager().playerEntityList);
		return l;
	}
	
	public static FastMap<UUID, EntityPlayerMP> getAllOnlinePlayersMap()
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
		if(!hasOnlinePlayers()) return null;
		
		for(int i = 0; i < getServer().getConfigurationManager().playerEntityList.size(); i++)
		{
			EntityPlayerMP ep = (EntityPlayerMP)getServer().getConfigurationManager().playerEntityList.get(i);
			if(ep.getUniqueID().equals(id)) return ep;
		}
		
		return null;
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
	
	public static WorldServer getServerWorld()
	{
		MinecraftServer ms = getServer();
		if(ms == null || ms.worldServers.length == 0) return null;
		return ms.worldServers[0];
	}
	
	public static Exception runCommand(ICommandSender ics, String s)
	{
		try { getServer().getCommandManager().executeCommand(ics, s); }
		catch(Exception e) { return e; } return null;
	}
	
	public static Exception runCommand(ICommandSender ics, String cmd, String[] args)
	{ return runCommand(ics, cmd + " " + LMStringUtils.unsplit(args, " ")); }
	
	public static void notifyPlayer(EntityPlayerMP ep, Notification n)
	{ LMNetHelper.sendTo(ep, new MessageNotifyPlayer(n)); }
	
	public static Object invokeStatic(String className, String methodName) throws Exception
	{ Class<?> c = Class.forName(className); return c.getMethod(methodName).invoke(null); }
	
	public static void openGui(EntityPlayer ep, IGuiTile i, NBTTagCompound data)
	{
		TileEntity te = (TileEntity)i;
		if(data == null) data = new NBTTagCompound();
		data.setIntArray("XYZ", new int[] { te.xCoord, te.yCoord, te.zCoord });
		FTBUGuiHandler.instance.openGui(ep, FTBUGuiHandler.TILE, data);
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
	{ return getServer() != null && getServer().isDedicatedServer(); }

	public static IChatComponent setColor(EnumChatFormatting e, IChatComponent c)
	{ c.getChatStyle().setColor(e); return c; }
}